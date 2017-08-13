package com.example.yuravyrovoy.test_one;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    EditText editDelay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editDelay = (EditText)findViewById(R.id.editText);
    }

    public void onBtn(View v) {

        int nDelay = Integer.parseInt (editDelay.getText().toString());

        JobInfo.Builder builder =
                new JobInfo.Builder(2018,
                        new ComponentName(getPackageName(),
                                JService.class.getName()))
                                            //.setMinimumLatency(nDelay)
                                            .setPeriodic(nDelay)
                                            .setPersisted(true);

        JobScheduler tm = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        tm.schedule(builder.build());


    }
}
