package com.example.soulface.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.soulface.BitmapUtils;
import com.example.soulface.DebugLogger;
import com.example.soulface.FullScreenAd;
import com.example.soulface.R;
import com.example.soulface.SoulFaceApp;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class WelcomeActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST = 1;

    private AtomicBoolean mWaitForPermissions = new AtomicBoolean();
    private AtomicBoolean mIsPermissionsGranted = new AtomicBoolean();
    private List<String> mLstPermissions;

    private final Handler mHandler = new Handler();
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        DebugLogger.d();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        mProgressBar = findViewById(R.id.progress_bar);
    }

    @Override
    protected void onStart() {
        DebugLogger.d();

        super.onStart();

        List<String> lstPermissions = new ArrayList<>();
        lstPermissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        lstPermissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        lstPermissions.add(Manifest.permission.CAMERA);
        lstPermissions.add(Manifest.permission.INTERNET);

        fillPermissionsRequest(lstPermissions);
    }

    private void fillPermissionsRequest(List<String> lstPermissions) {
        DebugLogger.d();

        mIsPermissionsGranted.set(true);
        mWaitForPermissions.set(false);

        mLstPermissions = new ArrayList<>();
        for (String sPermission : lstPermissions) {
            if(ContextCompat.checkSelfPermission(this, sPermission) !=
                                                                PackageManager.PERMISSION_GRANTED) {
                mIsPermissionsGranted.set(false);
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

                boolean isAllPermissionsGranted = true;
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        isAllPermissionsGranted = false;
                        break;
                    }
                }

                mIsPermissionsGranted.set(isAllPermissionsGranted);
                mWaitForPermissions.set(false);

                if (mIsPermissionsGranted.get() == false) {
                    mProgressBar.setVisibility(View.INVISIBLE);
                }
            }
        }
     }

    private void waitForPermissions() {
        DebugLogger.d();

        mHandler.postDelayed( () -> {

            while( mWaitForPermissions.get() == true ) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {}
            }

            if( mIsPermissionsGranted.get() == true ) {
                startActivity(new Intent(WelcomeActivity.this, PhotoSelectionActivity.class));
                finish();
            }
        }, 3000);
    }

}

