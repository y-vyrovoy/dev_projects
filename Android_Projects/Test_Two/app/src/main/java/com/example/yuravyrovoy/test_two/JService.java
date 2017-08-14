package com.example.yuravyrovoy.test_two;

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


    private static int nStartCounter = 0;

    @Override
    public boolean onStartJob(JobParameters jobParameters) {

        Log.i(TAG, "onStartJob()");

        startService(new Intent(this, MyService.class).putExtra(MyService.PARAM_START_ID, nStartCounter));

        saveMessageToLog( "onStartJob() " + Integer.toString(nStartCounter));

        nStartCounter++;

        jobFinished(jobParameters, true);
        return false;
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

                    String sToWrite = "\r\n" + android.text.format.DateFormat
                            .format("dd.MM.yyyy kk:mm:ss", Calendar.getInstance()) +
                            " : " + sMessage;

                    File myFile = new File(getExternalFilesDir(null), "Fire_Test_(job service #2).log");

                    BufferedWriter bw = new BufferedWriter(new FileWriter(myFile, true));
                    bw.append(sToWrite);
                    bw.close();

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


    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }




}