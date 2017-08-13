package com.example.yuravyrovoy.test_one;

import android.app.job.JobParameters;
import android.app.job.JobService;
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

    public JService() {
    }


    private static int nCounter = 0;

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




}
