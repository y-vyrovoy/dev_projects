package com.example.facesplit;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class EditPhotoActivity extends AppCompatActivity {

    private SplitImageView _imageEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_photo);

        _imageEdit = (SplitImageView) findViewById(R.id.imageEdit);

        Bitmap bmpEdit = MyApp.getBitmapToEdit();
        if(bmpEdit != null) {
            _imageEdit.setSourceBitmap(bmpEdit);
        }
    }

    public void onBtnReady(View v) {
        MyApp.setBitmapLeft(BitmapProcessor.getDoubledBitmap(_imageEdit.getLeftSideBitmap(), true));
        MyApp.setBitmapRight(BitmapProcessor.getDoubledBitmap(_imageEdit.getRightSideBitmap(), false));

        startActivity(new Intent(this, ResultActivity.class));
    }
}
