package com.hacklodge.seattle.appsampler;

import java.io.File;

/**
 * This class holds information about an app needed to install and launch the app. These include
 * the package name, app name, and apk path.
 */
public class AppHolder {

    String packageName;
    String appName;
    File apkPath;

    public AppHolder(String packageName, String appName, File apkPath) {
        this.packageName = packageName;
        this.appName = appName;
        this.apkPath = apkPath;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getAppName() {
        return appName;
    }

    public File getApkPath() {
        return apkPath;
    }
}
