package com.hacklodge.seattle.appsampler;

import android.net.Uri;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * This class holds information about an app needed to install and launch the app. These include
 * the package name, app name, and apk path.
 */
public class AppHolder {

    private String packageName;
    private String appName;
    private String icon;
    private boolean installed = false;

    public AppHolder(String packageName, String appName, String icon) {
        this.packageName = packageName;
        this.appName = appName;
        this.icon = icon;
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

    public boolean getInstalled(){return installed;}

    public void loadIcon(ImageView imageView) {
        Picasso.get().load(Uri.parse(icon)).into(imageView);
    }

    @Override
    public boolean equals(Object other) {
        if (! (other instanceof AppHolder)) {
            return false;
        }

        AppHolder otherApp = (AppHolder) other;
        return packageName.equals(otherApp.getPackageName());
    }
    public void setInstalled(boolean i){
        installed = i;
    }
}
