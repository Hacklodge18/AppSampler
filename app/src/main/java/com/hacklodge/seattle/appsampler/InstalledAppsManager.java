package com.hacklodge.seattle.appsampler;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.ArraySet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
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

    private static final String FAVORITES_FILENAME = "HERE_ARE_THE_FAVES.json";

    private static final String HAS_BEEN_CLICKED_FILENAME = "HAS_BEEN_CLICKED_FILENAME.json";

    private Set<AppHolder> installedPrograms;

    private Set<AppHolder> hasBeenClicked;

    private Set<AppHolder> favorites;

    private Context context;

    private AppHolder[] platter = new AppHolder[4];
    public InstalledAppsManager(Context c) {
        context = c;
        hasBeenClicked = new ArraySet<AppHolder>();
        installedPrograms = load(INSTALL_LIST_FILENAME);
        favorites = load(FAVORITES_FILENAME);
        loadPlatter();
        savePlatter();
    }

    public void updateInstalled(AppHolder app) {
        if (ensureInstalled(app.getPackageName()) && !installedPrograms.contains(app)) {
            addInstalled(app);
        }
        if (!ensureInstalled(app.getPackageName()) && installedPrograms.contains(app)) {
            removeInstalled(app);
        }
    }

    public void addInstalled(AppHolder app) {
        hasBeenClicked.add(app);
        if (ensureInstalled(app.getPackageName())) {
            installedPrograms.add(app);
            saveCurrentInstalled();
        }
    }

    public void removeInstalled(AppHolder app) {
        if (!ensureInstalled(app.getPackageName())) {
            installedPrograms.remove(app);
            saveCurrentInstalled();
        }
    }

    public void addFavorite(AppHolder app) {
        if (isInstalled(app)) {
            favorites.add(app);
            saveCurrentInstalled();
            saveFavorites();
        }
    }

    public void removeFavorite(AppHolder app) {
        favorites.remove(app);
        saveFavorites();
        if (isInstalled(app) && !inPlatter(app)) {
            InstallUtility.uninstall(context, app, this);
        }
    }

    public AppHolder[] getFavorites() {
        return favorites.toArray(new AppHolder[0]);
    }

    public boolean isFavorite(AppHolder app) {
        for (AppHolder check : favorites) {
            if (check.equals(app)) {
                return true;
            }
        }
        return false;
    }

    public void update() {
        for (AppHolder app : installedPrograms.toArray(new AppHolder[0])) {
            if (! ensureInstalled(app.getPackageName())) {
                installedPrograms.remove(app);
                favorites.remove(app);
            }
        }
    }

    /**
     * Checks to see if the given app is currently installed
     *
     * @param app
     * @return
     */
    public boolean isInstalled(AppHolder app) {
        return ensureInstalled(app.getPackageName());//installedPrograms.contains(app);
    }

    public List<AppHolder> shouldBeUninstalled() {
        List<AppHolder> apps = new ArrayList<>();
        for (AppHolder installed : hasBeenClicked) {
            if (! isFavorite(installed) && ensureInstalled(installed.getPackageName()) == true) {
                apps.add(installed);
                for (AppHolder shown : platter) {
                    if (installed.equals(shown)) {
                        apps.remove(installed);
                    }
                }
            }
        }
        for (AppHolder installed : installedPrograms) {
            if (! isFavorite(installed) && apps.contains(installed)==false) {
                apps.add(installed);
                for (AppHolder shown : platter) {
                    if (installed.equals(shown)) {
                        apps.remove(installed);
                    }
                }
            }
        }
        return apps;
    }

    private boolean ensureInstalled(String packageName) {
        boolean found = true;

        try {
            context.getPackageManager().getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            found = false;
        }

        return found;
    }

    /**
     * Updates the current platter with new, randomly selected games.
     * @return the current platter
     */
    public AppHolder[] cycle(String genre) {
        AppHolder[] allApps = loadAppList(genre);
        if (allApps == null) throw new NullPointerException("ALL APPS FAILED TO LOAD");

        for (int i = 0; i < platter.length; i++) {
            if (platter[i] != null) {
                InstallUtility.cancelDownload(platter[i].getPackageName());
                InstallUtility.deleteApk(context, platter[i]);
            }

            int randIndex = (int) (Math.random()*allApps.length);
            platter[i] = allApps[randIndex];

            InstallUtility.downloadApkAsync(context, platter[i]);
        }

        savePlatter();
        return getPlatter();
    }

    public AppHolder getAppHolder(String packageName) {
        AppHolder[] allApps = loadAppList("all");
        for (AppHolder app : allApps) {
            if (app.getPackageName().equals(packageName)) {
                return app;
            }
        }

        return null;
    }

    /**
     * @deprecated Use the cycle(c) command to instantly cycle platter
     * Checks the server for any updates to the platter. If updates are found, propagates them.
     * @return true if updates are found
     */
    @Deprecated
    public boolean checkForUpdates() {
        AppHolder[] allApps = loadAppList("all");
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

    private int findPlatterIndex() {
        long launchTime = 1545091200;
        Date date = Calendar.getInstance().getTime();
        long elapsedTime = date.getTime() - launchTime;
        return (int) (elapsedTime / 86400000) * 4;
    }

    public boolean inPlatter(AppHolder app) {
        for (int i = 0; i < platter.length; i++) {
            if (app.equals(platter[i])) return true;
        }
        return false;
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

    // SAVE AND LOAD FUNCTIONS BELOW THIS POINT
    //=======================================================================================================

    private void saveFavorites() {
        saveAppHolders(favorites.toArray(new AppHolder[0]), FAVORITES_FILENAME);
    }

    private Set<AppHolder> load(String filename) {
        Set<AppHolder> result = new HashSet<>();
        File dir = context.getFilesDir();
        File installList = new File(dir, filename);

        try {
            byte[] content = Files.readAllBytes(installList.toPath());
            String decodedContent = new String(content);
            AppHolder[] arr = loadFromJSON(decodedContent);
            if (arr == null) return result;
            for (AppHolder app : arr) {
                if (! result.contains(app) && ensureInstalled(app.getPackageName())) {
                    result.add(app);
                }
            }
        } catch(IOException e) {
            try {
                installList.createNewFile();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }

    private void loadPlatter() {
        File dir = context.getFilesDir();
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
            cycle("all");
        }
    }

    private AppHolder[] loadAppList(String genre) {
        try {

            if (genre.equals("all")) {
                genre = "";
            } else {
                genre = "-" + genre;
            }
            String json = loadJSONFile(context.getAssets().open("AppList" + genre + ".json"));
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
                String apkUrl = jsonObject.getString("url");
                appArray[i] = new AppHolder(packageName, name, iconURL, apkUrl);
            }

            return appArray;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void saveCurrentInstalled() {
        saveAppHolders(installedPrograms.toArray(new AppHolder[0]), INSTALL_LIST_FILENAME);
    }
    public void saveHasBeenClicked() {
        saveAppHolders(hasBeenClicked.toArray(new AppHolder[0]), HAS_BEEN_CLICKED_FILENAME);
    }

    public void savePlatter() {
        saveAppHolders(platter, PLATTER_FILENAME);
    }

    private void saveAppHolders(AppHolder[] apps, String filename) {
        JSONArray array = new JSONArray();
        for (AppHolder app : apps) {
            JSONObject obj = new JSONObject();
            try {
                obj.put("name", app.getAppName());
                obj.put("package", app.getPackageName());
                obj.put("icon", app.getIcon());
                obj.put("url", app.getApkUrl());
            } catch (JSONException e) {continue;}
            array.put(obj);
        }

        File dir = context.getFilesDir();
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
}
