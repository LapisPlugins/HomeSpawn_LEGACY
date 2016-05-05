package net.lapismc.HomeSpawn;


import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

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
    private Plugin plugin;

    public LapisUpdater(Plugin plugin) {
        this.plugin = plugin;
    }

    public boolean checkUpdate(String ID) {
        this.ID = ID;
        return updateCheck();
    }

    public boolean downloadUpdate(String ID) {
        this.ID = ID;
        return downloadUpdateJar();
    }

    private boolean downloadUpdateJar() {
        if (updateCheck()) {
            try {
                URL website = new URL("https://raw.githubusercontent.com/Dart2112/HomeSpawn/master/updater/" + ID.replace("s", "S").replace("b", "B") + "/Homespawn.jar");
                ReadableByteChannel rbc = Channels.newChannel(website.openStream());
                File f = new File(Bukkit.getUpdateFolder() + File.separator + "Homespawn.jar");
                FileOutputStream fos = new FileOutputStream(f);
                fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
                rbc.close();
                fos.flush();
                fos.close();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            return false;
        }
    }

    private boolean updateCheck() {
        try {
            URL website = new URL("https://raw.githubusercontent.com/Dart2112/HomeSpawn/master/updater/update.yml");
            ReadableByteChannel rbc = Channels.newChannel(website.openStream());
            File f = new File(plugin.getDataFolder().getAbsolutePath() + File.separator + "update.yml");
            Date d = new Date(f.lastModified());
            Date d0 = new Date();
            d0.setTime(d0.getTime() - 3600);
            if (!f.exists() || d.before(d0)) {
                FileOutputStream fos = new FileOutputStream(f);
                fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
                rbc.close();
                fos.flush();
                fos.close();
                f.setLastModified(d0.getTime());
            }
            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(f);
            if (!yaml.contains(ID)) {
                return false;
            }
            String oldVersionString = plugin.getDescription().getVersion().replace(".", "").replace("Beta ", "");
            String newVersionString = yaml.getString(ID).replace(".", "").replace("Beta ", "");
            Integer oldVersion = Integer.parseInt(oldVersionString);
            Integer newVersion = Integer.parseInt(newVersionString);
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
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
