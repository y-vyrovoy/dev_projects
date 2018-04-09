package com.example.soulface;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.RelativeLayout;

import com.example.soulface.DebugLogger;
import com.example.soulface.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import java.util.concurrent.atomic.AtomicBoolean;

public class FullScreenAd{

    private static final String APP_AD_ID = "ca-app-pub-3940256099942544~3347511713";
    private static final String SCREEN_AD_ID = "ca-app-pub-3940256099942544/1033173712";

    private long LOAD_AD_TIMEOUT = 10_000;

    private String mAdAppId;
    private String mAdScreenId;
    private InterstitialAd mInterstitialAd;

    private Context mContext;
    private OnAdClosedAction mOnAdCloseAction;
    private OnAdLoadedAction mOnAdLoadedAction;
    private AtomicBoolean mAdIsLoaded = new AtomicBoolean();

    private long mLoadStart;

    public FullScreenAd(Context context) {
        super();
        DebugLogger.d();

        mContext = context;
        initAdIDs(null, null);
    }

    public FullScreenAd(Context context, String adAppId, String adScreenId) {
        super();
        DebugLogger.d();

        mContext = context;
        initAdIDs(adAppId, adScreenId);
    }

    public FullScreenAd(Context context, int adAppId, int adBannerId) {
        super();
        DebugLogger.d();

        mContext = context;
        initAdIDs( context.getResources().getString(adAppId), context.getResources().getString(adBannerId) );
    }

    public void setOnAdLoadedAction(OnAdLoadedAction action) {
        mOnAdLoadedAction = action;
    }

    public long getLoadAdTimeout() {
        return LOAD_AD_TIMEOUT;
    }

    public void setLoadAdTimeout(long timeout) {
        LOAD_AD_TIMEOUT = timeout;
    }

    private void initAdIDs(String adAppId, String adScreenId) {
        DebugLogger.d();

        mAdAppId = (adAppId != null) ? adAppId : APP_AD_ID;
        mAdScreenId = (adScreenId != null) ? adScreenId : SCREEN_AD_ID;

        mInterstitialAd = new InterstitialAd(mContext);
        mInterstitialAd.setAdUnitId(mAdScreenId);

        setInterstitialAdListener();
        mOnAdCloseAction = null;
        mOnAdLoadedAction = null;
    }

    public void loadAd() {
        DebugLogger.d();

        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(mContext, mAdAppId);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();

        // Start loading the ad in the background.
        mAdIsLoaded.set(false);
        mInterstitialAd.loadAd(adRequest);
        mLoadStart = System.currentTimeMillis();
    }

    private void setInterstitialAdListener() {
        DebugLogger.d();

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                DebugLogger.d();

                DebugLogger.d(String.format("Ad loaded in %d ms", System.currentTimeMillis() - mLoadStart));

                mAdIsLoaded.set(true);
                if (mOnAdLoadedAction != null) {
                    mOnAdLoadedAction.onAdLoadedAction();
                }
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                DebugLogger.e("Can't load ad. errorCode" + errorCode);
            }

            @Override
            public void onAdOpened() {
                DebugLogger.d();
            }

            @Override
            public void onAdLeftApplication() {
                DebugLogger.d();
            }

            @Override
            public void onAdClosed() {
                DebugLogger.d();
                if (mOnAdCloseAction != null) {
                    mOnAdCloseAction.onAdCloseAction();
                    loadAd();
                }
            }
        });
    }

   public void showAd(OnAdClosedAction action) {
        DebugLogger.d();
        DebugLogger.d("Ad is loaded: " + mInterstitialAd.isLoaded());

        mOnAdCloseAction = action;
        boolean bShow = false;

        long lStartTime = System.currentTimeMillis();

        while (System.currentTimeMillis() - lStartTime < LOAD_AD_TIMEOUT && bShow == false) {
            if (mInterstitialAd.isLoaded()) {
                bShow = true;
            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {}
        }

        if (bShow) {
            mInterstitialAd.show();
        } else {
            DebugLogger.e("Can't load ad. Starting onAdClosedAction immediately.");
            mOnAdCloseAction.onAdCloseAction();
        }
    }

    public interface OnAdClosedAction {
        void onAdCloseAction();
    }

    public interface OnAdLoadedAction {
        void onAdLoadedAction();
    }
}