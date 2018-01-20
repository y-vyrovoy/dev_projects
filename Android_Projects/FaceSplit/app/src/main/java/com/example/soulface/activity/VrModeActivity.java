package com.example.soulface.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import com.example.soulface.MyApp;
import com.example.soulface.R;

public class VrModeActivity extends AppCompatActivity {
    private final static String TAG = VrModeActivity.class.getSimpleName();

    private ImageView mImageLeft;
    private ImageView mImageRight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vr_mode);

        mImageLeft = findViewById(R.id.imageLeft);
        mImageRight = findViewById(R.id.imageRight);

        if (mImageLeft != null) {
            mImageLeft.setImageBitmap(MyApp.getBitmapLeft());
        } else {
            Log.e(TAG, "Can't find left ImageView");
        }

        if (mImageRight != null) {
            mImageRight.setImageBitmap(MyApp.getBitmapRight());
        } else {
            Log.e(TAG, "Can't find right ImageView");
        }

    }
}
