package com.example.soulface.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.example.soulface.DebugLogger;
import com.example.soulface.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

public class BasicBanneredActivity extends AppCompatActivity {

    private static final String TAG = BasicBanneredActivity.class.getSimpleName();

    private static final String APP_AD_ID = "ca-app-pub-3940256099942544~3347511713";
    private static final String BANNER_AD_ID = "ca-app-pub-3940256099942544/6300978111";

    private String _adAppId = APP_AD_ID;
    private String _adBannerId = BANNER_AD_ID;
    private AdView _adBanner;


    @Override
    public void setContentView (int layoutResID) {
        DebugLogger.d();

        ViewGroup viewContent = null;

        try {
            super.setContentView(R.layout.activity_basic_bannred);
            viewContent = findViewById(R.id.content_frame);
        } catch (Exception ex) {}

        if (viewContent != null) {
            getLayoutInflater().inflate(layoutResID, viewContent);
        }else {
            super.setContentView(layoutResID);
        }
    }

    protected void InitializeBanner() {
        DebugLogger.d();

        InitializeBanner(null, null);
    }
    protected void InitializeBanner(String adAppId, String adBannerId) {
        DebugLogger.d();

        RelativeLayout layoutBanner = findViewById(R.id.layout_banner);

        _adAppId = (adAppId != null) ? adAppId : APP_AD_ID;
        _adBannerId = (adBannerId != null) ? adBannerId : BANNER_AD_ID;


        _adBanner = new AdView(this);
        _adBanner.setAdSize(AdSize.SMART_BANNER);
        _adBanner.setAdUnitId(_adBannerId);

        layoutBanner.addView(_adBanner);

        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(this, _adAppId);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();

        // Start loading the ad in the background.
        _adBanner.loadAd(adRequest);
    }

    /** Called when leaving the activity */
    @Override
    public void onPause() {
        DebugLogger.d();

        if (_adBanner != null) {
            _adBanner.pause();
        }
        super.onPause();
    }

    /** Called when returning to the activity */
    @Override
    public void onResume() {
        DebugLogger.d();

        super.onResume();
        if (_adBanner != null) {
            _adBanner.resume();
        }
    }

    /** Called before the activity is destroyed */
    @Override
    public void onDestroy() {
        DebugLogger.d();

        if (_adBanner != null) {
            _adBanner.destroy();
        }
        super.onDestroy();
    }
}
