package com.example.soulface.activity;

import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.example.soulface.DebugLogger;
import com.example.soulface.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.concurrent.atomic.AtomicBoolean;

public class BasicBanneredActivity extends AppCompatActivity {

    private static final String APP_AD_ID = "ca-app-pub-3940256099942544~3347511713";
    private static final String BANNER_AD_ID = "ca-app-pub-3940256099942544/6300978111";

    private String mAdAppId;
    private String mAdBannerId;

    private AdView mAdBanner;

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

    /**
     * Should be called after super.onCreate()
     */
    protected void InitializeBanner() {
        DebugLogger.d();

        InitializeBanner(null, null);
    }

    /**
     * Should be called after super.onCreate()
     */
    protected void InitializeBanner(int adAppId, int adBannerId) {
        DebugLogger.d();

        InitializeBanner( getResources().getString(adAppId), getResources().getString(adBannerId));
    }

    /**
     * Should be called after super.onCreate()
     */
    protected void InitializeBanner(String adAppId, String adBannerId) {
        DebugLogger.d();

        RelativeLayout layoutBanner = findViewById(R.id.layout_banner);

        mAdAppId = (adAppId != null) ? adAppId : APP_AD_ID;
        mAdBannerId = (adBannerId != null) ? adBannerId : BANNER_AD_ID;


        mAdBanner = new AdView(this);
        mAdBanner.setAdSize(AdSize.SMART_BANNER);
        mAdBanner.setAdUnitId(mAdBannerId);

        layoutBanner.addView(mAdBanner);

        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(this, mAdAppId);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();

        // Start loading the ad in the background.
        mAdBanner.loadAd(adRequest);
    }

    /** Called when leaving the activity */
    @Override
    public void onPause() {
        DebugLogger.d();

        if (mAdBanner != null) {
            mAdBanner.pause();
        }
        super.onPause();
    }

    /** Called when returning to the activity */
    @Override
    public void onResume() {
        DebugLogger.d();

        super.onResume();
        if (mAdBanner != null) {
            mAdBanner.resume();
        }
    }

    /** Called before the activity is destroyed */
    @Override
    public void onDestroy() {
        DebugLogger.d();

        if (mAdBanner != null) {
            mAdBanner.destroy();
        }
        super.onDestroy();
    }
}
