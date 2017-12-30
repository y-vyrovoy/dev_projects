package com.example.soulface.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.example.soulface.BitmapUtils;
import com.example.soulface.MyApp;
import com.example.soulface.R;

public class ResultActivity extends AppCompatActivity {

    ImageView _imageLeft;
    ImageView _imageRight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        _imageLeft = (ImageView) findViewById(R.id.viewImageLeft);
        _imageRight = (ImageView) findViewById(R.id.viewImageRight);

        _imageLeft.setImageBitmap(MyApp.getBitmapLeft());
        _imageRight.setImageBitmap(MyApp.getBitmapRight());
    }

    public void onBtnSave(View v) {
        BitmapUtils.saveBitmap(MyApp.getBitmapLeft(), "l", this);
        BitmapUtils.saveBitmap(MyApp.getBitmapRight(), "r", this);
    }

    public void onBtnShare(View v) {

    }

}
