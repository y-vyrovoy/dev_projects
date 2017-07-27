package com.example.yuravyrovoy.tryservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    public static final int MSG_SEND_HANDLER_NOTIFICATION = 1;
    private Handler serviceHandler;
    public static Handler activityHandler;

// Handler that receives messages from the thread


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activityHandler = new Handler(new Handler.Callback() {

            @Override
            public boolean handleMessage(Message message) {

                switch (message.what)
                {
                    case MSG_SEND_HANDLER_NOTIFICATION:
                        serviceHandler = (Handler)message.obj;
                        break;
                }
                return true;
            }
        });

        IntentFilter intentFilter = new IntentFilter(WonderService.REPLY_ACTION);

        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i(TAG, "BroadcastReceiver.onReceive()");
                TextView textView = (TextView)findViewById(R.id.textView);

                String intentMessage = intent.getStringExtra("message");
                textView.setText(intentMessage);
            }
        }, intentFilter);

    }

    @Override
    protected void onStart (){
        super.onStart ();

        Intent intent = new Intent(new Intent(this, WonderService.class));
        startService(intent);
    }

    public void onBtnSendMessage(View v){
        Log.i(TAG, "click click");

        EditText editText = (EditText)findViewById(R.id.editText);

        int nDelay = -1;
        try{
            nDelay = Integer.parseInt(editText.getText().toString());
        }catch (Exception ex){
            Log.i(TAG, ex.getMessage());
        }

        if(nDelay > 0) {
            Intent intent = new Intent(new Intent(this, WonderService.class));
            intent.putExtra("delay", nDelay);
            startService(intent);
        }
    }

    private static int nCounter333 = 0;

    public void onBtnStopService(View v){
        Log.i(TAG, "click click " + Integer.toString(nCounter333++));

/*
        Intent intent = new Intent(new Intent(this, WonderService.class));
        intent.putExtra("delay", -2);
        startService(intent);
*/
    }

}
