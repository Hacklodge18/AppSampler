package com.hacklodge.seattle.appsampler;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.gc.android.market.api.MarketSession;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.Scope;

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
        /*Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(app.getApkUri(), "application/vnd.android.package-archive");
        c.startActivity(intent);*/
        Intent goToMarket = new Intent(Intent.ACTION_VIEW)
                .setData(Uri.parse("market://details?id=" + app.getPackageName()));
        c.startActivity(goToMarket);
        manager.addInstalled(c, app);
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
        manager.removeInstalled(c, app);
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

    /**
     * Asynchronously downloads the apk for the given app in the background.
     * File path returned is not guaranteed to exist before apk is finished downloading.
     * If the phone is rooted, it will silently uninstall previous apps and install new ones.
     *
     * @param packageName the package name of the app to be installed
     * @return the AppHolder information about the apk
     */
    public static AppHolder downloadApkAsync(String packageName) {
        MarketSession session = new MarketSession();
        GoogleSignInOptions signIn = new GoogleSignInOptions.Builder()
                .requestScopes(new Scope(Scopes.PROFILE))
                .build();
        return null;
    }
}
