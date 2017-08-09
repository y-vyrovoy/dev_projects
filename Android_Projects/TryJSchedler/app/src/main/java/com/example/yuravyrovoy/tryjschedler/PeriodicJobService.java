package com.example.yuravyrovoy.tryjschedler;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class PeriodicJobService extends JobService {

    private static final String TAG = PeriodicJobService.class.getSimpleName();
    private static final String SERVICE_THREAD_NAME = "Thread [" + TAG + "]";

    private static final int CMD_WAIT_ANSWER = 1;

    public static final int WAKEUP_TIMEOUT = 1000;

    public static final String MSG_WAKEUP_ANSWER = TAG + "[wakeup_answer]";


    private long pingRequestTime;
    private boolean bWaitForWakeupAnswer;

    private ServiceHandler mServiceHandler;

    private JobParameters mJobParameters;

    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {

        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what)
            {
                case CMD_WAIT_ANSWER:

                    Log.i(TAG, "handling CMD_WAIT_ANSWER message" );

                    if(bWaitForWakeupAnswer == true) {

                        if (System.currentTimeMillis() - pingRequestTime > WAKEUP_TIMEOUT) {
                            Log.i(TAG, "> PING_TIMEOUT" );

                            bWaitForWakeupAnswer = false;
                            RestartService();

                            if(mJobParameters != null) {
                                jobFinished(mJobParameters, true);
                            }
                        }
                        else {
                            Message msgReWait = Message.obtain(null, CMD_WAIT_ANSWER, 0, 0);
                            mServiceHandler.sendMessage(msgReWait);
                        }
                    }

                    break;

                default:
                    Log.i(TAG, "handle unknown message #" + Integer.toString(msg.what) );
                    break;
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.i(TAG, "Periodic Service. onCreate");

        HandlerThread handlerThread = new HandlerThread(SERVICE_THREAD_NAME, Process.THREAD_PRIORITY_BACKGROUND);
        handlerThread.start();
        mServiceHandler = new ServiceHandler(handlerThread.getLooper());

        LocalBroadcastManager.getInstance(this).registerReceiver(
                new BroadcastReceiver() {

                    @Override
                    public void onReceive(Context context, Intent intent) {
                        Log.i(TAG, "local broadcast: " +
                                        MSG_WAKEUP_ANSWER +
                                        "[" + Integer.toString(intent.getIntExtra("counter", -1)) + "]");

                        bWaitForWakeupAnswer = false;

                        if(mJobParameters != null) {
                            jobFinished(mJobParameters, true);
                        }

                    }
                }, new IntentFilter(MSG_WAKEUP_ANSWER));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return START_STICKY;
    }

    private  static int nCounter = 0;
    private static int nWakeupRequestCounter = 0;

    @Override
    public boolean onStartJob(JobParameters jobParameters) {

        Log.i(TAG, "Periodic Job started #" + Integer.toString(nCounter++) );

        pingRequestTime = System.currentTimeMillis();
        bWaitForWakeupAnswer = true;

        mJobParameters = jobParameters;

        Message msg = Message.obtain(null, CMD_WAIT_ANSWER, 0, 0);
        mServiceHandler.sendMessage(msg);

        LocalBroadcastManager.getInstance(this)
                .sendBroadcast(new Intent(CommService.MSG_WAKEUP)
                        .putExtra("counter", nWakeupRequestCounter++));

        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        Log.i(TAG, "Periodic Job stopped");
        return true;
    }

    private void RestartService(){

        startActivity(new Intent(this, MainActivity.class));

        Intent intentComm = new Intent(PeriodicJobService.this, CommService.class);
        intentComm.putExtra(CommService.MSG_RESTART, true);
        stopService(intentComm);
        startService(intentComm);



    }

}
