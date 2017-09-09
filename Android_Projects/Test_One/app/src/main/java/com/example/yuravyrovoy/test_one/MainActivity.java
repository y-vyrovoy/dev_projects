package com.example.yuravyrovoy.test_one;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
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
        startService(new Intent(this, MyIntentService.class));
    }

    public void onBtnSchedule(View v) {

        int nDelay = Integer.parseInt (editDelay.getText().toString());
        JService.scheduleService(this, nDelay);

    }

    public void onBtnUnchedule(View v) {
        JService.unscheduleService(this);
    }

    public void onBtnKillActivity(View v) {
        die();
    }

    public void onBtnKillThread(View v) {
        startService(new Intent(this, MyIntentService.class)
                        .setAction("action_die"));
    }

    private void die(){
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
