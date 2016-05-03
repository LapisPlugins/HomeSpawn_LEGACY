package net.lapismc.HomeSpawn;


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

    public boolean checkUpdate(Plugin plugin, String ID) {
        this.ID = ID;
        this.plugin = plugin;
        return updateCheck();
    }

    private boolean updateCheck() {
        try {
            URL website = new URL("https://raw.githubusercontent.com/Dart2112/HomeSpawn/master/src/update.yml");
            ReadableByteChannel rbc = Channels.newChannel(website.openStream());
            File f = new File(plugin.getDataFolder().getAbsolutePath() + "update.yml");
            Date d = new Date(f.lastModified());
            Date d0 = new Date();
            d0.setTime(d0.getTime() - 3600);
            if (!f.exists() || d.before(d0)) {
                FileOutputStream fos = new FileOutputStream(f);
                fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
                rbc.close();
                fos.flush();
                fos.close();
            }
            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(f);
            String oldVersionString = plugin.getDescription().getVersion().replace(".", "");
            String newVersionString = yaml.getString(ID).replace(".", "");
            Integer oldVersion = Integer.parseInt(oldVersionString);
            Integer newVersion = Integer.parseInt(newVersionString);
            return (oldVersion < newVersion);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
