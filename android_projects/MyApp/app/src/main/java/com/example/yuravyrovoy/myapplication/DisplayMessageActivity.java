package com.example.yuravyrovoy.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

public class DisplayMessageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        // Capture the layout's TextView and set the string as its text
        TextView textView = (TextView) findViewById(R.id.textView);
        textView.setText(message);

        Log.d("STATE: ", "DisplayMessageActivity.onCreate()");
    }

    @Override
    protected void onStart(){
        Log.d("STATE: ", "DisplayMessageActivity.onStart()");

        super.onStart();
    }

    @Override
    protected void onResume(){
        Log.d("STATE: ", "DisplayMessageActivity.onResume()");

        super.onResume();
    }

    @Override
    protected void onPause(){
        Log.d("STATE: ", "DisplayMessageActivity.onPause()");

        super.onPause();
    }

    @Override
    protected void onStop(){
        Log.d("STATE: ", "DisplayMessageActivity.onStop()");

        super.onStop();
    }

    @Override
    protected void onDestroy(){
        Log.d("STATE: ", "DisplayMessageActivity.onDestroy()");

        super.onDestroy();
    }
}
