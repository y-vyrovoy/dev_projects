package com.example.mycustomview;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class MainActivity extends AppCompatActivity{

    private static final String TAG = MainActivity.class.getSimpleName();

    private SplitImageView _viewSplit;
    private GestureDetectorCompat _gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _viewSplit = (SplitImageView) findViewById(R.id.viewImage);
    }

    public void onBtnReload(View v) {
        _viewSplit.setSourceBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.face));
        _viewSplit.invalidate();
    }

    public void onBtnSplit(View v) {

        MyApp.getInstance().setBitmapLeft(BitmapProcessor.getDoubledBitmap(_viewSplit.getLeftSideBitmap()));
        startActivity(new Intent(this, ResultActivity.class));

    }
}
