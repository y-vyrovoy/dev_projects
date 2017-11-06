package com.example.facesplit;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Yura Vyrovoy on 10/15/2017.
 */

public class BitmapUtils {
    private static final String TAG = BitmapUtils.class.getSimpleName();

    public static Bitmap getDoubledBitmap(Bitmap bmpSource, boolean bLeft) {

        Bitmap bmpFlipped = getFlippedBitmap(bmpSource);

        Bitmap bmpResult = Bitmap.createBitmap(bmpSource.getWidth() * 2,
                                                bmpSource.getHeight(),
                                                bmpSource.getConfig());

        Canvas canvas = new Canvas(bmpResult);
        if(bLeft == true) {
            canvas.drawBitmap(bmpSource, 0f, 0f, null);
            canvas.drawBitmap(bmpFlipped, bmpSource.getWidth(), 0, null);
        } else {
            canvas.drawBitmap(bmpFlipped, 0f, 0f, null);
            canvas.drawBitmap(bmpSource, bmpSource.getWidth(), 0, null);
        }
        return  bmpResult;
    }

    private static Bitmap getFlippedBitmap(Bitmap bmp) {
        Matrix matrix = new Matrix();

        matrix.preScale(-1, 1);

        Bitmap bmpResult = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, false);
        return bmpResult;
    }

    public static Bitmap getLeftSideBitmap(Bitmap bmpSource) {

        return Bitmap.createBitmap(bmpSource,
                0, 0,
                bmpSource.getWidth()/2,
                bmpSource.getHeight());

    }

    public static Bitmap getRightSideBitmap(Bitmap bmpSource) {

        return Bitmap.createBitmap(bmpSource,
                bmpSource.getWidth()/2, 0,
                bmpSource.getWidth()/2,
                bmpSource.getHeight());

    }

    public static Bitmap getDoubledLeftPart(Bitmap source) {
        return getDoubledBitmap(getLeftSideBitmap(source), true);
    }

    public static Bitmap getDoubledRightPart(Bitmap source) {
        return getDoubledBitmap(getRightSideBitmap(source), false);
    }

    public static void saveBitmap(Bitmap bmp, String prefix, Context context) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd-HH_mm");
        String fileName   = sdf.format(new Date(System.currentTimeMillis()));
        File fNew = new File(MyApp.getInstance().getPhotosPath(), fileName + " (" + prefix + ").jpg");

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(fNew);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
            out.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            try {
                if (out != null) {
                    out.close();
                }
                return;
            } catch (IOException ex2) {
                ex2.printStackTrace();
                return;
            }
        }
        Toast.makeText(context, context.getResources().getString(R.string.photos_saved), Toast.LENGTH_SHORT).show();
    }

}
