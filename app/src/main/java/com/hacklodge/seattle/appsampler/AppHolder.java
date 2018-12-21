package com.hacklodge.seattle.appsampler;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.jar.JarFile;

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
        Uri uri = getApk(c);
        if (uri != null) {
            return isApkCorrupt(uri);
        }
        return false;
    }

    public Uri getApk(Context c) {
        /*if (InstallUtility.isExternalStorageReadable()) {
            File dir = c.getExternalCacheDir();
            File f = new File(dir, packageName);
            if (f.exists()) {
                //return null;
                return FileProvider.getUriForFile(c,
                        c.getApplicationContext().getPackageName() + ".com.hacklodge.seattle.provider",
                        f );

            }
            return null;
        } else {
            return null;
        }*/
        return null;
    }

    public static boolean isApkCorrupt(Uri filepath) {
        boolean corruptedApkFile = false;
        try {
            new JarFile(filepath.toString()); //Detect if the file have been corrupted
        } catch (Exception ex) {
            corruptedApkFile = true;
        }

        return corruptedApkFile;
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
