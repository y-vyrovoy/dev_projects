package com.example.yuravyrovoy.tryservice;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String REPLY_ACTION = TAG + "[WONDER_REPLY]";
    public static final String MSG_MESSAGE = TAG + "[MESSAGE]";

    private TextView viewMessages;
    private SeekBar seekBar;
    private EditText editText;

    private MyBoundedService mBoundedService;
    private boolean mBound = false;

    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            MyBoundedService.LocalBinder binder = (MyBoundedService.LocalBinder) iBinder;
            mBoundedService = binder.getService();
            mBound = true;

            binder.setMainActivityHandler(new Handler(Looper.getMainLooper()));
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    // initializing controls variables
        viewMessages = (TextView) findViewById(R.id.textMessages);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        editText = (EditText) findViewById(R.id.editText);


    // Broadcast receiver setup
        IntentFilter intentFilter = new IntentFilter(REPLY_ACTION);

        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i(TAG, "BroadcastReceiver.onReceive()");

                String intentMessage = intent.getStringExtra(MSG_MESSAGE);
                AddMessage(intentMessage);
            }
        }, intentFilter);


    // TextView setup
        if(viewMessages != null)
        {
            viewMessages.setMovementMethod(new ScrollingMovementMethod());
        }

    // SeekBar setup
        if(seekBar != null){
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {}
                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {}
                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    seekBarPositionChanged();
                }
            });
        }

    }

    @Override
    protected void onStart (){
        super.onStart ();

        startService(new Intent(this, WonderService.class));

        Intent intentBounded = new Intent(this, MyBoundedService.class);
        boolean bBounded = bindService(intentBounded, mServiceConnection, Context.BIND_AUTO_CREATE);

        Log.i(TAG, "Service is bounded = " + Boolean.toString(bBounded));
    }

    @Override
    protected void onStop() {
        super.onStop();

        // stop WonderService
        stopService(new Intent(new Intent(this, WonderService.class)));

        // stop MyBoundedService
        if (mBound) {
            unbindService(mServiceConnection);
            mBound = false;
        }

    }

    public void onBtnSendMessage(View v){

        int nDelay = -1;
        try{
            nDelay = Integer.parseInt(editText.getText().toString());
        }catch (Exception ex){
            Log.i(TAG, ex.getMessage());
        }

        if(nDelay > 0) {
            Intent intent = new Intent(this, WonderService.class);
            intent.putExtra(WonderService.MSG_DELAY, nDelay);
            startService(intent);
        }
    }

    private void seekBarPositionChanged(){

        int nNewValue = seekBar.getProgress();
        if(mBound == true) {
            mBoundedService.doTask(nNewValue);
        }
    }

    public void onBtnStopService(View v){

        // stop WonderService
        stopService(new Intent(new Intent(this, WonderService.class)));

        // stop MyBoundedService
        if (mBound) {
            unbindService(mServiceConnection);
            mBound = false;
        }

    }

    public void AddMessage(String sMessage){
        viewMessages.setText(sMessage+ "\r\n" + viewMessages.getText() );
    }

}
