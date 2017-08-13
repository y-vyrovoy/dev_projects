package com.example.yuravyrovoy.tryintentservice;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
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
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class WorkIntentService extends IntentService {

    public static final String TAG = WorkerThread.class.getSimpleName();
    public static final int FOREGROUND_ID = 333;

    public static final String ACTION_START = TAG + "[action_start]";
    public static final String ACTION_STOP = TAG + "[action_stop]";
    public static final String ACTION_WAKEUP = TAG + "[action_wakeup]";

    public static final String MSG_STOP = TAG + "[message_stop]";
    public static final String MSG_KILL_THREAD = TAG + "[message_kill_thread]";
    public static final String MSG_KILL_SERVICE = TAG + "[message_kill_service]";

    private static int nCounter = 0;
    private static long TIMEOUT = 1000;
    private static long DELAY = 500;
    long lLastCall_ms = 0;

    public WorkIntentService() {
        super("WorkIntentService");
    }

    WorkerThread workerThread;

    @Override
    protected void onHandleIntent(Intent intent) {

        if (intent == null) {
            return;
        }

        String sAction = intent.getAction();


        if(sAction == ACTION_START){

            if(workerThread == null) {
                workerThread = new WorkerThread();
                workerThread.start();
            }

        }
        else  if(sAction == ACTION_STOP){

            if(workerThread == null) {

                LocalBroadcastManager.getInstance(this)
                        .sendBroadcast(new Intent(MSG_STOP));
            }
        }
        else  if(sAction == ACTION_WAKEUP){

            if(System.currentTimeMillis() - lLastCall_ms >= TIMEOUT) {

                Log.i(TAG, "TIMEOUT");

                if(workerThread == null) {
                    workerThread = new WorkerThread();
                    workerThread.start();
                }
            }
        }
    }

    private class WorkerThread extends Thread{

        public boolean bRun;
        public boolean bKillThread = false;

        public void run() {

            lLastCall_ms = System.currentTimeMillis();
            bRun = true;

            registerBroadcastReceivers();

            while (bRun){

                if(bKillThread == true){
                    die();
                }

                if( System.currentTimeMillis() - lLastCall_ms > DELAY ){
                    lLastCall_ms = System.currentTimeMillis();
                    String sMessage = "next #" + Integer.toString(nCounter++);

                    Log.i(TAG, sMessage);
                    saveMessageToLog(sMessage);
                }

            }

        }

        private void registerBroadcastReceivers(){

            LocalBroadcastManager.getInstance(WorkIntentService.this).registerReceiver(
                    new BroadcastReceiver() {
                        @Override
                        public void onReceive(Context context, Intent intent) {

                            bRun = false;
                        }
                    },  new IntentFilter(MSG_STOP));

            LocalBroadcastManager.getInstance(WorkIntentService.this).registerReceiver(
                    new BroadcastReceiver() {
                        @Override
                        public void onReceive(Context context, Intent intent) {

                            bKillThread = true;
                        }
                    },  new IntentFilter(MSG_KILL_THREAD));

            LocalBroadcastManager.getInstance(WorkIntentService.this).registerReceiver(
                    new BroadcastReceiver() {
                        @Override
                        public void onReceive(Context context, Intent intent) {

                            die();
                        }
                    },  new IntentFilter(MSG_KILL_SERVICE));

        }

        private void die(){
            throw new RuntimeException("Testing unhandled exception processing.");
        }

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
