package com.example.yuravyrovoy.test_one;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;

public class JService extends JobService {

    private static final String TAG = JService.class.getSimpleName();
    private static final int JOB_SCHEDULE_ID = 1;

    public JService() {
    }


    @Override
    public boolean onStartJob(JobParameters jobParameters) {

        Log.i(TAG, "onStartJob()");

        startService(new Intent(this, MyIntentService.class));

        jobFinished(jobParameters, true);
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }


    public static void scheduleService(Context context, long millisecPeriod){
        JobInfo.Builder builder =
                new JobInfo.Builder(JOB_SCHEDULE_ID,
                        new ComponentName(context, JService.class.getName()))
                        .setPeriodic(millisecPeriod)
                        .setPersisted(true);

        JobScheduler tm = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        tm.schedule(builder.build());
    }

    public static void unscheduleService(Context context){

        JobScheduler tm = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        tm.cancel(JOB_SCHEDULE_ID);

    }

}
