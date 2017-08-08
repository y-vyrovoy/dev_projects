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
    private ComponentName mServiceComponent;

    private EditText textJobScheduleDelay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textJobScheduleDelay = (EditText)findViewById(R.id.edtDelay);
        mServiceComponent = new ComponentName(this, MyJobService.class);
    }

    public void onBtnStart(View v){

        JobInfo.Builder builder = new JobInfo.Builder(JOB_SERVICE_ID, mServiceComponent);

        long  REFRESH_INTERVAL = Integer.parseInt(textJobScheduleDelay.getText().toString());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            builder.setMinimumLatency(REFRESH_INTERVAL);
        } else {
            builder.setPeriodic(REFRESH_INTERVAL);
        }

        PersistableBundle bundle = new PersistableBundle();
        bundle.putBoolean(MyJobService.PRM_RESTART, true);


        JobScheduler tm = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        tm.schedule(builder.build());

    }

    public void onBtnStop(View v){

        JobScheduler tm = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        tm.cancel(JOB_SERVICE_ID);
    }

}
