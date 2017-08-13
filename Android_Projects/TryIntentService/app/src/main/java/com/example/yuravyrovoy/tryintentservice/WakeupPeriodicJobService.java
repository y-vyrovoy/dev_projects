package com.example.yuravyrovoy.tryintentservice;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;

public class WakeupPeriodicJobService extends JobService {

    private static final String TAG = WakeupPeriodicJobService.class.getSimpleName();



    @Override
    public void onCreate() {
        super.onCreate();

        saveMessage(TAG + ". onCreate()");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return START_STICKY;
    }

    private  static int nCounter = 0;

    @Override
    public boolean onStartJob(JobParameters jobParameters) {

        saveMessage(TAG + ". Periodic Job started #" + Integer.toString(nCounter++));

        //WakeupCommService();
        jobFinished(jobParameters, true);

        return false;
    }

    private JobParameters parametersJob;

    @Override
    public boolean onStopJob(JobParameters jobParameters) {

        parametersJob = jobParameters;
        return true;
    }

    private static int nWakeupCounter = 0;

    private void WakeupCommService(){

        saveMessage(TAG + ". Waking up CommService #" + Integer.toString(nWakeupCounter++));

        //Intent intentComm = new Intent(WakeupPeriodicJobService.this, WorkIntentService.class);
        //intentComm.setAction(WorkIntentService.ACTION_WAKEUP);
        //startService(intentComm);
    }

    private void saveMessage(String sMessage){

        Log.i(TAG, sMessage);
        saveMessageToLog(sMessage);
/*
        LocalBroadcastManager.getInstance(this)
                .sendBroadcast(new Intent(WorkIntentService.MSG_SAVE_MESSAGE)
                        .putExtra(WorkIntentService.PARAM_MESSAGE, sMessage));
*/
    }

    public void saveMessageToLog(String sMessage){

        class myRunnable implements Runnable{

            private String sMessage;

            public  myRunnable(String message){
                sMessage = message;
            }

            @Override
            public void run() {

                try {

                    String sToWrite = "\r\n" + android.text.format.DateFormat.format("dd.MM.yyyy kk:mm:ss", Calendar.getInstance()) +
                            " : " + sMessage;

                    File myFile = new File(getExternalFilesDir(null), "TryCommRestoring.log");

                    BufferedWriter bw = new BufferedWriter(new FileWriter(myFile, true));
                    bw.append(sToWrite);
                    bw.close();

                    Toast.makeText(WakeupPeriodicJobService.this, sToWrite, Toast.LENGTH_SHORT).show();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        myRunnable logSaver = new myRunnable(sMessage);

        new Thread(logSaver).run();
    }
}
