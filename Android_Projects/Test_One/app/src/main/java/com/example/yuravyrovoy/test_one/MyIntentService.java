package com.example.yuravyrovoy.test_one;

import android.app.IntentService;
import android.content.Intent;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class MyIntentService extends IntentService {


    public MyIntentService() {
        super("MyIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        saveMessageToLog("onHandleIntent() #" + Integer.toString(nCounter++));
    }

    private static int nCounter = 0;

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

                    File myFile = new File(getExternalFilesDir(null), "Fire02.log");

                    BufferedWriter bw = new BufferedWriter(new FileWriter(myFile, true));
                    bw.append(sToWrite);
                    bw.close();

                    Toast.makeText(MyIntentService.this, sToWrite, Toast.LENGTH_SHORT).show();

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
