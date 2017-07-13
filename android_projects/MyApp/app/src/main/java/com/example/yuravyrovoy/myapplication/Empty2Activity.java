package com.example.yuravyrovoy.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class Empty2Activity extends AppCompatActivity {

    private static int nCounter = 0;
    private String sState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty2);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_STATE);

        sState = ( (message != null) && (message.isEmpty() == false) ) ?
                        message + " + E2." + Integer.toString(nCounter++) :
                        "E2." + Integer.toString(nCounter++);

        TextView txtMessage = (TextView)findViewById(R.id.textEmpty2Message);
        txtMessage.setText(sState);
    }

    /** Called when the user taps the Show Empty 1 button */
    public void ShowEmpty1(View view){
        Intent intent = new Intent(this, Empty1Activity.class);
        intent.putExtra(MainActivity.EXTRA_STATE, sState);
        startActivity(intent);
    }

    /** Called when the user taps the Show Empty 2 button */
    public void ShowEmpty2(View view){
        Intent intent = new Intent(this, Empty2Activity.class);
        intent.putExtra(MainActivity.EXTRA_STATE, sState);
        startActivity(intent);
    }

}
