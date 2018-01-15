package com.example.soulface;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
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

    @Nullable
    public static String saveBitmapToAppFolder(Bitmap bmp, Context context) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_SSS");
        String fileName = sdf.format(new Date(System.currentTimeMillis()));
        File fNew = new File(MyApp.getInstance().getPhotosPath(), fileName + ".png");

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(fNew);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
            out.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
        Toast.makeText(context, context.getResources().getString(R.string.photos_saved), Toast.LENGTH_SHORT).show();
        return fNew.getAbsolutePath();
    }

    public static String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            if (cursor == null) {
                return null;
            }
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @Nullable
    public static String saveBitmapGallery(Bitmap bmp, Context context) {

        String sImageUrl = MediaStore.Images.Media.insertImage(context.getContentResolver(), bmp, "title" , "description");
        Uri savedImageURI = Uri.parse(sImageUrl);
        String sPath = getRealPathFromURI(context, savedImageURI);
        return sPath;
    }

    public static void shareImage(Bitmap bmp, Context context) {

        String sImageUrl = MediaStore.Images.Media.insertImage(context.getContentResolver(), bmp, "title" , "description");
        Uri savedImageURI = Uri.parse(sImageUrl);

        if (savedImageURI != null) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("image/jpg");
            shareIntent.putExtra(Intent.EXTRA_STREAM, savedImageURI);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(Intent.createChooser(shareIntent, "Share"));
        }

    }
}
