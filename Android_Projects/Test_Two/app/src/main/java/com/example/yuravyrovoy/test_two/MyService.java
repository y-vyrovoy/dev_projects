package com.example.yuravyrovoy.test_two;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;

public class MyService extends Service {

    public static final String TAG = MyService.class.getSimpleName();
    public static final String PARAM_START_ID = TAG + "[param_start_id]";
    private static int nCounter = 0;

    @Override
    public int onStartCommand (Intent intent, int flags, int startId)
    {
        int nID = intent.getIntExtra(PARAM_START_ID, -1);

        saveMessageToLog("onHandleIntent() #" + Integer.toString(nID));
        return START_REDELIVER_INTENT;
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

                    File myFile = new File(getExternalFilesDir(null), "Fire_Test_two (START_REDELIVER_INTENT).log");

                    BufferedWriter bw = new BufferedWriter(new FileWriter(myFile, true));
                    bw.append(sToWrite);
                    bw.close();

                    Toast.makeText(MyService.this, sToWrite, Toast.LENGTH_SHORT).show();

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
    public IBinder onBind(Intent intent) {
        return null;
    }


}
