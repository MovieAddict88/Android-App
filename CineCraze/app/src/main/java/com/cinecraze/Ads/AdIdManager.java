package com.cinecraze.Ads;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import com.cinecraze.Ads.AdIdManager;
public class AdIdManager {

    private static final String JSON_URL = "https://raw.githubusercontent.com/MovieAddict88/Movie-Source/main/ads.json";

    private static String appOpenAdUnitId;
    private static String interstitialAdUnitId;
    private static String rewardedVideoAdUnitId;
    private static String bannerAdUnitId;

    public interface AdIdsLoadedCallback {
        void onAdIdsLoaded();
    }

    public static void loadAdIds(Context context, AdIdsLoadedCallback callback) {
        new Thread(() -> {
            android.util.Log.d("AdIdManager", "Loading ad IDs from URL: " + JSON_URL);
            try {
                java.net.URL url = new java.net.URL(JSON_URL);
                java.net.HttpURLConnection connection = (java.net.HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                InputStream is = connection.getInputStream();
                java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(is));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                reader.close();
                is.close();

                String json = sb.toString();
                android.util.Log.d("AdIdManager", "Received JSON: " + json);
                JSONObject jsonObject = new JSONObject(json);
                appOpenAdUnitId = jsonObject.getString("app_open_ad_unit_id");
                interstitialAdUnitId = jsonObject.getString("interstitial_ad_unit_id");
                rewardedVideoAdUnitId = jsonObject.getString("rewarded_video_ad_unit_id");
                bannerAdUnitId = jsonObject.getString("banner_ad_unit_id");
                android.util.Log.d("AdIdManager", "Successfully loaded ad IDs.");
                if (callback != null) {
                    callback.onAdIdsLoaded();
                }
            } catch (IOException | JSONException e) {
                android.util.Log.e("AdIdManager", "Failed to load ad IDs", e);
            }
        }).start();
    }

    public static String getAppOpenAdUnitId() {
        return appOpenAdUnitId;
    }

    public static String getInterstitialAdUnitId() {
        return interstitialAdUnitId;
    }

    public static String getRewardedVideoAdUnitId() {
        return rewardedVideoAdUnitId;
    }

    public static String getBannerAdUnitId() {
        return bannerAdUnitId;
    }
}
