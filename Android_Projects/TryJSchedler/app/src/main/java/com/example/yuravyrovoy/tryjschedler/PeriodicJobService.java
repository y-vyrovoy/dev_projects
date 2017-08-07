package com.example.yuravyrovoy.tryjschedler;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;

public class PeriodicJobService extends JobService {

    private static final String TAG = PeriodicJobService.class.getSimpleName();
    private static final String SERVICE_THREAD_NAME = "Thread [" + TAG + "]";

    private static final int CMD_PING = 1;
    private static final int CMD_WAIT_ANSWER = 2;
    private static final int CMD_PING_ANSWER = 3;

    public static final int PING_TIMEOUT = 1000;

    private long pingRequestTime;
    private boolean bPingSucceeded;

    private ServiceHandler mServiceHandler;

    private JobParameters mJobParameteres;

    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {

        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what)
            {
                case CMD_PING:
                    Log.i(TAG, "CMD_PING message handled" );

                    pingRequestTime = System.currentTimeMillis();
                    bPingSucceeded = false;

                    Intent intentComm = new Intent(PeriodicJobService.this, CommService.class);
                    intentComm.putExtra(CommService.MSG_PING, true);
                    startService(intentComm);

                    Message msgWait = Message.obtain(null, CMD_WAIT_ANSWER, 0, 0);
                    mServiceHandler.sendMessage(msgWait);

                    break;

                case CMD_WAIT_ANSWER:

                    if(bPingSucceeded == false) {

                        if (System.currentTimeMillis() - pingRequestTime > PING_TIMEOUT) {
                            Log.i(TAG, "> PING_TIMEOUT" );
                            RestartService();
                            jobFinished(mJobParameteres, true);
                        }
                        else {
                            Message msgReWait = Message.obtain(null, CMD_WAIT_ANSWER, 0, 0);
                            mServiceHandler.sendMessage(msgReWait);
                        }
                    }
                    else{
                        Log.i(TAG, "bPingSucceeded == true" );
                    }

                    break;

                case CMD_PING_ANSWER:
                    Log.i(TAG, "bPingSucceeded -> true" );
                    bPingSucceeded = true;
                    jobFinished(mJobParameteres, true);
                    break;
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        HandlerThread handlerThread = new HandlerThread(SERVICE_THREAD_NAME, Process.THREAD_PRIORITY_BACKGROUND);
        handlerThread.start();
        mServiceHandler = new ServiceHandler(handlerThread.getLooper());

        Log.i(TAG, "Periodic Service created");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(intent.getBooleanExtra(CommService.MSG_PING_ANSWER, false) == true) {

            Log.i(TAG, "onStartCommand: MSG_PING_ANSWER" );

            Message msgWait = Message.obtain(null, CMD_PING_ANSWER, 0, 0);
            mServiceHandler.sendMessage(msgWait);
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "Periodic Service destroyed");
    }


    public PeriodicJobService() {
    }

    private  static int nCounter = 0;



    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.i(TAG, "Periodic Job started #" + Integer.toString(nCounter++) );
        pingRequestTime = -1;

        Message msg = Message.obtain(null, CMD_PING, 0, 0);
        mServiceHandler.sendMessage(msg);

        mJobParameteres = jobParameters;
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        Log.i(TAG, "Periodic Job stopped");
        return true;
    }

    private void RestartService(){
        Intent intentComm = new Intent(PeriodicJobService.this, CommService.class);
        stopService(intentComm);
        intentComm.putExtra(CommService.MSG_DELAY, 1122);
        startService(intentComm);
    }

}
