package com.itamarmedical.ble.facesplit;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
    }

    @Override
    protected void onStart() {
        super.onStart();

        new AsyncTask<String, Void, String> (){
            @Override
            protected String doInBackground(String... params) {

                try {
                    Thread.currentThread().sleep(2000);
                } catch(InterruptedException ex) {}

                startActivity(new Intent(WelcomeActivity.this, PhotoSelection.class));
                finish();

                return null;
            }

            @Override
            protected void onPostExecute(String result) {
            }

            @Override
            protected void onPreExecute() {
            }

            @Override
            protected void onProgressUpdate(Void... values) {
            }
        }.execute();

    }
}
