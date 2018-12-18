package com.hacklodge.seattle.appsampler;

import android.content.Context;
import android.content.pm.PackageManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class InstalledAppsManager {

    private static final String INSTALL_LIST_FILENAME = "INSTALLED_APPS_LIST";

    private List<AppHolder> installedPrograms;

    private AppHolder[] platter = new AppHolder[4];

    public InstalledAppsManager(Context c) {
        loadInstalled(c);
        checkForUpdates(c);
    }

    public void addInstalled(Context c, AppHolder app) {
        if (ensureInstalled(c, app.getPackageName())) {
            installedPrograms.add(app);
        }
    }

    public void removeInstalled(Context c, AppHolder app) {
        if (!ensureInstalled(c, app.getPackageName())) {
            installedPrograms.remove(app);
        }
    }

    public boolean isInstalled(Context c, AppHolder app) {
        boolean checker = ensureInstalled(c, app.getPackageName());
        if(checker == true){
            installedPrograms.add(app); 
        }
        return checker;
    }

    public List<AppHolder> shouldBeUninstalled() {
        List<AppHolder> apps = new ArrayList<>();
        for (AppHolder installed : installedPrograms) {
            apps.add(installed);
            for (AppHolder shown : platter) {
                if (installed.equals(shown)) {
                    apps.remove(installed);
                }
            }
        }
        return apps;
    }

    private void loadInstalled(Context c) {
        installedPrograms = new ArrayList<AppHolder>();
        File dir = c.getFilesDir();
        File installList = new File(dir, INSTALL_LIST_FILENAME);
        try {
            byte[] content = Files.readAllBytes(installList.toPath());
            String decodedContent = new String(content);
            String[] parsed = decodedContent.split("\n");
            for (String s : parsed) {
                if (! installedPrograms.contains(s) && ensureInstalled(c, s)) {
                    installedPrograms.add(new AppHolder(s, s, null));
                }
            }
        } catch(IOException e) {
            installList.mkdir();
        }
    }

    private boolean ensureInstalled(Context c, String packageName) {
        boolean found = true;

        try {
            c.getPackageManager().getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            found = false;
        }

        return found;
    }

    private void saveCurrentInstalled(Context c) {
        String content = "";
        for (AppHolder app : installedPrograms) {
            content += app.getPackageName() + "\n";
        }
        try {
            FileOutputStream stream =  c.openFileOutput(INSTALL_LIST_FILENAME, Context.MODE_PRIVATE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks the server for any updates to the platter. If updates are found, propagates them.
     * @return true if updates are found
     */
    public boolean checkForUpdates(Context c) {
        AppHolder[] allApps = loadAppList(c);
        if (allApps == null) throw new NullPointerException("ALL APPS FAILED TO LAOD");

        int platterIndex = 0;//findPlatterIndex();
        if (platterIndex < 0 || platterIndex >= allApps.length - 4) {
            return false;
        }

        boolean updated = false;
        for (int i = 0; i < 4; i++) {
            if (! allApps[i+platterIndex].equals(platter[i])) {
                updated = true;
                platter[i] = allApps[i+platterIndex];
            }
        }

        return updated;
    }

    private AppHolder[] loadAppList(Context c) {
        String json = null;
        try {
            InputStream is = c.getAssets().open("AppList.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }

        try {
            JSONArray array = new JSONArray(json);
            AppHolder[] appArray = new AppHolder[array.length()];
            for (int i = 0; i < array.length(); i++) {
                JSONObject jsonObject = array.getJSONObject(i);

                String name = jsonObject.getString("name");
                String packageName = jsonObject.getString("package");
                String iconURL = jsonObject.getString("icon");
                appArray[i] = new AppHolder(packageName, name, iconURL);
            }

            return appArray;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private int findPlatterIndex() {
        long launchTime = 1545091200;
        Date date = Calendar.getInstance().getTime();
        long elapsedTime = date.getTime() - launchTime;
        return (int) (elapsedTime / 86400000) * 4;
    }

    /**
     * Gets the current platter of apps
     * @return an array representing the current platter of apps
     */
    public AppHolder[] getPlatter() {
        return platter.clone();
    }
}
