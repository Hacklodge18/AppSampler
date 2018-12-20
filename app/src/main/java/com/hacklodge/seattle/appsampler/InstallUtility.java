package com.hacklodge.seattle.appsampler;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.Uri;

import com.downloader.Error;
import com.downloader.OnCancelListener;
import com.downloader.OnDownloadListener;
import com.downloader.OnPauseListener;
import com.downloader.OnProgressListener;
import com.downloader.OnStartOrResumeListener;
import com.downloader.PRDownloader;
import com.downloader.Progress;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is for holding static functions relating to downloading, installing, and uninstalling apps.
 * Utilizes the AppHolder class to pass information about apps to be changed.
 */
public class InstallUtility {

    /**
     * Launches the given app, iff it is installed
     * @param c the context this function was called from
     * @param app the app that will be launched
     */
    public static void launch(Context c, AppHolder app) {
        try {
            Intent intent = c.getPackageManager().getLaunchIntentForPackage(app.getPackageName());
            c.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Prompts the user to install the given app
     *
     * @param c the context this function was called from
     * @param app the information about the app to be installed
     */
    public static void install(Context c, AppHolder app, InstalledAppsManager manager) {
        if (app.ApkIsDownloaded(c)) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(app.getApk(c), "application/vnd.android.package-archive");
            c.startActivity(intent);
        } else {
            Intent goToMarket = new Intent(Intent.ACTION_VIEW)
                    .setData(Uri.parse("market://details?id=" + app.getPackageName()));
            c.startActivity(goToMarket);

            //cancelDownload(app.getPackageName());
            //deleteApk(c, app.getPackageName());
        }

//        Intent browserIntent = new Intent(c, Browser.class);
//        Bundle b = new Bundle();
//        b.putString("package", app.getPackageName());
//        browserIntent.putExtras(b);
//        c.startActivity(browserIntent);

        manager.addInstalled(app);
    }

    /**
     * Prompts the user to uninstall the given app
     *
     * @param c the context this function was called from
     * @param app the information about the app to be uninstalled
     */
    public static void uninstall(Context c, AppHolder app, InstalledAppsManager manager) {
        Intent intent = new Intent(Intent.ACTION_DELETE);
        intent.setData(Uri.parse("package:"+app.getPackageName()));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        c.startActivity(intent);
        manager.removeInstalled(app);
    }

    /**
     * Will attempt to silently install app in background, if phone is rooted
     *
     * @param c the context this function was called from
     * @param app the information about the app to be installed
     */
    public static void trySilentInstall(Context c, AppHolder app) {

    }

    /**
     * Will attempt to silently uninstall in background, if phone is rooted
     * @param c the context this function was called from
     * @param app the information about the app to be uninstalled
     */
    public static void trySilentUninstall(Context c, AppHolder app) {

    }

    private static Map<String, Integer> currentDownloads = new HashMap<>();

    /**
     * Asynchronously downloads the apk for the given app in the background.
     * File path returned is not guaranteed to exist before apk is finished downloading.
     * If the phone is rooted, it will silently uninstall previous apps and install new ones.
     *
     * @param app the app information for the app that will be downloaded
     */
    public static void downloadApkAsync(Context c, final AppHolder app) {
        final String dlAddress = app.getApkUrl();

        if (isWifiEnabled(c)) {

            final int downloadId = PRDownloader.download(dlAddress,
                    c.getFilesDir().toString(),
                    app.getPackageName())
                    .build()
                    .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                        @Override
                        public void onStartOrResume() {
                            System.out.println("Download started for " + dlAddress);
                        }
                    })
                    .setOnPauseListener(new OnPauseListener() {
                        @Override
                        public void onPause() {
                            System.out.println("Download paused for " + dlAddress);
                        }
                    })
                    .setOnCancelListener(new OnCancelListener() {
                        @Override
                        public void onCancel() {
                            currentDownloads.remove(app.getPackageName());
                            System.out.println("Download cancelled for " + dlAddress);
                        }
                    })
                    .setOnProgressListener(new OnProgressListener() {
                        @Override
                        public void onProgress(Progress progress) {

                        }
                    })
                    .start(new OnDownloadListener() {
                        @Override
                        public void onDownloadComplete() {
                            currentDownloads.remove(app.getPackageName());
                            System.out.println("Download success for " + dlAddress);
                        }

                        @Override
                        public void onError(Error error) {
                            System.out.println("Download failed for " + dlAddress);
                        }
                    });
            currentDownloads.put(app.getPackageName(), downloadId);
        }
    }

    public static void cancelDownload(String packageName) {
        if (currentDownloads.containsKey(packageName)) {
            System.out.println(packageName + "AAAB");
            PRDownloader.cancel(currentDownloads.get(packageName));
        }
    }

    public static void deleteApk(Context c, String packageName) {
        File f = new File(c.getFilesDir(), packageName);
        if (f.exists()) {
            f.delete();
        } else {
            System.out.println("File " + packageName + " does not exist!");
        }
    }

    public static boolean isWifiEnabled(Context c) {
        ConnectivityManager connectionManager = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        for (Network network : connectionManager.getAllNetworks()) {
            NetworkInfo info = connectionManager.getNetworkInfo(network);
            if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                return true;
            }
        }
        return false;
    }
}
