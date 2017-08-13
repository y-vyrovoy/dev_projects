package com.example.yuravyrovoy.tryintentservice;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable throwable) {

            }
        });
    }

    public void onBtnStart(View v){
        startService(new Intent(this, WorkIntentService.class)
                        .setAction(WorkIntentService.ACTION_START));
    }

    public void onBtnStop(View v){
        LocalBroadcastManager.getInstance(null)
                .sendBroadcast(new Intent(WorkIntentService.MSG_STOP));
    }

    public void onBtnKillThread(View v){
        LocalBroadcastManager.getInstance(null)
                .sendBroadcast(new Intent(WorkIntentService.MSG_KILL_THREAD));
    }

    public void onBtnKillService(View v){
        LocalBroadcastManager.getInstance(null)
                .sendBroadcast(new Intent(WorkIntentService.MSG_KILL_SERVICE));
    }

    public void onBtnSetJobScheduler(View v) {
        JobInfo.Builder builder = new JobInfo.Builder(134,
                new ComponentName(this, WakeupPeriodicJobService.class))
                //.setPeriodic(900_000)
                .setMinimumLatency(10_000)
                .setPersisted(true);

        JobScheduler tm = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        tm.schedule(builder.build());
    }

}
