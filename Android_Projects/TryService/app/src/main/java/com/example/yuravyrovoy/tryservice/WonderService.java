package com.example.yuravyrovoy.tryservice;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;
import android.widget.Toast;

public class WonderService extends Service {

    private static final String TAG = WonderService.class.getSimpleName();
    public static final int MSG_SEND_NOTIFICATION = 1;
    private static final String SERVICE_THREAD_NAME = "com.example.yuravyrovoy.tryservice.WonderService.Thread";

    private ServiceHandler mServiceHandler;
    private ServiceHandler mUIServiceHandler;

    private int mCounter;

    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {

        public ServiceHandler(Looper looper) {
            super(looper);
            mCounter = 0;
        }

        @Override
        public void handleMessage(Message msg) {
            // Normally we would do some work here, like download a file.
            // For our sample, we just sleep for 5 seconds.

            switch (msg.what)
            {
                case MSG_SEND_NOTIFICATION:
                    try {
                        int nDelay = msg.arg1;
                        Thread.sleep(nDelay);
                    } catch (InterruptedException e) {
                        // Restore interrupt status.
                        Thread.currentThread().interrupt();
                    }

                    Log.i(TAG,  "Finished delay:" + msg.arg1);
                    Log.i(TAG,  "Counter:" + mCounter++);
                    Log.i(TAG, msg.toString());
                    //stopSelf();
                    break;
            }

            // Stop the service using the startId, so that we don't stop
            // the service in the middle of handling another job
            //stopSelf(msg.arg1);
        }
    }

    public WonderService() {
    }

    @Override
    public void onCreate() {

        HandlerThread  handlerThread = new HandlerThread(SERVICE_THREAD_NAME, Process.THREAD_PRIORITY_BACKGROUND);
        handlerThread.start();
        mServiceHandler = new ServiceHandler(handlerThread.getLooper());

        boolean b2 = Looper.myLooper().getThread() == handlerThread;

        mUIServiceHandler = new ServiceHandler(Looper.myLooper());
        Message msgHandler = Message.obtain(mUIServiceHandler,
                                            MainActivity.MSG_SEND_HANDLER_NOTIFICATION,
                                            21, 23,
                                            mServiceHandler);

        boolean b = mUIServiceHandler.sendMessage(msgHandler);
        Log.i(TAG, "mUIServiceHandler.sendMessage(msgHandler) = " + Boolean.toString(b));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();

        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job

        Message msg = Message.obtain(null, WonderService.MSG_SEND_NOTIFICATION, 0, 0);
        mServiceHandler.sendMessage(msg);

        // If we get killed, after returning from here, restart
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
        Log.i(TAG, "onDestroy()");
    }


}
