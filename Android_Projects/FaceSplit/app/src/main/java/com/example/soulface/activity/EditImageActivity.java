package com.example.soulface.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.example.soulface.BitmapUtils;
import com.example.soulface.MyApp;
import com.example.soulface.R;
import com.example.soulface.ScalableImageView;

public class EditImageActivity extends AppCompatActivity {
    private static final String TAG = EditImageActivity.class.getSimpleName();

    private ScalableImageView mImageMain = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate()");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_image);
        mImageMain = findViewById(R.id.imageSelection);
    }

    @Override
    public void onStart () {
        super.onStart();
        mImageMain.setImageBitmap(MyApp.getBitmapToEdit());
    }

    public void onBtnBack(View v) {
        //finish();
        onBackPressed();
    }

    public void onBtnReady(View v) {

        Bitmap bmpResult = mImageMain.getResultBitmap();

        //MyApp.setBitmapToEdit(bmpResult);
        MyApp.setBitmapLeft(BitmapUtils.getDoubledLeftPart(bmpResult));
        MyApp.setBitmapRight(BitmapUtils.getDoubledRightPart(bmpResult));


        startActivity(new Intent(this, ResultActivity.class));
    }
}
