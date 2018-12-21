package com.hacklodge.seattle.appsampler;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;

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

            if (c.getPackageManager().canRequestPackageInstalls()) {
                System.out.println("Installing! " + app.getApk(c).toString());

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(app.getApk(c), "application/vnd.android.package-archive");
                c.startActivity(intent);
            } else {
                c.startActivity(new Intent(android.provider.Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
                        Uri.parse("package:com.hacklodge.seattle.appsampler")));
            }

        } else {
            Intent goToMarket = new Intent(Intent.ACTION_VIEW)
                    .setData(Uri.parse("market://details?id=" + app.getPackageName()));
            c.startActivity(goToMarket);

            cancelDownload(app.getPackageName());
            deleteApk(c, app);
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
        /*final String dlAddress = app.getApkUrl();
        if (dlAddress == null) return;

        if (isWifiEnabled(c) && isExternalStorageWritable()) {

            final int downloadId = PRDownloader.download(dlAddress,
                    c.getExternalCacheDir().toString(),
                    app.getPackageName())
                    .build()
                    .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                        @Override
                        public void onStartOrResume() {
                            System.out.println("Download started for " + app.getAppName());
                        }
                    })
                    .setOnPauseListener(new OnPauseListener() {
                        @Override
                        public void onPause() {
                            System.out.println("Download paused for " + app.getAppName());
                        }
                    })
                    .setOnCancelListener(new OnCancelListener() {
                        @Override
                        public void onCancel() {
                            currentDownloads.remove(app.getPackageName());
                            System.out.println("Download cancelled for " + app.getAppName());
                        }
                    })
                    .setOnProgressListener(new OnProgressListener() {
                        @Override
                        public void onProgress(Progress progress) {
                           System.out.println("Current progress for " + app.getAppName() + ": "
                                   + ((double) progress.currentBytes / (double) progress.totalBytes));
                        }
                    })
                    .start(new OnDownloadListener() {
                        @Override
                        public void onDownloadComplete() {
                            currentDownloads.remove(app.getPackageName());
                            System.out.println("Download success for " + app.getAppName());
                        }

                        @Override
                        public void onError(Error error) {
                            System.out.println("Download failed for " + app.getAppName());
                        }
                    });
            currentDownloads.put(app.getPackageName(), downloadId);
        }*/
    }

    public static void cancelDownload(String packageName) {
        /*if (currentDownloads.containsKey(packageName)) {
            int id = currentDownloads.get(packageName);
            PRDownloader.cancel(id);

        }*/
    }

    public static void deleteApk(Context c, AppHolder app) {
        /*if (isExternalStorageWritable()) {

            Uri apk = app.getApk(c);
            if (apk != null) {
                File f = new File(apk.toString());
                f.delete();
            }
        }*/
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

    /* Checks if external storage is available for read and write */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }
}
