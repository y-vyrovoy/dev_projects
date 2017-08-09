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


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class CommService extends Service {

    private static final String TAG = CommService.class.getSimpleName();

    public static final int CMD_NEXT_ITERATION = 1;
    public static final int CMD_ANSWER_WAKEUP = 2;
    public static final int CMD_DIE = 3;
    public static final int CMD_STOP = 4;

    public static final String MSG_DELAY = TAG + "[delay]";
    public static final String MSG_DIE = TAG + "[die]";
    public static final String MSG_RESTART = TAG + "[restart]";
    public static final String MSG_WAKEUP = TAG + "[wakeup]";
    public static final String MSG_STOP = TAG + "[stop]";

    private static final String SERVICE_THREAD_NAME = TAG + "[thread]";


    private ServiceHandler mServiceHandler;
    private int mHandlerId;
    private int mDelay;

    private HandlerThread handlerThread;

    private static int nSendMessageID = 0;

    private static int nWakeupResponseCounter = 0;

    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {

        public ServiceHandler(Looper looper) {
            super(looper);

            mHandlerId = 0;
            mDelay = -1;
        }


        @Override
        public void handleMessage(Message msg) {

            mHandlerId++;

            switch (msg.what)
            {
                case CMD_NEXT_ITERATION:

                    if(mDelay > 0) {
                        try {
                            Thread.sleep(mDelay);
                            nSendMessageID++;
                        } catch (InterruptedException e) {
                            // Restore interrupt status.
                            Thread.currentThread().interrupt();
                        }

                        String sMessage = "D = " + Integer.toString(mDelay) +
                                                " [ msgId =" + Integer.toString(nSendMessageID) + ", " +
                                                "hndlrId =" + Integer.toString(mHandlerId) +"]";

                        Intent intent = new Intent(MainActivity.REPLY_ACTION);
                        intent.putExtra(MainActivity.MSG_MESSAGE, "Message: " + sMessage);

                        LocalBroadcastManager.getInstance(CommService.this).sendBroadcast(intent);
                        //Log.i(TAG, "Log: " + sMessage);
                    }

                    int nNextDelay = mDelay > 0 ? mDelay : 0;

                    Message msgNew = Message.obtain(null, CMD_NEXT_ITERATION, nNextDelay, 0);
                    sendMessage(msgNew);
                    break;

                case CMD_ANSWER_WAKEUP:

                    LocalBroadcastManager.getInstance(CommService.this)
                            .sendBroadcast(new Intent(PeriodicJobService.MSG_WAKEUP_ANSWER)
                                    .putExtra("counter", nWakeupResponseCounter++));


                    break;

                case CMD_DIE:
                    die();
                    break;

                case CMD_STOP:
                    Thread.currentThread().interrupt();
                    stopSelf();
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

        if( (handlerThread == null) || (handlerThread.isAlive() == false) )
        {
            handlerThread = new HandlerThread(SERVICE_THREAD_NAME, Process.THREAD_PRIORITY_BACKGROUND);
            handlerThread.start();
            mServiceHandler = new ServiceHandler(handlerThread.getLooper());
        }
    }


    @Override
    public void onCreate() {
        Log.i(TAG, "service starting");

        startHandleThread();

        LocalBroadcastManager.getInstance(this).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        Log.i(TAG, "local broadcast: " +
                                    MSG_WAKEUP +
                                    "[" + Integer.toString(intent.getIntExtra("counter", -1)) + "]") ;

                        Message msg = Message.obtain(null, CMD_ANSWER_WAKEUP, 0, 0);
                        mServiceHandler.sendMessage(msg);

                    }
                }, new IntentFilter(MSG_WAKEUP));

    }

    @Override
    public void onDestroy (){
        Log.i(TAG, "service destroyed");
        mServiceHandler.removeMessages(0);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "task starting");

        if(intent.getBooleanExtra(MSG_DIE, false) == true){

            Log.i(TAG, "MSG_DIE");

            Message msg = Message.obtain(null, CMD_DIE, 0, startId);
            mServiceHandler.sendMessage(msg);

        }
        else if (intent.getBooleanExtra(MSG_RESTART, false) == true) {
            startHandleThread();

            mDelay = 1234;
            Message msg = Message.obtain(null, CMD_NEXT_ITERATION, 0, startId);
            mServiceHandler.sendMessage(msg);
        }
        else if (intent.getBooleanExtra(MSG_STOP, false) == true) {
            Message msg = Message.obtain(null, CMD_STOP, 0, startId);
            mServiceHandler.sendMessage(msg);
        }

        else {

            mDelay = intent.getIntExtra(MSG_DELAY, -1);
            Log.i(TAG, "MSG_DELAY: " + Integer.toString(mDelay));

            Message msg = Message.obtain(null, CMD_NEXT_ITERATION, mDelay, startId);
            mServiceHandler.sendMessage(msg);
        }

        return START_REDELIVER_INTENT ;
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
