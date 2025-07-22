package com.cinecraze.Ads;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

public class InterstitialAdManager {
    private InterstitialAd mInterstitialAd;
    private final Context context;
    private final AdListener adListener;
    private static final String TAG = "InterstitialAdManager";

    public interface AdListener {
        void onAdClosed();
    }

    public InterstitialAdManager(Context context, AdListener adListener) {
        this.context = context;
        this.adListener = adListener;
    }

    public void loadAd() {
        if (AdIdManager.getInterstitialAdUnitId() == null) {
            Log.e(TAG, "Interstitial Ad Unit ID is null. Cannot load ad.");
            return;
        }
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(
                context,
                AdIdManager.getInterstitialAdUnitId(),
                adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(InterstitialAd interstitialAd) {
                        Log.d(TAG, "Interstitial ad loaded successfully");
                        mInterstitialAd = interstitialAd;
                        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                Log.d(TAG, "Ad dismissed");
                                loadAd(); // Reload after dismissal
                                if (adListener != null) {
                                    adListener.onAdClosed();
                                }
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(AdError adError) {
                                Log.e(TAG, "Failed to show ad: " + adError.getMessage());
                                mInterstitialAd = null;
                                loadAd(); // Reload after failure
                            }
                        });
                    }

                    @Override
                    public void onAdFailedToLoad(LoadAdError loadAdError) {
                        Log.e(TAG, "Ad failed to load: " + loadAdError.getMessage());
                        mInterstitialAd = null;
                        // Retry after 10 seconds
                        new Handler(Looper.getMainLooper()).postDelayed(() -> loadAd(), 10000);
                    }
                });
    }

    public void showAd(Activity activity) {
        if (mInterstitialAd != null) {
            mInterstitialAd.show(activity);
        } else {
            Log.d(TAG, "Ad not loaded. Calling onAdClosed");
            if (adListener != null) {
                adListener.onAdClosed();
            }
            loadAd(); // Load new ad if not available
        }
    }
}