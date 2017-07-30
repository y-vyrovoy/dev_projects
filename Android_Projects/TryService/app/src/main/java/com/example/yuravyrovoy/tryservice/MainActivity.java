package com.example.yuravyrovoy.tryservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
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

    private TextView viewMessages;
    private SeekBar seekBar;
    private EditText editText;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    // initializing controls variables
        viewMessages = (TextView) findViewById(R.id.textMessages);
        if (viewMessages == null)
            Log.e(TAG, "Can't find R.id.textMessages", new Throwable("Can't find control"));

        seekBar = (SeekBar) findViewById(R.id.seekBar);
        if (seekBar == null)
            Log.e(TAG, "Can't find R.id.seekBar", new Throwable("Can't find control"));

        editText = (EditText) findViewById(R.id.editText);
        if (editText == null)
            Log.e(TAG, "Can't find R.id.editText", new Throwable("Can't find control"));


    // Broadcast receiver setup
        IntentFilter intentFilter = new IntentFilter(WonderService.REPLY_ACTION);

        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i(TAG, "BroadcastReceiver.onReceive()");

                String intentMessage = intent.getStringExtra("message");
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
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

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

        Intent intent = new Intent(new Intent(this, WonderService.class));
        startService(intent);
    }

    public void onBtnSendMessage(View v){
        Log.i(TAG, "click click");

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

    private void seekBarPositionChanged(){

        int nNewValue = seekBar.getProgress();
        String sMessage = new String();

        for(int i = 0; i < nNewValue; i++)
        {
            sMessage += "[" + Integer.toString(i) + "]<-";
        }
        sMessage += "|";

        AddMessage(sMessage);
    }

    private static int nCounter333 = 0;

    public void onBtnStopService(View v){
        Log.i(TAG, "click click " + Integer.toString(nCounter333++));

        Intent intent = new Intent(new Intent(this, WonderService.class));
        intent.putExtra("delay", -2);
        stopService(intent);

    }

    public void AddMessage(String sMessage){
        viewMessages.setText(viewMessages.getText() + "\r\n" + sMessage);
    }

}
