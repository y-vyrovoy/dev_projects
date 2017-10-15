package com.example.facesplit;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class WelcomeActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_STORAGE = 1;
    private static final int PERMISSIONS_REQUEST_CAMERA = 2;

    private boolean waitForPermissionCamera;
    private boolean waitForPermissionStorage;

    private boolean isPermissionGrantedStorage;
    private boolean isPermissionGrantedCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
    }

    @Override
    protected void onStart() {
        super.onStart();

        waitForPermissionStorage = false;
        waitForPermissionCamera = false;

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                                                                PackageManager.PERMISSION_DENIED) {
            requestPermisson(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                                                    PERMISSIONS_REQUEST_STORAGE);

            waitForPermissionStorage = true;
            isPermissionGrantedStorage = false;
        } else {
            isPermissionGrantedStorage = true;
        }

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==
                                                                PackageManager.PERMISSION_DENIED) {
            requestPermisson(Manifest.permission.CAMERA, PERMISSIONS_REQUEST_CAMERA);
            waitForPermissionCamera = true;
            isPermissionGrantedCamera = false;
        } else {
            isPermissionGrantedCamera = true;
        }

        waitForPermissions();
    }

    private void requestPermisson(String permission, int id) {
        // Should we show an explanation?
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {

        } else {
            ActivityCompat.requestPermissions(this, new String[]{permission}, id);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_STORAGE: {

                waitForPermissionStorage = false;

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    isPermissionGrantedStorage = true;
                } else {
                    isPermissionGrantedStorage = false;
                }
                return;
            }

            case PERMISSIONS_REQUEST_CAMERA: {

                waitForPermissionCamera = false;
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    isPermissionGrantedCamera = true;
                } else {
                    isPermissionGrantedCamera = false;
                }
                return;
            }
        }
    }

    private void waitForPermissions() {

        new AsyncTask<String, Void, String> (){
            @Override
            protected String doInBackground(String... params) {

                try {
                    Thread.currentThread().sleep(2000);
                } catch(InterruptedException ex) {}

                while( (waitForPermissionStorage == true) &&
                        (waitForPermissionCamera == true)) { }

                if(isPermissionGrantedStorage || isPermissionGrantedCamera) {
                    startActivity(new Intent(WelcomeActivity.this, PhotoSelection.class));
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
