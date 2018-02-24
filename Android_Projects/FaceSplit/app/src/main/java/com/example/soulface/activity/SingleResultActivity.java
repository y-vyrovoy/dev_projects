package com.example.soulface.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.soulface.BitmapUtils;
import com.example.soulface.MyApp;
import com.example.soulface.R;

public class SingleResultActivity extends AppCompatActivity {
    private final static String TAG = SingleResultActivity.class.getSimpleName();

    private ImageView mImageGeneral;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singlt_result);

        mImageGeneral = findViewById(R.id.image_general);
        mProgressBar = findViewById(R.id.progress_bar);

        Bitmap bmpVrModeImage = MyApp.getSingleResultBitmap();

        if (mImageGeneral != null && bmpVrModeImage != null) {
            mImageGeneral.setImageBitmap(bmpVrModeImage);
        } else {
            Log.e(TAG, "Can't find left ImageView");
        }
    }

    protected void onStart() {
        super.onStart();
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    public void onBtnShare(View v) {
        mProgressBar.setVisibility(View.VISIBLE);
        Bitmap bmpVrModeImage = MyApp.getVrModeBitmap(false);
        BitmapUtils.shareImage(bmpVrModeImage,
                this,
                ()-> {
                    mProgressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(this, R.string.image_saved, Toast.LENGTH_SHORT).show();
                },
                null);
    }

    public void onBtnBack(View v) {
        onBackPressed();
    }

    public void onBtnSave(View v) {
        mProgressBar.setVisibility(View.VISIBLE);
        Bitmap bmpVrModeImage = MyApp.getVrModeBitmap(false);
        BitmapUtils.saveBitmapGallery(bmpVrModeImage, this);
        mProgressBar.setVisibility(View.INVISIBLE);
        v.setVisibility(View.INVISIBLE);
        Toast.makeText(this, R.string.image_saved, Toast.LENGTH_SHORT).show();
    }
}
