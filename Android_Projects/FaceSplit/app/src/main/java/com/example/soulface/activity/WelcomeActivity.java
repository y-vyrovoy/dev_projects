package com.example.soulface.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.soulface.R;

import java.util.ArrayList;
import java.util.List;

public class WelcomeActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST = 1;

    private boolean waitForPermissions;
    private boolean isPermissionsGranted;
    private List<String> mLstPermissions;

    private final Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
    }

    @Override
    protected void onStart() {
        super.onStart();

        waitForPermissions = false;
        mLstPermissions = new ArrayList<>();

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                                                                PackageManager.PERMISSION_DENIED) {

            mLstPermissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);

            waitForPermissions = true;
          } else {
            isPermissionsGranted = true;
        }

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_DENIED) {

            mLstPermissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);

            waitForPermissions = true;
        } else {
            isPermissionsGranted = true;
        }

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==
                                                                PackageManager.PERMISSION_DENIED) {

            mLstPermissions.add(Manifest.permission.CAMERA);

            waitForPermissions = true;
        } else {
            isPermissionsGranted = true;
        }

        if(waitForPermissions == true) {
            String[] permissionsArray = mLstPermissions.toArray(new String[mLstPermissions.size()]);
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
                        lstGranted.add(mLstPermissions.get(i));
                    }
                }
                mLstPermissions.removeAll(lstGranted);

                isPermissionsGranted = (mLstPermissions.isEmpty() == true);
                waitForPermissions = false;
            }
        }
    }

    private void waitForPermissions() {

        mHandler.postDelayed( () -> {
            while( waitForPermissions == true ) { }

            if( isPermissionsGranted == true ) {
                startActivity(new Intent(WelcomeActivity.this, PhotoSelectionActivity.class));
                finish();
            }
        }, 1000);
    }
}
