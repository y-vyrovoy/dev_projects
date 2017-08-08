package com.example.yuravyrovoy.tryjschedler;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String MSG_MESSAGE = TAG + "[message]";
    public static final String REPLY_ACTION = TAG + "[reply_action]";

    public static EditText textDelay;
    public static EditText textJobScheduleDelay;

    private ComponentName mServiceComponent;
    private int mJobId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textDelay = (EditText)findViewById(R.id.textDelay);
        textJobScheduleDelay = (EditText)findViewById(R.id.textDelayJobSchedule);
        mServiceComponent = new ComponentName(this, PeriodicJobService.class);

        // Broadcast receiver setup
        IntentFilter intentFilter = new IntentFilter(REPLY_ACTION);

        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String intentMessage = intent.getStringExtra(MSG_MESSAGE);
                AddMessage(intentMessage);
            }
        }, intentFilter);
    }

    public void onBtnSetDelay(View view){

        int nDelay = Integer.parseInt(textDelay.getText().toString());

        Intent intentComm = new Intent(this, CommService.class);
        intentComm.putExtra(CommService.MSG_DELAY, nDelay);
        startService(intentComm);
    }


    public void onBtnScheduleJob(View view){

        JobInfo.Builder builder = new JobInfo.Builder(mJobId, mServiceComponent);

        long  REFRESH_INTERVAL = Integer.parseInt(textJobScheduleDelay.getText().toString());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            builder.setMinimumLatency(REFRESH_INTERVAL);
        } else {
            builder.setPeriodic(REFRESH_INTERVAL);
        }

        JobScheduler tm = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        tm.schedule(builder.build());
    }

    public void onBtnThrowException(View view){
        Intent intentComm = new Intent(this, CommService.class);
        intentComm.putExtra(CommService.MSG_DIE, true);
        startService(intentComm);
    }

    private void AddMessage(String intentMessage){
        Log.i(TAG, intentMessage);
    }


    public void onBtnStopCommService (View view) {


    }
}
