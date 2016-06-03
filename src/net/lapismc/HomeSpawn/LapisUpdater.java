package net.lapismc.HomeSpawn;


import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Date;

public class LapisUpdater {

    private String ID;
    private String result;
    private HomeSpawn plugin;
    private Boolean force;

    public LapisUpdater(HomeSpawn plugin) {
        this.plugin = plugin;
    }

    public boolean checkUpdate(String ID) {
        this.ID = ID;
        this.force = false;
        return updateCheck();
    }

    public boolean downloadUpdate(String ID) {
        this.ID = ID;
        this.force = true;
        return downloadUpdateJar();
    }

    private boolean downloadUpdateJar() {
        if (updateCheck()) {
            try {
                URL website = new URL("https://raw.githubusercontent.com/Dart2112/HomeSpawn/master/updater/" + ID.replace("s", "S").replace("b", "B") + "/Homespawn.jar");
                ReadableByteChannel rbc = Channels.newChannel(website.openStream());
                File update = new File(plugin.getDataFolder().getParent() + File.separator + "update");
                if (!update.exists()) {
                    update.mkdir();
                }
                File f = new File(update.getAbsolutePath() + File.separator + "Homespawn.jar");
                if (!f.exists()) {
                    f.createNewFile();
                }
                FileOutputStream fos = new FileOutputStream(f);
                fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
                rbc.close();
                fos.flush();
                fos.close();
                return true;
            } catch (IOException e) {
                plugin.logger.severe("HomeSpawn updater failed to download updates!");
                plugin.logger.severe("Please check your internet connection and" +
                        " firewall settings and try again later");
                return false;
            }
        } else {
            return false;
        }
    }

    private boolean updateCheck() {
        Integer oldVersion = null;
        Integer newVersion = null;
        File f = null;
        YamlConfiguration yaml = null;
        try {
            URL website = new URL("https://raw.githubusercontent.com/Dart2112/HomeSpawn/master/updater/update.yml");
            ReadableByteChannel rbc = Channels.newChannel(website.openStream());
            f = new File(plugin.getDataFolder().getAbsolutePath() + File.separator + "update.yml");
            Date d = new Date(f.lastModified());
            Date d0 = new Date();
            d0.setTime(d0.getTime() - 3600);
            if (!f.exists() || force || d.before(d0)) {
                FileOutputStream fos = new FileOutputStream(f);
                fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
                rbc.close();
                fos.flush();
                fos.close();
                f.setLastModified(d0.getTime());
            }
        } catch (IOException e) {
            plugin.logger.severe("Failed to check for updates!");
            plugin.logger.severe("Please check your internet and firewall settings" +
                    " and try again later!");
            return false;
        }
        try {
            yaml = YamlConfiguration.loadConfiguration(f);
            if (!yaml.contains(ID)) {
                return false;
            }
            String oldVersionString = plugin.getDescription().getVersion().replace(".", "").replace("Beta ", "");
            String newVersionString = yaml.getString(ID).replace(".", "").replace("Beta ", "");
            oldVersion = Integer.parseInt(oldVersionString);
            newVersion = Integer.parseInt(newVersionString);
        } catch (Exception e) {
            plugin.logger.severe("Failed to load update.yml or parse the values!" +
                    " It may be corrupt!");
            plugin.logger.severe("Please try again later");
            f.delete();
            return false;
        }
        Boolean update = false;
        if (yaml.getString(ID).contains("Beta") && !plugin.getDescription().getVersion().contains("Beta")) {
            update = true;
        }
        if (!yaml.getString(ID).contains("Beta") && plugin.getDescription().getVersion().contains("Beta")) {
            update = true;
        }
        if (yaml.getString(ID).contains("Beta") && plugin.getDescription().getVersion().contains("Beta")) {
            update = oldVersion < newVersion;
        }
        if (!yaml.getString(ID).contains("Beta") && !plugin.getDescription().getVersion().contains("Beta")) {
            update = oldVersion < newVersion;
        }
        return update;
    }
}
