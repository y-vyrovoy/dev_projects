package com.example.soulface.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.soulface.R;

import java.util.ArrayList;
import java.util.List;

public class WelcomeActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST = 1;
    private static final int PERMISSIONS_REQUEST_STORAGE = 2;
    private static final int PERMISSIONS_REQUEST_CAMERA = 3;

    private boolean waitForPermissions;
    private boolean isPermissionsGranted;
    private List<String> _lstPermissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
    }

    @Override
    protected void onStart() {
        super.onStart();

        waitForPermissions = false;
        _lstPermissions = new ArrayList<>();

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                                                                PackageManager.PERMISSION_DENIED) {

            _lstPermissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);

            waitForPermissions = true;
          } else {
            isPermissionsGranted = true;
        }

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==
                                                                PackageManager.PERMISSION_DENIED) {

            _lstPermissions.add(Manifest.permission.CAMERA);

            waitForPermissions = true;
        } else {
            isPermissionsGranted = true;
        }

        if(waitForPermissions == true) {
            String[] permissionsArray = _lstPermissions.toArray(new String[_lstPermissions.size()]);
            ActivityCompat.requestPermissions(this, permissionsArray, PERMISSIONS_REQUEST);
        }
        waitForPermissions();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST: {

                List lstGranted = new ArrayList<>();

                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        lstGranted.add(_lstPermissions.get(i));
                    }
                }
                _lstPermissions.removeAll(lstGranted);

                isPermissionsGranted = (_lstPermissions.isEmpty() == true);
                waitForPermissions = false;
            }
        }
    }

    private void waitForPermissions() {

        new AsyncTask<String, Void, String> (){
            @Override
            protected String doInBackground(String... params) {

                try {
                    Thread.currentThread().sleep(1000);
                } catch(InterruptedException ex) {}

                while( waitForPermissions == true ) { }

                if( isPermissionsGranted == true ) {
                    startActivity(new Intent(WelcomeActivity.this, PhotoSelectionActivity.class));
                    finish();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String result) {}

            @Override
            protected void onPreExecute() { }

            @Override
            protected void onProgressUpdate(Void... values) { }
        }.execute();
    }

}
