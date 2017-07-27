package com.example.yuravyrovoy.tryservice;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

public class WonderService extends Service {

    private static final String TAG = WonderService.class.getSimpleName();
    public static final int MSG_SEND_NOTIFICATION = 1;
    private static final String SERVICE_THREAD_NAME = "com.example.yuravyrovoy.tryservice.WonderService.Thread";
    public static final String REPLY_ACTION = "com.example.yuravyrovoy.tryservice.WonderService.WONDER_REPLY";

    private ServiceHandler mServiceHandler;

    private int mCounter;

    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {

        public ServiceHandler(Looper looper) {
            super(looper);
            mCounter = 0;
        }

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what)
            {
                case MSG_SEND_NOTIFICATION:
                    int nDelay = msg.arg1;

                    if(nDelay == -2) {
                        Log.i(TAG,  "Try to stop. nDelay = " + msg.arg1 + " | Counter:" + mCounter++);
                        stopSelf();
                        return;
                    }

                    try {
                        Thread.sleep(nDelay);
                    } catch (InterruptedException e) {
                        // Restore interrupt status.
                        Thread.currentThread().interrupt();
                    }


                    Intent intent = new Intent(REPLY_ACTION);
                    intent.putExtra("message", "Delay finished: " + Integer.toString(nDelay));

                    LocalBroadcastManager.getInstance(WonderService.this).sendBroadcast(intent);
                    Log.i(TAG,  "Finished delay:" + msg.arg1 + " | Counter:" + mCounter++);

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
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();

        int nDelay = intent.getIntExtra("delay", -1);

        if( nDelay != -1){
            Message msg = Message.obtain(null, WonderService.MSG_SEND_NOTIFICATION, nDelay, startId);
            mServiceHandler.sendMessage(msg);
        }

        return START_STICKY;
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
