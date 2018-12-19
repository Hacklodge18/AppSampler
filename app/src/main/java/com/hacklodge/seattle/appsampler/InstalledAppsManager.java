package com.hacklodge.seattle.appsampler;

import android.content.Context;
import android.content.pm.PackageManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class InstalledAppsManager {

    private static final String INSTALL_LIST_FILENAME = "INSTALLED_APPS_LIST.json";

    private static final String PLATTER_FILENAME = "PLATTER_SAVE_DATA.json";

    private Set<AppHolder> installedPrograms;

    private AppHolder[] platter = new AppHolder[4];
    public InstalledAppsManager(Context c) {
        loadInstalled(c);
        loadPlatter(c);
        savePlatter(c);
    }

    public void addInstalled(Context c, AppHolder app) {
        if (ensureInstalled(c, app.getPackageName())) {
            installedPrograms.add(app);
            saveCurrentInstalled(c);
        }
    }

    public void removeInstalled(Context c, AppHolder app) {
        if (!ensureInstalled(c, app.getPackageName())) {
            installedPrograms.remove(app);
            saveCurrentInstalled(c);
        }
    }

    public void keep(Context c, AppHolder app) {
        if (isInstalled(c, app)) {
            installedPrograms.remove(app);
            saveCurrentInstalled(c);
        }
    }

    public boolean isInstalled(Context c, AppHolder app) {
        boolean checker = ensureInstalled(c, app.getPackageName());
        if(checker){
            installedPrograms.add(app);
            saveCurrentInstalled(c);
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
        installedPrograms = new HashSet<>();
        File dir = c.getFilesDir();
        File installList = new File(dir, INSTALL_LIST_FILENAME);

        try {
            byte[] content = Files.readAllBytes(installList.toPath());
            String decodedContent = new String(content);
            AppHolder[] arr = loadFromJSON(decodedContent);
            if (arr == null) return;
            for (AppHolder app : arr) {
                if (! installedPrograms.contains(app) && ensureInstalled(c, app.getPackageName())) {
                    installedPrograms.add(app);
                }
            }
        } catch(IOException e) {
            try {
                installList.createNewFile();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }
    }

    private void loadPlatter(Context c) {
        File dir = c.getFilesDir();
        File platterList = new File(dir, PLATTER_FILENAME);
        try {
            byte[] content = Files.readAllBytes(platterList.toPath());
            String decodedContent = new String(content);
            AppHolder[] arr = loadFromJSON(decodedContent);
            if (arr == null) return;

            for (int i = 0; i < arr.length; i++) {
                if (i >= platter.length) break;
                platter[i] = arr[i];
            }
        } catch(IOException e) {
            try {
                platterList.createNewFile();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
            cycle(c);
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

    public void saveCurrentInstalled(Context c) {
        saveAppHolders(c, installedPrograms.toArray(new AppHolder[0]), INSTALL_LIST_FILENAME);
    }

    public void savePlatter(Context c) {
        saveAppHolders(c, platter, PLATTER_FILENAME);
    }

    private void saveAppHolders(Context c, AppHolder[] apps, String filename) {
        JSONArray array = new JSONArray();
        for (AppHolder app : apps) {
            JSONObject obj = new JSONObject();
            try {
                obj.put("name", app.getAppName());
                obj.put("package", app.getPackageName());
                obj.put("icon", app.getIcon());
            } catch (JSONException e) {continue;}
            array.put(obj);
        }

        File dir = c.getFilesDir();
        File file = new File(dir, filename);
        try {
            BufferedWriter output = new BufferedWriter(new FileWriter(file));
            output.write(array.toString());
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
            try {
                file.createNewFile();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }
    }

    /**
     * Updates the current platter with new, randomly selected games.
     * @param c the context this function was called from
     * @return the current platter
     */
    public AppHolder[] cycle(Context c) {
        AppHolder[] allApps = loadAppList(c);
        if (allApps == null) throw new NullPointerException("ALL APPS FAILED TO LAOD");

        for (int i = 0; i < platter.length; i++) {
            int randIndex = (int) (Math.random()*allApps.length);
            platter[i] = allApps[randIndex];
        }

        savePlatter(c);
        return getPlatter();
    }

    /**
     * @deprecated Use the cycle(c) command to instantly cycle platter
     * Checks the server for any updates to the platter. If updates are found, propagates them.
     * @return true if updates are found
     */
    @Deprecated
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
        try {
            String json = loadJSONFile(c.getAssets().open("AppList.json"));
            return loadFromJSON(json);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String loadJSONFile(InputStream file) {
        String json = null;
        try {
            int size = file.available();
            byte[] buffer = new byte[size];
            file.read(buffer);
            file.close();
            return new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private AppHolder[] loadFromJSON(String json) {
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
    public boolean inInstalledProgram(AppHolder a){
        return installedPrograms.contains(a);
    }
}
