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

    private static final int MSG_WAKEUP = 1;


    private ServiceHandler mServiceHandler;

    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {

        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what)
            {
                case MSG_WAKEUP:
                    Log.i(TAG, "MSG_WAKEUP message handled" );

                    Intent intentComm = new Intent(PeriodicJobService.this, CommService.class);
                    stopService(intentComm);

                    intentComm.putExtra(CommService.CMD_WAKEUP, true);
                    startService(intentComm);

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

        Log.i(TAG, "Service created");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "Service destroyed");
    }


    public PeriodicJobService() {
    }

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.i(TAG, "Job started");

        Message msg = Message.obtain(null, MSG_WAKEUP, 0, 0);
        mServiceHandler.sendMessage(msg);

        jobFinished(jobParameters, true);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        Log.i(TAG, "Job stopped");
        return true;
    }




}
