package com.example.myimageview;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class PhotoEdit extends AppCompatActivity {

    private static final String TAG = PhotoEdit.class.getSimpleName();

    private static int RESULT_LOAD_IMAGE = 1;
    private static int RESULT_CAMERA_IMAGE = 2;

    private ScalableImageView imageMain = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_edit);

        imageMain = (ScalableImageView) findViewById(R.id.imageMain);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void onBtnLoad(View v ) {
        Intent gallery = new Intent(Intent.ACTION_GET_CONTENT);
        gallery.setType("image/*");
        startActivityForResult(gallery, RESULT_LOAD_IMAGE);
    }

    public void onBtnShoot(View v ) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, RESULT_CAMERA_IMAGE);
    }

    public void onBtnGrid(View v ) {
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.grid);
        MyApp.setSourceBitmap(bmp);
        imageMain.setImageBitmap(bmp);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if(requestCode == RESULT_LOAD_IMAGE) {
                Uri selectedImage = data.getData();
                Log.i(TAG, selectedImage.toString());

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inScaled = false;
                options.inDither = false;
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                Bitmap bitmap = BitmapFactory.decodeFile(selectedImage.getEncodedPath(), options);
                imageMain.setImageBitmap(bitmap);

                Bitmap bmp = null;
                try {
                    bmp = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                MyApp.setSourceBitmap(bmp);

            } else if(requestCode == RESULT_CAMERA_IMAGE) {
                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                //imageMain.setImageBitmap(thumbnail);
                MyApp.setSourceBitmap(thumbnail);
            }
        }
    }
}
