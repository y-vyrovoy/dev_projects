package com.example.facesplit;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;

/**
 * Created by Yura Vyrovoy on 10/13/2017.
 */

public class ImageEditor extends View {

    private Bitmap _bitmapSource = null;

    public ImageEditor(Context context) {
        super(context);
    }

    public void setBitmapSource(Bitmap bmp) {
        _bitmapSource = bmp;
    }

    @Override
    public void onDraw(Canvas canvas) {

        int width = getWidth();
        int height = getHeight();


    }

}