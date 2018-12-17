package com.hacklodge.seattle.appsampler;

import android.content.Context;

/**
 * This class is for holding static functions relating to downloading, installing, and uninstalling apps.
 * Utilizes the AppHolder class to pass information about apps to be changed.
 */
public class InstallUtility {

    /**
     * Prompts the user to install the given app
     *
     * @param c the context this function was called from
     * @param app the information about the app to be installed
     */
    public static void install(Context c, AppHolder app) {

    }

    /**
     * Prompts the user to uninstall the given app
     *
     * @param c the context this function was called from
     * @param app the information about the app to be uninstalled
     */
    public static void uninstall(Context c, AppHolder app) {

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
     *
     * @param packageName the package name of the app to be installed
     * @return the AppHolder information about the apk
     */
    public static AppHolder downloadApkAsync(String packageName) {
        return null;
    }
}
