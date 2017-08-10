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

    public static final int WAKEUP_TIMEOUT = 3000;

    public static final String MSG_WAKEUP_ANSWER = TAG + "[wakeup_answer]";
    public static final String PARAM_COUNTER = TAG + "[counter]";


    private long pingRequestTime;
    private boolean bWaitForWakeupAnswer;

    private ServiceHandler mServiceHandler;
    private BroadcastReceiver receiverWakeupAnswer;

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

                    if(bWaitForWakeupAnswer == true) {

                        if (System.currentTimeMillis() - pingRequestTime > WAKEUP_TIMEOUT) {
                            saveMessage(TAG + ". WAKEUP_TIMEOUT" );

                            bWaitForWakeupAnswer = false;
                            RestartService();

                            postWakeup();
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

        saveMessage(TAG + ". onCreate()");

        registerWakeupReceiver();

        HandlerThread handlerThread = new HandlerThread(SERVICE_THREAD_NAME, Process.THREAD_PRIORITY_BACKGROUND);
        handlerThread.start();
        mServiceHandler = new ServiceHandler(handlerThread.getLooper());
    }

    @Override
    public void onDestroy (){



        Log.i(TAG, "service destroyed");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return START_STICKY;
    }

    private  static int nCounter = 0;
    private static int nWakeupRequestCounter = 0;

    @Override
    public boolean onStartJob(JobParameters jobParameters) {

        saveMessage(TAG + ". Periodic Job started #" + Integer.toString(nCounter++));

        pingRequestTime = System.currentTimeMillis();
        bWaitForWakeupAnswer = true;

        mJobParameters = jobParameters;


        Message msg = Message.obtain(null, CMD_WAIT_ANSWER, 0, 0);
        mServiceHandler.sendMessage(msg);

        LocalBroadcastManager.getInstance(this)
                .sendBroadcast(new Intent(CommService.MSG_WAKEUP)
                        .putExtra(PARAM_COUNTER, nWakeupRequestCounter++));

        return false;
    }

    private void postWakeup(){

        LocalBroadcastManager.getInstance(PeriodicJobService.this).unregisterReceiver(receiverWakeupAnswer);

        if(mJobParameters != null) {
            jobFinished(mJobParameters, true);
        }
    }

    private void registerWakeupReceiver(){

        receiverWakeupAnswer = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {

                saveMessage(TAG + "<-" + MSG_WAKEUP_ANSWER +
                        "[" + Integer.toString(intent.getIntExtra(PARAM_COUNTER, -1)) + "]");

                bWaitForWakeupAnswer = false;

                postWakeup();

            }
        };
        //broadcastReceiverManager.registerReceiver(receiverWakeupAnswer, MSG_WAKEUP_ANSWER);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiverWakeupAnswer, new IntentFilter(MSG_WAKEUP_ANSWER));
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {

        return true;
    }

    private void RestartService(){

        saveMessage(TAG + ". Restarting CommService");

        Intent intentComm = new Intent(PeriodicJobService.this, CommService.class);
        intentComm.putExtra(CommService.MSG_DELAY, 1234);
        stopService(intentComm);
        startService(intentComm);
    }

    private void saveMessage(String sMessage){
        LocalBroadcastManager.getInstance(this)
                .sendBroadcast(new Intent(CommService.MSG_SAVE_MESSAGE)
                        .putExtra(CommService.PARAM_MESSAGE, sMessage));
    }
}
