package com.example.yuravyrovoy.tryjschedler;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String MSG_MESSAGE = TAG + "[message]";
    public static final String REPLY_ACTION = TAG + "[reply_action]";

    public static EditText editDelay;
    public static EditText editJobScheduleDelay;
    public static TextView textMessage;

    private int JOB_SERVICE_ID = 122;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editDelay = (EditText)findViewById(R.id.editDelay);
        editJobScheduleDelay = (EditText)findViewById(R.id.editDelayJobSchedule);
        textMessage = (TextView)findViewById(R.id.textOutput);


        // Broadcast receiver setup
        IntentFilter intentFilter = new IntentFilter(REPLY_ACTION);

        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String intentMessage = intent.getStringExtra(MSG_MESSAGE);
                AddMessage(intentMessage);
            }
        }, intentFilter);

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable throwable) {

            }
        });
    }

    public void onBtnSetDelay(View view){

        int nDelay = Integer.parseInt(editDelay.getText().toString());

        Intent intentComm = new Intent(this, CommService.class);
        intentComm.putExtra(CommService.MSG_DELAY, nDelay);
        startService(intentComm);
    }


    public void onBtnScheduleJob(View view){

        long checkupInterval = Integer.parseInt(editJobScheduleDelay.getText().toString());

        JobInfo.Builder builder = new JobInfo.Builder(JOB_SERVICE_ID,
                                                new ComponentName(this, PeriodicJobService.class));

        //builder.setPeriodic(checkupInterval);
        builder.setMinimumLatency(checkupInterval);

        JobScheduler tm = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        tm.schedule(builder.build());

    }

    public void onBtnThrowException(View view){
        Intent intentComm = new Intent(this, CommService.class);
        intentComm.putExtra(CommService.MSG_DIE, true);
        startService(intentComm);
    }

    private void AddMessage(String sMessage){
        textMessage.setText(sMessage + "\r\n" + textMessage.getText());
    }


    public void onBtnStopCommService (View view) {
        Intent intentComm = new Intent(this, CommService.class);
        intentComm.putExtra(CommService.MSG_STOP, true);
        startService(intentComm);
    }
}
