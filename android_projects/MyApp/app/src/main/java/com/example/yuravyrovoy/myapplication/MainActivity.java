package com.example.yuravyrovoy.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    //public static int nCounter = 0;
    
    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
    public static final String EXTRA_STATE = "com.example.myfirstapp.STATE";
    static final int PICK_CONTACT_REQUEST = 0;

    static final String STATE_OLOLO = "stateOlolo";

    static int nLogCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("STATE: ", "MainActivity.onCreate()");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(savedInstanceState != null) {

            // Restore state members from saved instance
            int nCurrentCounter = savedInstanceState.getInt(STATE_OLOLO);
            TextView txtCounter = (TextView) findViewById(R.id.txtCounter);
            txtCounter.setText(Integer.toString(nCurrentCounter));
        }
    }


    @Override
    protected void onStart(){
        Log.d("STATE: ", "MainActivity.onStart()");

        super.onStart();
    }

    @Override
    protected void onResume(){
        Log.d("STATE: ", "MainActivity.onResume()");

        super.onResume();
    }

    @Override
    protected void onPause(){
        Log.d("STATE: ", "MainActivity.onPause()");

        super.onPause();
    }

    @Override
    protected void onStop(){
        Log.d("STATE: ", "MainActivity.onStop()");

        super.onStop();
    }

    @Override
    protected void onDestroy(){
        Log.d("STATE: ", "MainActivity.onDestroy()");

        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        Log.d("STATE: ", "MainActivity.onSaveInstanceState()");

        savedInstanceState.putInt(STATE_OLOLO, nLogCounter++);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.d("STATE: ", "MainActivity.onRestoreInstanceState()");

        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);

        // Restore state members from saved instance
        int nCurrentCounter = savedInstanceState.getInt(STATE_OLOLO);
        TextView txtCounter = (TextView)findViewById(R.id.txtCounter);
        txtCounter.setText(Integer.toString(nCurrentCounter));
    }


    /** Called when the user taps the Send button */
    public void sendMessage(View view) {

        EditText editText = (EditText) findViewById(R.id.textView);
        String message = editText.getText().toString();

        Intent intent = new Intent(this, DisplayMessageActivity.class);
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    /** Called when the user taps the Do something button */
    public void DoSomething(View view){

        Intent intent = new Intent(this, Empty1Activity.class);
        intent.putExtra(EXTRA_STATE, "");
        startActivity(intent);
    }
}
