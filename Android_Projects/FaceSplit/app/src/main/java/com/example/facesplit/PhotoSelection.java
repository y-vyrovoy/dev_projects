package com.example.facesplit;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.IOException;

public class PhotoSelection extends AppCompatActivity {

    private static final String TAG = PhotoSelection.class.getSimpleName();

    private static int RESULT_LOAD_IMAGE = 1;
    private static int RESULT_CAMERA_IMAGE = 2;

    private ImageView imageMain = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_selection);

        imageMain = (ImageView) findViewById(R.id.imageSelection);
    }

    public void onBtlLoadPhoto(View v) {
        Intent gallery = new Intent(Intent.ACTION_GET_CONTENT);
        gallery.setType("image/*");
        startActivityForResult(gallery, RESULT_LOAD_IMAGE);
    }

    public void onBtnShootPhoto(View v) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, RESULT_CAMERA_IMAGE);
    }

    public void onBtnReady(View v) {
        if(MyApp.getBitmapToEdit() != null) {
            startActivity(new Intent(this, EditPhotoActivity.class));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if(requestCode == RESULT_LOAD_IMAGE) {
                Uri selectedImage = data.getData();
                Log.i(TAG, selectedImage.toString());

                imageMain.setImageURI(selectedImage);

                Bitmap bmp = null;
                try {
                    bmp = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                MyApp.setBitmapToEdit(bmp);

            } else if(requestCode == RESULT_CAMERA_IMAGE) {
                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                imageMain.setImageBitmap(thumbnail);
                MyApp.setBitmapToEdit(thumbnail);
            }
        }
    }
}
