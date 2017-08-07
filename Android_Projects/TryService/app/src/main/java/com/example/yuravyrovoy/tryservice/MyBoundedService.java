package com.example.yuravyrovoy.tryservice;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

public class MyBoundedService extends Service {


    private static final String TAG = WonderService.class.getSimpleName();
    private static final String SERVICE_THREAD_NAME = TAG +"[THREAD]";

    public static final int CMD_START_JOB = 1;

    private final IBinder mBinder = new LocalBinder();
    private Handler mActivityHandler;
    private ServiceHandler mServiceHandler;

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {

        MyBoundedService getService() {
            return MyBoundedService.this;
        }

        public void setMainActivityHandler(Handler handler){
            mActivityHandler = handler;
        }

    }

    public MyBoundedService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


    @Override
    public void onCreate() {
        Toast.makeText(this, TAG + " service starting", Toast.LENGTH_SHORT).show();

        HandlerThread handlerThread = new HandlerThread(SERVICE_THREAD_NAME, Process.THREAD_PRIORITY_BACKGROUND);
        handlerThread.start();
        mServiceHandler = new MyBoundedService.ServiceHandler(handlerThread.getLooper());
    }


    public void doTask(int nTask){
        Message msg = Message.obtain(null, CMD_START_JOB, nTask, 0);
        mServiceHandler.sendMessage(msg);
    }

    private void sendMessagesToActivity(int nTask){

        for(int iTask = nTask; iTask >= 0;  iTask--) {
            String sMessage = new String();

            for (int i = 0; i < iTask; i++) {
                sMessage += "[" + Integer.toString(i) + "]<-";
            }
            sMessage += "|";


            Intent intent = new Intent(MainActivity.REPLY_ACTION);
            intent.putExtra(MainActivity.MSG_MESSAGE, sMessage);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {

        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what)
            {
                case CMD_START_JOB:
                    Log.i(TAG, "Got new job");

                    int nTask = msg.arg1;
                    sendMessagesToActivity(nTask);
                    break;
            }

        }
    }
}
