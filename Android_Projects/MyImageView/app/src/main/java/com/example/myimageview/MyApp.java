package com.example.myimageview;

import android.app.Application;
import android.graphics.Bitmap;

/**
 * Created by Yura Vyrovoy on 10/17/2017.
 */

public class MyApp extends Application
{
    private static MyApp _instance = null;
    private Bitmap _sourceBitmap;

    @Override
    public void onCreate() {
        super.onCreate();

        _instance = this;
    }


    public MyApp getInstance() {
        return _instance;
    }

    public static void setSourceBitmap(Bitmap bmp) {
        if(_instance == null) {
            return;
        }
        _instance._sourceBitmap = bmp;
    }

    public static Bitmap getSourceBitmap() {
        if(_instance == null) {
            return null;
        }
        return _instance._sourceBitmap;
    }
}
