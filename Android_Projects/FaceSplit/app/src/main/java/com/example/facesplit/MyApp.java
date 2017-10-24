package com.example.facesplit;

import android.app.Application;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.nio.file.Path;

/**
 * Created by Yura Vyrovoy on 10/13/2017.
 */

public class MyApp extends Application {
    private final static String TAG = MyApp.class.getSimpleName();

    private static MyApp _instance = null;

    private Bitmap _bitmapToEdit = null;
    private Bitmap _bitmapLeft = null;
    private Bitmap _bitmapRight = null;

    private String _pathPhotos;

    @Override
    public void onCreate() {
        super.onCreate();
        createFolder();

        _instance = this;
    }

    public static MyApp getInstance() {
        return _instance;
    }

    public static void setBitmapToEdit(Bitmap bitmapToEdit) {
        if (_instance == null) {
            return;
        }
        _instance._bitmapToEdit = bitmapToEdit;
    }
    public static Bitmap getBitmapToEdit() {
        if (_instance == null) {
            return null;
        }
        return _instance._bitmapToEdit;
    }

    public static void setBitmapLeft(Bitmap bitmapToEdit) {
        if (_instance == null) {
            return;
        }
        _instance._bitmapLeft = bitmapToEdit;
    }

    public static Bitmap getBitmapLeft() {
        if (_instance == null) {
            return null;
        }
        return _instance._bitmapLeft;
    }

    public static void setBitmapRight(Bitmap bitmapToEdit) {
        if (_instance == null) {
            return;
        }
        _instance._bitmapRight = bitmapToEdit;
    }

    public static Bitmap getBitmapRight() {
        if (_instance == null) {
            return null;
        }
        return _instance._bitmapRight;
    }

    private void createFolder() {

        String path = String.valueOf(Environment.getExternalStorageDirectory());
        try {
            File root_sd = new File(path);
            File folder = new File(root_sd .getAbsolutePath(), "FaceSplit");

            boolean success = true;
            if (!folder.exists()) {
                success = folder.mkdir();
            }

            if(success) {
                _pathPhotos = folder.getAbsolutePath();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public String getPhotosPath() {
        return _pathPhotos;
    }
}
