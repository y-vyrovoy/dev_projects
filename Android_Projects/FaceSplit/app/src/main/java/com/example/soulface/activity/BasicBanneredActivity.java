package com.example.adstry;

import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

public class BasicBanneredActivity extends AppCompatActivity {

    private static final String TAG = BasicBanneredActivity.class.getSimpleName();

    private static final String APP_ID = "ca-app-pub-3940256099942544~3347511713";

    private AdView _adBanner;

    @Override
    public void setContentView (int layoutResID) {

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

        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");

        // Gets the ad view defined in layout/ad_fragment.xml with ad unit ID set in
        // values/strings.xml.
        _adBanner = findViewById(R.id.ad_banner);

        // Create an ad request. Check your logcat output for the hashed device ID to
        // get test ads on a physical device. e.g.
        // "Use AdRequest.Builder.addTestDevice("ABCDEF012345") to get test ads on this device."
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();

        // Start loading the ad in the background.
        _adBanner.loadAd(adRequest);
    }

    /** Called when leaving the activity */
    @Override
    public void onPause() {
        if (_adBanner != null) {
            _adBanner.pause();
        }
        super.onPause();
    }

    /** Called when returning to the activity */
    @Override
    public void onResume() {
        super.onResume();
        if (_adBanner != null) {
            _adBanner.resume();
        }
    }

    /** Called before the activity is destroyed */
    @Override
    public void onDestroy() {
        if (_adBanner != null) {
            _adBanner.destroy();
        }
        super.onDestroy();
    }
}
