package com.cinecraze;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class AdBlocker {

    private static final String TAG = "AdBlocker";
    private static final String AD_HOSTS_URL = "https://github.com/MovieAddict88/Movie-Source/raw/main/easylist.txt";
    private static final String AD_HOSTS_FILE = "easylist.txt";
    private static final Set<String> AD_HOSTS = new HashSet<>();
    private static final Set<String> VIDEO_WHITELIST = new HashSet<>(Arrays.asList(
            "vidsrc.net", "vidjoy.pro", "vidcloud.pro", "mycloud.blue", "playhydrax.com"
    ));

    public static void init(Context context) {
        new Thread(() -> {
            try {
                File cacheFile = new File(context.getCacheDir(), AD_HOSTS_FILE);
                if (cacheFile.exists()) {
                    loadHostsFromFile(cacheFile);
                } else {
                    loadHostsFromUrl(cacheFile);
                }
            } catch (IOException e) {
                Log.e(TAG, "Error initializing AdBlocker", e);
            }
        }).start();
    }

    private static void loadHostsFromFile(File file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;
        while ((line = reader.readLine()) != null) {
            processLine(line);
        }
        reader.close();
        Log.d(TAG, "AdBlocker initialized from cache with " + AD_HOSTS.size() + " hosts.");
    }

    private static void loadHostsFromUrl(File cacheFile) throws IOException {
        URL url = new URL(AD_HOSTS_URL);
        BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
        FileWriter writer = new FileWriter(cacheFile);
        String line;
        while ((line = reader.readLine()) != null) {
            writer.write(line + "\n");
            processLine(line);
        }
        reader.close();
        writer.close();
        Log.d(TAG, "AdBlocker initialized from URL with " + AD_HOSTS.size() + " hosts.");
    }

    private static void processLine(String line) {
        if (line.startsWith("||") && line.endsWith("^")) {
            String host = line.substring(2, line.length() - 1);
            // Skip if host is in video whitelist
            if (!VIDEO_WHITELIST.contains(host)) {
                AD_HOSTS.add(host);
            }
        }
    }

    public static boolean isAd(String url) {
        if (url == null || url.isEmpty()) {
            return false;
        }
        String host = Uri.parse(url).getHost();
        if (host == null) {
            return false;
        }
        
        // Always allow video sources
        if (VIDEO_WHITELIST.contains(host)) {
            return false;
        }
        
        // Check host and parent domains
        while (host.contains(".")) {
            if (AD_HOSTS.contains(host)) {
                return true;
            }
            host = host.substring(host.indexOf('.') + 1);
        }
        return false;
    }
}