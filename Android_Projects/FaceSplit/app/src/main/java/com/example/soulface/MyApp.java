package com.example.soulface;

import android.app.Application;
import android.graphics.Bitmap;
import android.os.Environment;

import java.io.File;

/**
 * Created by Yura Vyrovoy on 10/13/2017.
 */

public class MyApp extends Application {
    private final static String TAG = MyApp.class.getSimpleName();

    private static MyApp mInstance = null;

    private Bitmap mBitmapToEdit = null;
    private Bitmap mBitmapLeft = null;
    private Bitmap mBitmapRight = null;

    private String mPathPhotos;

    @Override
    public void onCreate() {
        super.onCreate();
        createFolder();

        mInstance = this;
    }

    public static MyApp getInstance() {
        return mInstance;
    }

    public static void setBitmapToEdit(Bitmap bitmapToEdit) {
        if (mInstance == null) {
            return;
        }
        mInstance.mBitmapToEdit = bitmapToEdit;
    }
    public static Bitmap getBitmapToEdit() {
        if (mInstance == null) {
            return null;
        }
        return mInstance.mBitmapToEdit;
    }

    public static void setBitmapLeft(Bitmap bitmapToEdit) {
        if (mInstance == null) {
            return;
        }
        mInstance.mBitmapLeft = bitmapToEdit;
    }

    public static Bitmap getBitmapLeft() {
        if (mInstance == null) {
            return null;
        }
        return mInstance.mBitmapLeft;
    }

    public static void setBitmapRight(Bitmap bitmapToEdit) {
        if (mInstance == null) {
            return;
        }
        mInstance.mBitmapRight = bitmapToEdit;
    }

    public static Bitmap getBitmapRight() {
        if (mInstance == null) {
            return null;
        }
        return mInstance.mBitmapRight;
    }

    private void createFolder() {

        mPathPhotos = "";
        try {
            File folder = new File(Environment.getExternalStorageDirectory(), getApplicationContext().getString(R.string.app_name));
            folder.mkdir();
            mPathPhotos = folder.getAbsolutePath();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public String getPhotosPath() {
        return mPathPhotos;
    }
}
