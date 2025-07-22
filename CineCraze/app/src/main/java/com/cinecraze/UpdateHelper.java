package com.cinecraze;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import androidx.core.content.FileProvider;

import android.view.View;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.shashank.sony.fancydialoglib.Animation;
import com.shashank.sony.fancydialoglib.FancyAlertDialog;

import java.io.File;
import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class UpdateHelper {

    private static final String TAG = "UpdateHelper";
    private static final String UPDATE_URL = "https://github.com/MovieAddict88/Movie-Source/raw/main/version.json";

    public static void checkForUpdate(final Activity activity) {
        new Thread(() -> {
            try {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url(UPDATE_URL).build();
                Response response = client.newCall(request).execute();
                if (response.isSuccessful() && response.body() != null) {
                    String json = response.body().string();
                    Gson gson = new Gson();
                    JsonObject jsonObject = gson.fromJson(json, JsonObject.class);

                    String latestVersion = jsonObject.get("latestVersion").getAsString();
                    int latestVersionCode = jsonObject.get("latestVersionCode").getAsInt();
                    String url = jsonObject.get("url").getAsString();

                    PackageInfo pInfo = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0);
                    int currentVersionCode = pInfo.versionCode;

                    if (latestVersionCode > currentVersionCode) {
                        activity.runOnUiThread(() -> showUpdateDialog(activity, latestVersion, url));
                    }
                }
            } catch (IOException | PackageManager.NameNotFoundException e) {
                Log.e(TAG, "Error checking for update", e);
            }
        }).start();
    }

    private static void showUpdateDialog(final Activity activity, String latestVersion, final String url) {
        FancyAlertDialog.Builder.with(activity)
                .setTitle("New Update Available")
                .setMessage("Version " + latestVersion + " is available. Do you want to update?")
                .setPositiveBtnText("Update")
                .setPositiveBtnBackgroundRes(R.color.colorPrimary)
                .setAnimation(Animation.POP)
                .isCancellable(false)
                .setIcon(R.drawable.ic_baseline_exit_to_app_24, View.VISIBLE)
                .onPositiveClicked(dialog -> downloadAndInstall(activity, url))
                .build()
                .show();
    }

    private static void downloadAndInstall(final Activity activity, String url) {
        DownloadManager downloadManager = (DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "app-release.apk");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        final long downloadId = downloadManager.enqueue(request);

        BroadcastReceiver onComplete = new BroadcastReceiver() {
            public void onReceive(Context ctxt, Intent intent) {
                long receivedDownloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (downloadId == receivedDownloadId) {
                    installApk(activity, downloadManager.getUriForDownloadedFile(downloadId));
                    activity.unregisterReceiver(this);
                }
            }
        };
        activity.registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    private static void installApk(Context context, Uri apkUri) {
        if (apkUri != null) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Uri contentUri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", new File(apkUri.getPath()));
                intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
            }
            context.startActivity(intent);
        }
    }
}
