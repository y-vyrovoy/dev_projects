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



    @Override
    public void onCreate() {
        super.onCreate();

        saveMessage(TAG + ". onCreate()");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return START_STICKY;
    }

    private  static int nCounter = 0;

    @Override
    public boolean onStartJob(JobParameters jobParameters) {

        saveMessage(TAG + ". Periodic Job started #" + Integer.toString(nCounter++));

        WakeupCommService();
        jobFinished(jobParameters, true);

        return false;
    }


    @Override
    public boolean onStopJob(JobParameters jobParameters) {

        return true;
    }

    private static int nWakeupCounter = 0;

    private void WakeupCommService(){

        saveMessage(TAG + ". Waking up CommService #" + Integer.toString(nWakeupCounter++));

        Intent intentComm = new Intent(PeriodicJobService.this, CommService.class);
        intentComm.putExtra(CommService.MSG_WAKEUP, true);
        startService(intentComm);
    }

    private void saveMessage(String sMessage){
        LocalBroadcastManager.getInstance(this)
                .sendBroadcast(new Intent(CommService.MSG_SAVE_MESSAGE)
                        .putExtra(CommService.PARAM_MESSAGE, sMessage));
    }
}
