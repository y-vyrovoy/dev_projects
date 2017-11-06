package com.example.facesplit;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

    private ScalableImageView imageMain = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_selection);

        imageMain = (ScalableImageView) findViewById(R.id.imageSelection);
    }

    public void onBtlLoadPhoto(View v) {
        Intent gallery = new Intent(Intent.ACTION_GET_CONTENT);
        gallery.setType("image/*");
        startActivityForResult(gallery, RESULT_LOAD_IMAGE);
    }

    private Uri _imageUri;
    public void onBtnShootPhoto(View v) {

        ContentValues values = new ContentValues();
        //values.put(MediaStore.Images.Media.TITLE, "New Picture");
        //values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
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
                Log.i(TAG, selectedImage.toString());

                Bitmap bmp = null;
                try {
                    bmp = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                if(bmp != null) {
                    MyApp.setBitmapToEdit(bmp);
                    imageMain.setImageBitmap(bmp);
                }
            } else if(requestCode == RESULT_CAMERA_IMAGE) {
                Bitmap bmp = null;
                try {
                    bmp = MediaStore.Images.Media.getBitmap(
                                                    getContentResolver(), _imageUri);

                } catch (Exception e) {e.printStackTrace();}

                imageMain.setImageBitmap(bmp);
                MyApp.setBitmapToEdit(bmp);
            }
        }
    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public void onBtnReady(View v) {

        Bitmap bmpResult = imageMain.getResultBitmap();

        MyApp.setBitmapLeft(BitmapUtils.getDoubledLeftPart(bmpResult));
        MyApp.setBitmapRight(BitmapUtils.getDoubledRightPart(bmpResult));


        startActivity(new Intent(this, ResultActivity.class));
    }


}
