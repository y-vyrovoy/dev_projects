package com.example.yuravyrovoy.test_one;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

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

    private static final String TAG = MyIntentService.class.getSimpleName();
    boolean bDie = false;

    public MyIntentService() {
        super("MyIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Intent notificationIntent = new Intent(this, MyIntentService.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        builder.setContentTitle("setContentTitle")
                .setContentText("let's dance")
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .setContentIntent(pendingIntent)
                .setTicker("setTicker");

        startForeground(2208, builder.build());

        if(intent.getAction() == "action_die"){
            bDie = true;
        }
    }

    private static int nCounter = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "OnCreate");

        bDie = false;

        new Thread(new Runnable() {

            @Override
            public void run() {

                while (true){

                    if(bDie == true){
                        die();
                    }

                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    String sMessage = "thread loop #" + nCounter++;
                    //saveMessageToLog(sMessage);
                    Log.i(TAG, sMessage);
                }

            }
        }).start();

        //JService.scheduleService(this, 5000);

    }


    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy()");

        super.onDestroy();
    }

    private void die(){
        throw new RuntimeException("Testing unhandled exception processing.");
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

                    File myFile = new File(getExternalFilesDir(null), "Fire02.log");

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

}
