package com.example.soulface.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.soulface.DebugLogger;
import com.example.soulface.FullScreenAd;
import com.example.soulface.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class WelcomeActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST = 1;

    private AtomicBoolean mWaitForPermissions = new AtomicBoolean();
    private AtomicBoolean mIsPermissionsGranted = new AtomicBoolean();
    private List<String> mLstPermissions;

    private final Handler mHandler = new Handler();
    private FullScreenAd mFullScreenAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        DebugLogger.d();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        mFullScreenAd = new FullScreenAd(this);
    }

    @Override
    protected void onStart() {
        DebugLogger.d();

        super.onStart();

        mFullScreenAd.loadAd();

        List<String> lstPermissions = new ArrayList<>();
        lstPermissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        lstPermissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        lstPermissions.add(Manifest.permission.CAMERA);
        lstPermissions.add(Manifest.permission.INTERNET);

        fillPermissionsRequest(lstPermissions);
    }

    private void fillPermissionsRequest(List<String> lstPermissions) {

        mIsPermissionsGranted.set(true);
        mWaitForPermissions.set(false);

        mLstPermissions = new ArrayList<>();
        for (String sPermission : lstPermissions) {
            if(ContextCompat.checkSelfPermission(this, sPermission) !=
                                                                PackageManager.PERMISSION_GRANTED) {
                mWaitForPermissions.set(true);
                mLstPermissions.add(sPermission);
            }
        }

        if(mWaitForPermissions.get() == true) {
            String[] permissionsArray = mLstPermissions.toArray(new String[mLstPermissions.size()]);
            ActivityCompat.requestPermissions(this, permissionsArray, PERMISSIONS_REQUEST);
        }

        waitForPermissions();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        DebugLogger.d();

        switch (requestCode) {
            case PERMISSIONS_REQUEST: {

                List lstGranted = new ArrayList<>();

                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        lstGranted.add(mLstPermissions.get(i));
                    }
                }
                mLstPermissions.removeAll(lstGranted);

                mIsPermissionsGranted.set(mLstPermissions.isEmpty() == true);
                mWaitForPermissions.set(false);
            }
        }
    }

    private void waitForPermissions() {
        DebugLogger.d();

        mHandler.postDelayed( () -> {

            while( mWaitForPermissions.get() == true ) { }

            if( mIsPermissionsGranted.get() == true ) {
                mFullScreenAd.showAd(()->{
                    startActivity(new Intent(WelcomeActivity.this, PhotoSelectionActivity.class));
                    finish();
                });
            }
        }, 3000);
    }

}

