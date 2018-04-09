package com.example.soulface.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.example.soulface.BitmapUtils;
import com.example.soulface.DebugLogger;
import com.example.soulface.FullScreenAd;
import com.example.soulface.SoulFaceApp;
import com.example.soulface.R;

public class ResultActivity extends BasicBanneredActivity {
    private static final String TAG = ResultActivity.class.getSimpleName();
    private static final double RESULT_VIEW_PHOTO_RATIO = 0.6;
    private static final int ROUND_RADIUS = 40;

    private Handler mHandler = new Handler();

    private View mLeftViewTop;
    private View mLeftViewBottom;
    private View mRightViewTop;
    private View mRightViewBottom;
    private ProgressBar mProgressBar;
    private ImageView mImageSaved;
    private FullScreenAd mFullScreenAd;

    private int mScreenWidth;
    private boolean mLeftOnTop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        DebugLogger.d();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        InitializeBanner();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        mScreenWidth = displayMetrics.widthPixels;

        LayoutInflater layoutInflater = LayoutInflater.from(this);

        mLeftViewTop = layoutInflater.inflate(R.layout.layout_left_photo_top, null);
        setSizedDrawable (mLeftViewTop, BitmapUtils.getRoundedCornerBitmap( SoulFaceApp.getBitmapLeft(), ROUND_RADIUS, true, this ));

        mLeftViewBottom = layoutInflater.inflate(R.layout.layout_left_photo_bottom, null);
        setSizedDrawable (mLeftViewBottom, BitmapUtils.getRoundedCornerBitmap( SoulFaceApp.getBitmapLeft(), ROUND_RADIUS, true, this ));

        mRightViewTop = layoutInflater.inflate(R.layout.layout_right_photo_top, null);
        setSizedDrawable (mRightViewTop, BitmapUtils.getRoundedCornerBitmap( SoulFaceApp.getBitmapRight(), ROUND_RADIUS, true, this ));

        mRightViewBottom = layoutInflater.inflate(R.layout.layout_right_photo_bottom, null);
        setSizedDrawable (mRightViewBottom, BitmapUtils.getRoundedCornerBitmap( SoulFaceApp.getBitmapRight(), ROUND_RADIUS, true, this ));

        mProgressBar = findViewById(R.id.progressBar);
        mImageSaved = findViewById(R.id.image_saved);

        mFullScreenAd = SoulFaceApp.getInstance().getPreloadedAd();
    }

    public void onStart() {
        DebugLogger.d();

        super.onStart();

        doLayout(true);
        mProgressBar.setVisibility(View.INVISIBLE);
        mImageSaved.setVisibility(View.INVISIBLE);
    }

    private void doLayout(boolean leftOnTop){
        DebugLogger.d();

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
        DebugLogger.d();

        ImageView imageView = view.findViewById(R.id.photo);

        if (imageView != null) {
            try {
                double desiredWidth = mScreenWidth * RESULT_VIEW_PHOTO_RATIO;
                int bmpWidth = SoulFaceApp.getBitmapLeft().getWidth();
                double ratio = desiredWidth / bmpWidth;

                int imageWidth = (int) (bitmapSrc.getWidth() * ratio);
                int imageHeight = (int) (bitmapSrc.getHeight() * ratio);

                Bitmap bitmapDest = Bitmap.createScaledBitmap(bitmapSrc, imageWidth, imageHeight, false);
                imageView.setImageBitmap(bitmapDest);
            } catch (Exception ex) {}

        }
    }

    public void onBtnSave(View v) {
        DebugLogger.d();

        Bitmap bmpToSave = null;
        if (v == mLeftViewTop.findViewById(R.id.btn_save_left)) {
            bmpToSave = SoulFaceApp.getBitmapLeft();
        } else if (v == mRightViewTop.findViewById(R.id.btn_save_right)) {
            bmpToSave = SoulFaceApp.getBitmapRight();
        }

        if (bmpToSave != null) {
            mProgressBar.setVisibility(View.VISIBLE);
            BitmapUtils.saveBitmapGallery(bmpToSave, this);
            mProgressBar.setVisibility(View.INVISIBLE);
            v.setVisibility(View.INVISIBLE);

            mImageSaved.setVisibility(View.VISIBLE);
            mHandler.postDelayed(() -> mImageSaved.setVisibility(View.INVISIBLE), 1000);
        }
    }

    public void onBtnShare(View v) {
        DebugLogger.d();

        mProgressBar.setVisibility(View.VISIBLE);
        Bitmap bmpToShare = null;
        if (v == mLeftViewTop.findViewById(R.id.btn_share_left)) {
            bmpToShare = SoulFaceApp.getBitmapLeft();
        } else if (v == mRightViewTop.findViewById(R.id.btn_share_right)) {
            bmpToShare = SoulFaceApp.getBitmapRight();
        }

        if (bmpToShare != null) {
            BitmapUtils.shareImage(bmpToShare,
                                    this,
                                    ()-> {
                                        mProgressBar.setVisibility(View.INVISIBLE);
                                        mImageSaved.setVisibility(View.VISIBLE);
                                        mHandler.postDelayed(() -> mImageSaved.setVisibility(View.INVISIBLE), 1000);
                                    },
                                    null);
        }
    }

    public void onBtnVrMode(View v) {
        DebugLogger.d();

        mFullScreenAd.showAd(()->{
            Intent intent = new Intent(this, VrModeActivity.class);
            startActivity(intent);
        });
    }

    public void onBtnSingleMode(View v) {
        DebugLogger.d();

        mFullScreenAd.showAd(()->{
            Intent intent = new Intent(this, SingleResultActivity.class);
            startActivity(intent);
        });
    }

    public void onBtnBack(View v) {
        DebugLogger.d();
        onBackPressed();
    }

    public void onImageClick(View v) {
        DebugLogger.d();

        if (v == mLeftViewBottom.findViewById(R.id.photo)) {
            doLayout(true);
        } else if (v == mRightViewBottom.findViewById(R.id.photo)) {
            doLayout(false);
        }
    }


}
