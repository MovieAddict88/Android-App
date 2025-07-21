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
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

public class VideoRewardAdManager {
    private RewardedAd mRewardedAd;
    private final Context context;
    private final AdListener adListener;
    private static final String TAG = "RewardedAdManager";

    public interface AdListener {
        void onAdClosed();
        void onUserEarnedReward();
    }

    public VideoRewardAdManager(Context context, AdListener adListener) {
        this.context = context;
        this.adListener = adListener;
        loadAd();
    }

    public void loadAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        RewardedAd.load(
                context,
                AdIdManager.getRewardedVideoAdUnitId(),
                adRequest,
                new RewardedAdLoadCallback() {
                    @Override
                    public void onAdLoaded(RewardedAd rewardedAd) {
                        Log.d(TAG, "Rewarded ad loaded successfully");
                        mRewardedAd = rewardedAd;
                        mRewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
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
                                mRewardedAd = null;
                                loadAd(); // Reload after failure
                            }
                        });
                    }

                    @Override
                    public void onAdFailedToLoad(LoadAdError loadAdError) {
                        Log.e(TAG, "Ad failed to load: " + loadAdError.getMessage());
                        mRewardedAd = null;
                        // Retry after 10 seconds
                        new Handler(Looper.getMainLooper()).postDelayed(() -> loadAd(), 10000);
                    }
                });
    }

    public void showAd(Activity activity) {
        if (mRewardedAd != null) {
            mRewardedAd.show(activity, new OnUserEarnedRewardListener() {
                @Override
                public void onUserEarnedReward(com.google.android.gms.ads.rewarded.RewardItem rewardItem) {
                    Log.d(TAG, "User earned reward");
                    if (adListener != null) {
                        adListener.onUserEarnedReward();
                    }
                }
            });
        } else {
            Log.d(TAG, "Ad not loaded. Calling onAdClosed");
            if (adListener != null) {
                adListener.onAdClosed();
            }
            loadAd(); // Load new ad if not available
        }
    }
}