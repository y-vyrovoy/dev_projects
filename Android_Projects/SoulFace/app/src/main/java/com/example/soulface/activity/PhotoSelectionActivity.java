package com.example.soulface.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.soulface.DebugLogger;
import com.example.soulface.FullScreenAd;
import com.example.soulface.R;
import com.example.soulface.SoulFaceApp;

import java.io.IOException;

public class PhotoSelectionActivity extends BasicBanneredActivity {

    private static final String TAG = PhotoSelectionActivity.class.getSimpleName();

    private static int RESULT_LOAD_IMAGE = 1;
    private static int RESULT_CAMERA_IMAGE = 2;

    private ImageView mImageMain;
    private ImageView mImageAnimation;
    private FullScreenAd mFullScreenAd;

    private int mAlpha0;
    private int mAlpha1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        DebugLogger.d();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_selection);

        InitializeBanner();

        mImageAnimation = findViewById(R.id.image_animation);
        mImageMain = findViewById(R.id.image_main);
        mFullScreenAd = SoulFaceApp.getInstance().getPreloadedAd();
    }

    @Override
    public void onStart () {
        DebugLogger.d();
        super.onStart();

        mAlpha0 = 0xFF;
        mAlpha1 = 0x00;

        mImageAnimation.setImageResource(R.drawable.face_anim_0);
        mImageAnimation.setImageAlpha(mAlpha0);
        mImageMain.setImageResource(R.drawable.face_anim_1);
        mImageMain.setImageAlpha(mAlpha1);

        RedrawThread th = new RedrawThread();
        th.start();
    }

    public void onBtlLoadPhoto(View v) {
        DebugLogger.d();

        Intent gallery = new Intent(Intent.ACTION_GET_CONTENT);
        gallery.setType("image/*");
        startActivityForResult(gallery, RESULT_LOAD_IMAGE);
    }

    private Uri _imageUri;
    public void onBtnShootPhoto(View v) {
        DebugLogger.d();

        ContentValues values = new ContentValues();

        _imageUri = getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, _imageUri);
        startActivityForResult(intent, RESULT_CAMERA_IMAGE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        DebugLogger.d();

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
                    SoulFaceApp.setBitmapToEdit(bmp);
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
                SoulFaceApp.setBitmapToEdit(bmp);
            }

            mFullScreenAd.showAd(() -> startActivity(new Intent(this, EditImageActivity.class)));
        }
    }

    public void onBackPressed() {
        DebugLogger.d();

        // doing nothing to prevent moving to WelcomeActivity
    }

    private void refreshImages() {

        if (mImageMain != null && mImageAnimation != null)
        {
            mImageAnimation.setImageAlpha(mAlpha0);
            mImageMain.setImageAlpha(mAlpha1);
            mImageMain.postInvalidate();
            mImageAnimation.postInvalidate();
        }
    }

    private class RedrawThread extends Thread {

        private int mFramesCount = 50;
        private int mFrameDuration = 40;
        private int alphaDelta;

        public RedrawThread() {
        }

        public RedrawThread(int framesCount, int animationDuration) {
            mFramesCount = framesCount;
            mFrameDuration = animationDuration / mFramesCount;
        }

        @Override
        public void run() {
            alphaDelta = 255 / mFramesCount;
            if (alphaDelta * mFramesCount < 255) {
                mFramesCount += 1;
            }

            try {
                Thread.sleep(50);
            } catch (InterruptedException ex){ }

            for (int i = 0; i < mFramesCount; i++)
            {
                mAlpha0 -= alphaDelta;
                mAlpha1 += alphaDelta;
                runOnUiThread(()->refreshImages()) ;

                try {
                    Thread.sleep(mFrameDuration);
                } catch (InterruptedException ex){ }
            }
        }
    }
}
