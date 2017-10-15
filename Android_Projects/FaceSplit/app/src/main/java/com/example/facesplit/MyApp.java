package com.example.facesplit;

import android.app.Application;
import android.graphics.Bitmap;

/**
 * Created by Yura Vyrovoy on 10/13/2017.
 */

public class MyApp extends Application {

    private static MyApp _instance = null;

    private Bitmap _bitmapToEdit = null;
    private Bitmap _bitmapLeft = null;
    private Bitmap _bitmapRight = null;

    @Override
    public void onCreate() {
        super.onCreate();

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

}
