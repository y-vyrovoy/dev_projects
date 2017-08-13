package com.example.yuravyrovoy.tryjschedler;

import android.app.IntentService;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.support.annotation.Nullable;
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
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class CommService extends Service {

    private static final String TAG = CommService.class.getSimpleName();

    public static final int CMD_NEXT_ITERATION = 1;
    public static final int CMD_DIE = 3;
    public static final int CMD_SET_DELAY = 4;


    public static final String MSG_DELAY = TAG + "[delay]";
    public static final String MSG_DIE = TAG + "[die]";
    public static final String MSG_WAKEUP = TAG + "[wakeup]";
    public static final String MSG_STOP = TAG + "[stop]";
    public static final String MSG_SAVE_MESSAGE = TAG + "[save_message]";
    public static final String PARAM_MESSAGE = TAG + "[param_message]";

    private static final String SERVICE_THREAD_NAME = TAG + "[thread]";

    private ServiceHandler mServiceHandler;
    private HandlerThread handlerThread;
    private BroadcastReceiver receiverStop;
    private BroadcastReceiver receiverSaveMessage;
    private BroadcastReceiver receiverDie;

    private int mDelayINT = 0;
    private static int nSendMessageID = 0;

    private long cycleStart;
    private long lastHandleMessageCallMS = 0;
    public static long RESPONSE_TIMEOUT = 1000;

    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {

        public ServiceHandler(Looper looper) {
            super(looper);

        }


        @Override
        public void handleMessage(Message msg) {

            // to be able to check whether thread is alive or not
            lastHandleMessageCallMS = System.currentTimeMillis();

            switch (msg.what)
            {
                case CMD_SET_DELAY:

                    mDelayINT = msg.arg1;

                    cycleStart = System.currentTimeMillis();

                    Message msgNew = Message.obtain(null, CMD_NEXT_ITERATION, 0, 0);
                    sendMessage(msgNew);

                break;

                case CMD_NEXT_ITERATION:

                    if(mDelayINT > 0) {

                        if(System.currentTimeMillis() >= cycleStart + mDelayINT)
                        {
                            cycleStart = System.currentTimeMillis();

                            nSendMessageID++;

                            String sMessage = "D = " + Integer.toString(mDelayINT) +
                                    " [msgId = " + Integer.toString(nSendMessageID)  + "]";

                            sendUserMessage("MSG: " + sMessage);
                        }

                    }

                    Message msgNext = Message.obtain(null, CMD_NEXT_ITERATION, 0, 0);
                    sendMessage(msgNext);
                break;

                case CMD_DIE:
                    sendUserMessage(MSG_DIE);

                    die();
                break;


                default:
                    super.handleMessage(msg);
                break;
            }

        }
    }



    public CommService() {
        handlerThread = null;
    }

    private void startHandleThread(){

        boolean bRestartThread = false;

        if( handlerThread == null) {
            bRestartThread = true;
        }
        else if(handlerThread.isAlive() == false){
            handlerThread.interrupt();
            handlerThread.quit();
            bRestartThread = true;
        }

        if(bRestartThread == true){
            handlerThread = new HandlerThread(SERVICE_THREAD_NAME, Process.THREAD_PRIORITY_BACKGROUND);
            handlerThread.start();
            mServiceHandler = new ServiceHandler(handlerThread.getLooper());
        }
    }

    @Override
    public void onCreate() {

        sendUserMessage(TAG + ".onCreate()");

        startHandleThread();
        registerBroadcastManagers();

    }

    @Override
    public void onDestroy (){

        unregisterBroadcastManagers();

        Log.i(TAG, "service destroyed");
    }

    private void registerBroadcastManagers(){


        receiverStop =  new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                sendUserMessage(TAG + "." + MSG_STOP);

                Message msg = Message.obtain(null, CMD_SET_DELAY, 0, 0);
                mServiceHandler.sendMessage(msg);

            }
        };
        LocalBroadcastManager.getInstance(this).registerReceiver(receiverStop, new IntentFilter(MSG_STOP));

        receiverSaveMessage =  new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String sMessage = intent.getStringExtra(PARAM_MESSAGE);

                sendUserMessage(sMessage);
            }
        };
        LocalBroadcastManager.getInstance(this).registerReceiver(receiverSaveMessage, new IntentFilter(MSG_SAVE_MESSAGE));


        receiverDie = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Message msg = Message.obtain(null, CMD_DIE, 0, 0);
                mServiceHandler.sendMessage(msg);

            }
        };
        LocalBroadcastManager.getInstance(this).registerReceiver(receiverDie, new IntentFilter(MSG_DIE));
    }

    private void unregisterBroadcastManagers(){
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiverStop);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiverSaveMessage);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiverDie);

    }

    private static int nStartCommandCounter = 0;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        sendUserMessage("CommService starting #" + Integer.toString(nStartCommandCounter++));

        startHandleThread();

        int nDelay = -1;

        if( intent.getIntExtra(MSG_DELAY, -1) >=0 ) {
            nDelay = intent.getIntExtra(MSG_DELAY, -1);
        }
        else if(intent.getBooleanExtra(MSG_WAKEUP, false) == true){
            if(System.currentTimeMillis() - lastHandleMessageCallMS > RESPONSE_TIMEOUT) {
                nDelay = 5000;
            }
        }

        if(nDelay >= 0) {
            Message msg = Message.obtain(null, CMD_SET_DELAY, nDelay, startId);
            mServiceHandler.sendMessage(msg);
        }

        return START_REDELIVER_INTENT;
    }

    private void die(){
        throw new RuntimeException("Testing unhandled exception processing.");
    }


    public void sendUserMessage(String sMessage){

        saveMessageToLog(sMessage);

        LocalBroadcastManager.getInstance(null)
                .sendBroadcast(new Intent(MainActivity.REPLY_ACTION)
                        .putExtra(MainActivity.PARAM_MESSAGE, sMessage));

        Log.i(TAG, sMessage);

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

                    File myFile = new File(getExternalFilesDir(null), "TryJScheduler.log");

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

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}
