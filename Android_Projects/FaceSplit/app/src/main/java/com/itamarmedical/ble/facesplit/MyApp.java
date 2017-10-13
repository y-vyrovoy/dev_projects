package com.itamarmedical.ble.facesplit;

import android.app.Application;
import android.graphics.Bitmap;

/**
 * Created by Yura Vyrovoy on 10/13/2017.
 */

public class MyApp extends Application {

    private Bitmap _bitmapToEdit = null;
    private static MyApp _instance = null;

    @Override
    public void onCreate() {
        super.onCreate();

        _instance = this;
    }

    public static void setBitmapToEdit(Bitmap bitmapToEdit) {
        if (_instance != null) {
            _instance._bitmapToEdit = bitmapToEdit;
        }
    }
    public static Bitmap getBitmapToEdit() {
        if (_instance != null) {
            return _instance._bitmapToEdit;
        } else {
            return null;
        }
    }

}
