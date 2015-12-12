package net.lapismc.HomeSpawn;


import net.gravitydevelopment.updater.Updater;
import org.bukkit.plugin.Plugin;
import org.inventivetalent.spiget.api.java.Spiget;
import org.inventivetalent.spiget.api.java.SpigetAPI;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class LapisUpdater {

    private final Updater updater;
    private String ID;
    private String result;
    private String error;
    private Plugin plugin;
    private URL downloadURL;

    public LapisUpdater(Updater plugin) {
        updater = plugin;
    }

    public void update(Plugin plugin, boolean download, String ID) {
        boolean download1 = download;
        this.ID = ID;
        this.plugin = plugin;
        String updateCheck = updateCheck();
        if (updateCheck.equalsIgnoreCase("No Update")) {
            result = "No Update Available";
        } else if (updateCheck.startsWith("Update Failed: ")) {
            result = "Update Failed";
            error = updateCheck.replace("Update Failed: ", "");
        } else {
            if (download) {
                download();
            } else {
                result = "New Version: " + updateCheck;
            }
        }
    }

    public String getResult() {
        if (result.equalsIgnoreCase("Update Failed")) {
            return result + ": " + error;
        } else {
            return result;
        }
    }

    private void download() {
        //get remote file
        File file = null;
        if (file.getName().endsWith(".zip")) {
            updater.saveFile(file.getName());
        } else {
            File file1 = new File(plugin.getServer().getUpdateFolder() + File.separator + file.getName());
            file.renameTo(file1);
        }
    }

    private String updateCheck() {
        try {
            HttpURLConnection c = (HttpURLConnection) new URL("http://www.spigotmc.org/api/general.php").openConnection();
            c.setDoOutput(true);
            c.setRequestMethod("POST");
            c.getOutputStream().write(("key=98BE0FE67F88AB82B4C197FAF1DC3B69206EFDCC4D3B80FC83A00037510B99B4" +
                    "&resource=" + ID).getBytes("UTF-8"));
            SpigetAPI api = Spiget.getAPI();

            String oldVersion = plugin.getDescription().getVersion();
            String newVersion = new BufferedReader(new InputStreamReader(c.getInputStream()))
                    .readLine().replaceAll("[a-zA-Z ]", "");
            if (!newVersion.equals(oldVersion)) {
                return newVersion;
            } else {
                return "No Update";
            }
        } catch (IOException e) {
            e.printStackTrace();
            String message = e.getMessage();
            return "Update Failed: " + message;
        }
    }

}
