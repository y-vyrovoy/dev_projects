package com.example.yuravyrovoy.tryservice;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    public static final int MSG_SEND_HANDLER_NOTIFICATION = 1;
    private Handler serviceHandler;
    private ActivityHandler activityHandler;

// Handler that receives messages from the thread

    private final class ActivityHandler extends Handler {

        public ActivityHandler(Looper looper) {
            super(looper);
          }

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what)
            {
                case MSG_SEND_HANDLER_NOTIFICATION:
                    serviceHandler = (Handler)msg.obj;
                    break;

                default:
                    super.handleMessage(msg);
                    break;
            }
        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //activityHandler = new ActivityHandler(Looper.myLooper());

        Handler h = new Handler(new Handler.Callback() {

            @Override
            public boolean handleMessage(Message message) {

                switch (message.what)
                {
                    case MSG_SEND_HANDLER_NOTIFICATION:
                        serviceHandler = (Handler)message.obj;
                        break;
                }
                return false;
            }
        });
    }

    @Override
    protected void onStart (){
        super.onStart ();

        Intent intent = new Intent(new Intent(this, WonderService.class));
        startService(intent);
    }

    public void onBtnSendMessage(View v){
        Log.i(TAG, "click click");

        Message msg = Message.obtain(serviceHandler,
                                            WonderService.MSG_SEND_NOTIFICATION,
                                            1000, 0);
        serviceHandler.sendMessage(msg);

    }

    public void onBtnStopService(View v){
        Log.i(TAG, "click click 2");

    }


    private void sendMessage() {

        Message msg = Message.obtain(null, WonderService.MSG_SEND_NOTIFICATION, 0, 0);

        try {

        } catch (Exception ex) {
            Log.e(TAG, "Error sending a message", ex);
        }
    }
}
