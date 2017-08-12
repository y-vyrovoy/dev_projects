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
    public static final String PARAM_MESSAGE = TAG + "[message]";
    public static final String REPLY_ACTION = TAG + "[reply_action]";


    public static EditText editDelay;
    public static EditText editJobScheduleDelay;
    public static TextView textMessage;

    private int JOB_SERVICE_ID = 122;

    private BroadcastReceiver receiverMessage = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        editDelay = (EditText)findViewById(R.id.editDelay);
        editJobScheduleDelay = (EditText)findViewById(R.id.editDelayJobSchedule);
        textMessage = (TextView)findViewById(R.id.textOutput);

        // Broadcast receiver setup
        IntentFilter intentFilter = new IntentFilter(REPLY_ACTION);

        receiverMessage = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String intentMessage = intent.getStringExtra(PARAM_MESSAGE);
                AddMessage(intentMessage);
            }
        };
        LocalBroadcastManager.getInstance(this).registerReceiver(receiverMessage, intentFilter);

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable throwable) {

            }
        });
    }

    @Override
    protected  void onDestroy(){
        super.onDestroy();

        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiverMessage);
    }


    public void onBtnSetDelay(View view){

        int nDelay = -1;
        try {
            nDelay = Integer.parseInt(editDelay.getText().toString());
        }
        catch (Exception ex){
            ex.printStackTrace();
        }

        if(nDelay > 0) {
            Intent intentComm = new Intent(this, CommService.class);
            intentComm.putExtra(CommService.MSG_DELAY, nDelay);
            startService(intentComm);
        }
    }

    public void onBtnScheduleJob(View view){

        long checkupInterval = -1;

        try {
            checkupInterval = Integer.parseInt(editJobScheduleDelay.getText().toString());
        }catch (Exception ex){
            ex.printStackTrace();
        }

        JobInfo.Builder builder = new JobInfo.Builder(JOB_SERVICE_ID,
                                                new ComponentName(this, PeriodicJobService.class))
                                        //.setPeriodic(checkupInterval)
                                        .setMinimumLatency(checkupInterval)
                                        .setPersisted(true);

        JobScheduler tm = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        tm.schedule(builder.build());
    }

    public void onBtnThrowException(View view){
        LocalBroadcastManager.getInstance(this)
                .sendBroadcast(new Intent(CommService.MSG_DIE));
    }

    public void onBtnStopCommService (View view) {
        LocalBroadcastManager.getInstance(this)
                .sendBroadcast(new Intent(CommService.MSG_STOP));
    }

    public void onBtnStopPeriodicService(View view) {
        //TODO stop periodic service

    }

    private void AddMessage(String sMessage){
        textMessage.setText(sMessage + "\r\n" + textMessage.getText());
    }

}
