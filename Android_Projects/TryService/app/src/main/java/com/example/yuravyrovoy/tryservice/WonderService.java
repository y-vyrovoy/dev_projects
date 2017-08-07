package com.example.yuravyrovoy.tryservice;

import android.app.Service;
import android.content.Intent;
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
    private static final String SERVICE_THREAD_NAME = TAG +"[THREAD]";

    public static final int CMD_SEND_NOTIFICATION = 1;

    public static final String MSG_DELAY = TAG + "[DELAY]";

    private ServiceHandler mServiceHandler;
    private int mCounter;

    public WonderService() {
    }

    @Override
    public void onCreate() {
        Toast.makeText(this, TAG + " service starting", Toast.LENGTH_SHORT).show();

        HandlerThread  handlerThread = new HandlerThread(SERVICE_THREAD_NAME, Process.THREAD_PRIORITY_BACKGROUND);
        handlerThread.start();
        mServiceHandler = new ServiceHandler(handlerThread.getLooper());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, TAG + " task starting", Toast.LENGTH_SHORT).show();

        int nDelay = intent.getIntExtra(MSG_DELAY, -1);

        if( nDelay != -1 ){
            Message msg = Message.obtain(null, CMD_SEND_NOTIFICATION, nDelay, startId);
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
        Toast.makeText(this, TAG + " service done", Toast.LENGTH_SHORT).show();
        Log.i(TAG, TAG + " onDestroy()");
    }

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
                case CMD_SEND_NOTIFICATION:
                    int nDelay = msg.arg1;

                    try {
                        Thread.sleep(nDelay);
                    } catch (InterruptedException e) {
                        // Restore interrupt status.
                        Thread.currentThread().interrupt();
                    }

                    Intent intent = new Intent(MainActivity.REPLY_ACTION);
                    intent.putExtra(MainActivity.MSG_MESSAGE, "Delay finished: " + Integer.toString(nDelay));
                    LocalBroadcastManager.getInstance(WonderService.this).sendBroadcast(intent);

                    Log.i(TAG,  "Finished delay:" + msg.arg1 + " | Counter:" + mCounter++);
                    Toast.makeText(WonderService.this, "task done", Toast.LENGTH_SHORT).show();

                    break;
            }

        }
    }
}
