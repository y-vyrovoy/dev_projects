package com.example.yuravyrovoy.tryjschedler;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class CommService extends Service {

    private static final String TAG = CommService.class.getSimpleName();

    public static final int MSG_CHANGE_DELAY = 1;
    public static final int MSG_NEXT_ITERATION = 2;
    public static final int MSG_WAKEUP = 3;
    public static final int MSG_DIE = 4;

    public static final String CMD_DELAY = "delay";
    public static final String CMD_DIE = "die";
    public static final String CMD_WAKEUP = "wake_up";


    private static final String SERVICE_THREAD_NAME = "Thread [" + TAG + "]";
    public static final String REPLY_ACTION = "com.example.yuravyrovoy.tryservice.WonderService.WONDER_REPLY";

    private ServiceHandler mServiceHandler;
    private int mCounter;
    private int mDelay;

    private HandlerThread handlerThread;

    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {

        public ServiceHandler(Looper looper) {
            super(looper);
            mCounter = 0;
            mDelay = -1;
        }



        @Override
        public void handleMessage(Message msg) {

            switch (msg.what)
            {
                case MSG_CHANGE_DELAY:

                    Log.i(TAG, "Change delay: " + Integer.toString(mDelay));

                    boolean bStart = mDelay< 1 ? true : false;
                    mDelay = msg.arg1;

                    if(mDelay == -2) {
                        Log.i(TAG,  "Try to stop. nDelay = " + msg.arg1 + " | Counter:" + mCounter++);
                        stopSelf();
                        return;
                    }

                    if(bStart == true){
                        Message msgNew = Message.obtain(null, CommService.MSG_NEXT_ITERATION, mDelay, 0);
                        sendMessage(msgNew);
                        Log.i(TAG, "Send next iteration: " + Integer.toString(mDelay));
                    }

                    break;

                case MSG_NEXT_ITERATION:

                    Log.i(TAG, "Next iteration: " + Integer.toString(mDelay));

                    try {
                        Thread.sleep(mDelay);
                    } catch (InterruptedException e) {
                        // Restore interrupt status.
                        Thread.currentThread().interrupt();
                    }


                    Intent intent = new Intent(REPLY_ACTION);
                    intent.putExtra("message", "Delay finished: " + Integer.toString(mDelay));

                    LocalBroadcastManager.getInstance(CommService.this).sendBroadcast(intent);
                    Log.i(TAG,  "Finished delay:" + msg.arg1 + " | Counter:" + mCounter++);

                    if(mDelay > 0){
                        Message msgNew = Message.obtain(null, MSG_NEXT_ITERATION, mDelay, 0);
                        sendMessage(msgNew);
                    }
                    break;

                case MSG_WAKEUP:

                    Log.i(TAG, "MSG_WAKEUP: mDelay: " + Integer.toString(msg.arg1));

                    mDelay = msg.arg1;

                    Message msgNew = Message.obtain(null, MSG_NEXT_ITERATION, mDelay, 0);
                    sendMessage(msgNew);

                    break;

                case MSG_DIE:
                    die();
                    break;
            }


            // Stop the service using the startId, so that we don't stop
            // the service in the middle of handling another job
            //stopSelf(msg.arg1);
        }
    }



    public CommService() {}

    private void startHandleThread(){
        handlerThread = new HandlerThread(SERVICE_THREAD_NAME, Process.THREAD_PRIORITY_BACKGROUND);
        handlerThread.start();
        mServiceHandler = new ServiceHandler(handlerThread.getLooper());
    }

    @Override
    public void onCreate() {
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();
        startHandleThread();


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "task starting", Toast.LENGTH_SHORT).show();

        boolean bDie = intent.getBooleanExtra(CMD_DIE, false);
        if(bDie == true){
            Log.i(TAG, "CMD_DIE");
            Message msg = Message.obtain(null, CommService.MSG_DIE, 0, startId);
            mServiceHandler.sendMessage(msg);

            return START_STICKY;
        }

        boolean bWakeUp = intent.getBooleanExtra(CMD_WAKEUP, false);
        if(bWakeUp == true){
            Log.i(TAG, "CMD_WAKEUP");

            if( (handlerThread == null) || (handlerThread.isAlive() == false) ){
                startHandleThread();
            }

            Message msg = Message.obtain(null, CommService.MSG_WAKEUP, 1234, startId);
            mServiceHandler.sendMessage(msg);

            return START_STICKY;
        }

        int nDelay = intent.getIntExtra(CMD_DELAY, -1);

        Log.i(TAG, "onStartCommand: " + Integer.toString(mDelay));

        if( nDelay != -1 ){
            Message msg = Message.obtain(null, CommService.MSG_CHANGE_DELAY, nDelay, startId);
            mServiceHandler.sendMessage(msg);
        }

        return START_STICKY;
    }


    private void die(){
        throw new RuntimeException("Testing unhandled exception processing.");
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
