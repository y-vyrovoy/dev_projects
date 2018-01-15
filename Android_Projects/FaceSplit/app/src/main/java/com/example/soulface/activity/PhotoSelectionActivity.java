package com.example.soulface.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.soulface.MyApp;
import com.example.soulface.R;

import java.io.IOException;

public class PhotoSelectionActivity extends AppCompatActivity {

    private static final String TAG = PhotoSelectionActivity.class.getSimpleName();

    private static int RESULT_LOAD_IMAGE = 1;
    private static int RESULT_CAMERA_IMAGE = 2;

    private ImageView mImageMain = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_selection);

        mImageMain = findViewById(R.id.imageTemplate);
    }

    @Override
    public void onStart () {
        super.onStart();
        mImageMain.setImageResource(R.drawable.nobody);
    }

    public void onBtlLoadPhoto(View v) {
        Intent gallery = new Intent(Intent.ACTION_GET_CONTENT);
        gallery.setType("image/*");
        startActivityForResult(gallery, RESULT_LOAD_IMAGE);
    }

    private Uri _imageUri;
    public void onBtnShootPhoto(View v) {

        ContentValues values = new ContentValues();

        _imageUri = getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, _imageUri);
        startActivityForResult(intent, RESULT_CAMERA_IMAGE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if(requestCode == RESULT_LOAD_IMAGE) {
                Uri selectedImage = data.getData();
                if (selectedImage == null) {
                    Log.e(TAG, "URI selectedImage == null");
                    return;
                }

                Bitmap bmp = null;
                try {
                    bmp = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                if(bmp != null) {
                    MyApp.setBitmapToEdit(bmp);
                    mImageMain.setImageBitmap(bmp);
                    Log.d(TAG, "Save bitmap to load: w: " + bmp.getWidth() +
                                        ", h: " + bmp.getHeight());
                }
            } else if(requestCode == RESULT_CAMERA_IMAGE) {
                Bitmap bmp = null;
                try {
                    bmp = MediaStore.Images.Media.getBitmap(
                                                    getContentResolver(), _imageUri);

                } catch (Exception e) {e.printStackTrace();}

                mImageMain.setImageBitmap(bmp);
                MyApp.setBitmapToEdit(bmp);
            }

            startActivity(new Intent(this, EditImageActivity.class));
        }
    }

    public void onBackPressed() {

    }

}
