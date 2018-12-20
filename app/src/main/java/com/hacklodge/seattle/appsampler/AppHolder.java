package com.hacklodge.seattle.appsampler;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;

/**
 * This class holds information about an app needed to install and launch the app. These include
 * the package name, app name, and apk path.
 */
public class AppHolder {

    private String packageName;
    private String appName;
    private String icon;
    private String apk;

    public AppHolder(String packageName, String appName, String icon, String apk) {
        this.packageName = packageName;
        this.appName = appName;
        this.icon = icon;
        this.apk = apk;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getAppName() {
        return appName;
    }

    public String getIcon() {
        return icon;
    }

    public void loadIcon(ImageView imageView) {
        Picasso.get().load(Uri.parse(icon)).into(imageView);
    }

    public boolean ApkIsDownloaded(Context c) {
        return getApk(c) != null;
    }

    public Uri getApk(Context c) {
        File dir = c.getFilesDir();
        File f = new File(dir, packageName);
        if (f.exists()) {
            return Uri.parse(f.toURI().toString());
        }
        return null;
    }

    public String getApkUrl() {
        return apk;
    }

    @Override
    public boolean equals(Object other) {
        if (! (other instanceof AppHolder)) {
            return false;
        }

        AppHolder otherApp = (AppHolder) other;
        return packageName.equals(otherApp.getPackageName());
    }
}
