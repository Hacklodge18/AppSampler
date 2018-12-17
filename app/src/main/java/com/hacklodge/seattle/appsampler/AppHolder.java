package com.hacklodge.seattle.appsampler;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Icon;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * This class holds information about an app needed to install and launch the app. These include
 * the package name, app name, and apk path.
 */
public class AppHolder {

    private String packageName;
    private String appName;
    private Bitmap icon;

    public AppHolder(String packageName, String appName, String icon) {
        this.packageName = packageName;
        this.appName = appName;
        //try {
            //URL iconURL = new URL(icon);
            //this.icon = BitmapFactory.decodeStream(iconURL.openConnection().getInputStream());
        //} catch (MalformedURLException e) {} catch (IOException e){}
    }

    public String getPackageName() {
        return packageName;
    }

    public String getAppName() {
        return appName;
    }

    public Icon getIcon() {
        return null;
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
