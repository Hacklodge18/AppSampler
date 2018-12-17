package com.hacklodge.seattle.appsampler;

import android.net.Uri;

import java.io.File;

/**
 * This class holds information about an app needed to install and launch the app. These include
 * the package name, app name, and apk path.
 */
public class AppHolder {

    private String packageName;
    private String appName;
    private Uri apkUri;

    public AppHolder(String packageName, String appName, Uri apkUri) {
        this.packageName = packageName;
        this.appName = appName;
        this.apkUri = apkUri;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getAppName() {
        return appName;
    }

    public Uri getApkUri() {
        return apkUri;
    }
}
