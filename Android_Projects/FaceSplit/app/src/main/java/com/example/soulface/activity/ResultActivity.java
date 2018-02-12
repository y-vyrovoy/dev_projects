package com.example.soulface.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.soulface.BitmapUtils;
import com.example.soulface.MyApp;
import com.example.soulface.R;

public class ResultActivity extends AppCompatActivity {
    private static final String TAG = ResultActivity.class.getSimpleName();
    private static final double RESULT_VIEW_PHOTO_RATIO = 0.75;
    private static final int ROUND_RADIUS = 40;

    private View mLeftViewTop;
    private View mLeftViewBottom;
    private View mRightViewTop;
    private View mRightViewBottom;
    private ProgressBar mProgressBar;

    private int mScreenWidth;
    private boolean mLeftOnTop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        mScreenWidth = displayMetrics.widthPixels;

        LayoutInflater layoutInflater = LayoutInflater.from(this);

        mLeftViewTop = layoutInflater.inflate(R.layout.layout_left_photo_top, null);
        setSizedDrawable (mLeftViewTop, BitmapUtils.getRoundedCornerBitmap( MyApp.getBitmapLeft(), ROUND_RADIUS ));

        mLeftViewBottom = layoutInflater.inflate(R.layout.layout_left_photo_bottom, null);
        setSizedDrawable (mLeftViewBottom, BitmapUtils.getRoundedCornerBitmap( MyApp.getBitmapLeft(), ROUND_RADIUS ));

        mRightViewTop = layoutInflater.inflate(R.layout.layout_right_photo_top, null);
        setSizedDrawable (mRightViewTop, BitmapUtils.getRoundedCornerBitmap( MyApp.getBitmapRight(), ROUND_RADIUS ));

        mRightViewBottom = layoutInflater.inflate(R.layout.layout_right_photo_bottom, null);
        setSizedDrawable (mRightViewBottom, BitmapUtils.getRoundedCornerBitmap( MyApp.getBitmapRight(), ROUND_RADIUS ));

        mProgressBar = findViewById(R.id.progressBar);
    }

    public void onStart() {
        super.onStart();
        doLayout(true);
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    private void doLayout(boolean leftOnTop){

        if (mLeftOnTop == leftOnTop) {
            return;
        }
        mLeftOnTop = leftOnTop;

        RelativeLayout layoutRoot = findViewById(R.id.layout_root);
        if (layoutRoot == null) {
            Log.e(TAG, "Can't find root layout. doLayout() terminated");
            return;
        }
        layoutRoot.removeAllViews();

        if (leftOnTop) {
            layoutRoot.addView(mRightViewBottom);
            layoutRoot.addView(mLeftViewTop);
        } else {
            layoutRoot.addView(mLeftViewBottom);
            layoutRoot.addView(mRightViewTop);
        }
    }

    private void setSizedDrawable(View view, Bitmap bitmapSrc) {

        ImageView imageView = view.findViewById(R.id.photo);

        if (imageView != null) {
            try {
                double desiredWidth = mScreenWidth * RESULT_VIEW_PHOTO_RATIO;
                int bmpWidth = MyApp.getBitmapLeft().getWidth();
                double ratio = desiredWidth / bmpWidth;

                int imageWidth = (int) (bitmapSrc.getWidth() * ratio);
                int imageHeight = (int) (bitmapSrc.getHeight() * ratio);

                Bitmap bitmapDest = Bitmap.createScaledBitmap(bitmapSrc, imageWidth, imageHeight, false);
                imageView.setImageBitmap(bitmapDest);
            } catch (Exception ex) {}

        }
    }

    public void onBtnSave(View v) {
        Bitmap bmpToSave = null;
        if (v == mLeftViewTop.findViewById(R.id.btn_save_left)) {
            bmpToSave = MyApp.getBitmapLeft();
        } else if (v == mRightViewTop.findViewById(R.id.btn_save_right)) {
            bmpToSave = MyApp.getBitmapRight();
        }

        if (bmpToSave != null) {
            mProgressBar.setVisibility(View.VISIBLE);
            BitmapUtils.saveBitmapGallery(bmpToSave, this);
            mProgressBar.setVisibility(View.INVISIBLE);
            v.setVisibility(View.INVISIBLE);
            Toast.makeText(this, R.string.image_saved, Toast.LENGTH_SHORT).show();
        }
    }

    public void onBtnShare(View v) {
        mProgressBar.setVisibility(View.VISIBLE);
        Bitmap bmpToShare = null;
        if (v == mLeftViewTop.findViewById(R.id.btn_share_left)) {
            bmpToShare = MyApp.getBitmapLeft();
        } else if (v == mRightViewTop.findViewById(R.id.btn_share_right)) {
            bmpToShare = MyApp.getBitmapRight();
        }

        if (bmpToShare != null) {
            BitmapUtils.shareImage(bmpToShare,
                                    this,
                                    ()-> {
                                        mProgressBar.setVisibility(View.INVISIBLE);
                                        Toast.makeText(this, R.string.image_saved, Toast.LENGTH_SHORT).show();
                                    },
                                    null);
        }
    }

    public void onBtnVrMode(View v) {
        Intent intent = new Intent(this, VrModeActivity.class);
        startActivity(intent);
    }

    public void onBtnBack(View v) {
        onBackPressed();
    }


    public void onImageClick(View v) {
        if (v == mLeftViewBottom.findViewById(R.id.photo)) {
            doLayout(true);
        } else if (v == mRightViewBottom.findViewById(R.id.photo)) {
            doLayout(false);
        }
    }


}
