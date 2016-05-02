package net.lapismc.HomeSpawn;


import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Objects;

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
            URL website = new URL("http://www.dart2112.comli.com/plugins/update.yml");
            ReadableByteChannel rbc = Channels.newChannel(website.openStream());
            File f = new File(plugin.getDataFolder().getAbsolutePath() + "update.yml");
            FileOutputStream fos = new FileOutputStream(f);
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            rbc.close();
            fos.flush();
            fos.close();
            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(f);
            String oldVersion = plugin.getDescription().getVersion();
            String newVersion = yaml.getString(ID);
            return !Objects.equals(oldVersion, newVersion);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

}
