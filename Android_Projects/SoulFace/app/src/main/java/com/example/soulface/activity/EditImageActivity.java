package com.example.soulface.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;

import com.example.soulface.BitmapUtils;
import com.example.soulface.DebugLogger;
import com.example.soulface.SoulFaceApp;
import com.example.soulface.R;
import com.example.soulface.ScalableImageView;

public class EditImageActivity extends BasicBanneredActivity {
    private static final String TAG = EditImageActivity.class.getSimpleName();

    private ScalableImageView mImageMain = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        DebugLogger.d();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_image);
        mImageMain = findViewById(R.id.imageSelection);

        InitializeBanner();
    }

    @Override
    public void onStart () {
        DebugLogger.d();

        super.onStart();
        mImageMain.setImageBitmap(SoulFaceApp.getBitmapToEdit());
    }

    public void onBtnBack(View v) {
        DebugLogger.d();
        onBackPressed();
    }

    public void onBtnReady(View v) {
        DebugLogger.d();

        Bitmap bmpResult = mImageMain.getResultBitmap();

        SoulFaceApp.setBitmapLeft(BitmapUtils.getDoubledLeftPart(bmpResult));
        SoulFaceApp.setBitmapRight(BitmapUtils.getDoubledRightPart(bmpResult));

        startActivity(new Intent(this, ResultActivity.class));
    }
}
