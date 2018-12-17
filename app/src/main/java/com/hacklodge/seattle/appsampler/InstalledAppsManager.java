package com.hacklodge.seattle.appsampler;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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

    public void addInstalled(AppHolder app) {
        installedPrograms.add(app);
    }

    public void removeInstalled(AppHolder app) {
        installedPrograms.remove(app);
    }

    public boolean isInstalled(AppHolder app) {
        return installedPrograms.contains(app);
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
                if (! installedPrograms.contains(s)) {
                    installedPrograms.add(new AppHolder(s, s, null));
                }
            }

        } catch(IOException e) {
            installList.mkdir();
        }
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
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(c.getAssets().open("AppList.txt")));
            int platterIndex = findPlatterIndex();
            int count = 0;
            String line = "";
            String[] foundPlatter = new String[4];
            while ((line = reader.readLine()) != null) {
                if (count >= platterIndex) {
                    if (count >= platterIndex+4) {
                        break;
                    }
                    foundPlatter[count-platterIndex] = line;
                }
                count++;
            }

            boolean updated = false;
            for (int i = 0; i < 4; i++) {
                if (! foundPlatter[i].equals(platter[i].getPackageName())) {
                    updated = true;
                    platter[i] = new AppHolder(foundPlatter[i], foundPlatter[i], null);
                }
            }

            return updated;

        } catch(IOException e) {
            return false;
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
