package com.example.yuravyrovoy.testjs;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    private static final int JOB_SERVICE_ID = 1;

    private EditText textJobScheduleDelay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textJobScheduleDelay = (EditText)findViewById(R.id.edtDelay);
    }

    public void onBtnStart(View v){

        long nRefreshInterval = Integer.parseInt(textJobScheduleDelay.getText().toString());

        JobInfo.Builder builder = new JobInfo.Builder(JOB_SERVICE_ID,
                                                        new ComponentName(getPackageName(),
                                                                            MyJobService.class.getName()));

        builder.setPeriodic(nRefreshInterval);

        JobScheduler tm = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        tm.schedule(builder.build());

    }

    public void onBtnStop(View v){

        JobScheduler tm = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        tm.cancelAll();
    }

}
