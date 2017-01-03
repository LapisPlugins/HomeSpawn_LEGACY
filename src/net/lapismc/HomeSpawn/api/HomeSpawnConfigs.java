package net.lapismc.HomeSpawn.api;

import net.lapismc.HomeSpawn.HomeSpawn;
import net.lapismc.HomeSpawn.HomeSpawnComponents;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.util.ArrayList;

/**
 * api Class to get Config Files
 *
 * @author Dart2112
 */
public class HomeSpawnConfigs {

    private HomeSpawn plugin;
    private ArrayList<Plugin> blocked = new ArrayList<>();

    public HomeSpawnConfigs(Plugin plugin) {
        if (plugin.getName().equalsIgnoreCase("HomeSpawn")) {
            return;
        }
        HomeSpawnComponents hsc = new HomeSpawnComponents();
        if (hsc.api()) {
            this.plugin.logger.info("Plugin " + plugin.getName()
                    + " has connected to the API");
        } else {
            this.plugin.logger.severe("Plugin "
                    + plugin.getName() + " has attempted to connect to the HomeSpawn API," +
                    " But as it is disabled the plugin was denied access");
            blocked.add(plugin);
        }
    }

    public void init(HomeSpawn p) {
        this.plugin = p;
    }

    /**
     * Reloads all configs from disk
     *
     * @throws IOException
     */
    public void reloadConfigs(Plugin p) throws IOException {
        if (blocked.contains(p)) {
            return;
        }
        plugin.HSConfig.reload("Silent");
    }

    /**
     * Returns the currently loaded HomeSpawnSpawn.yml
     */
    public YamlConfiguration getSpawnConfig(Plugin p) {
        if (blocked.contains(p)) {
            return null;
        }
        return plugin.HSConfig.spawn;
    }

    /**
     * Saves the given YamlConfiguration as the HomeSpawnSpawn.yml file
     *
     * @throws IOException
     */
    public void saveSpawnConfig(Plugin p, YamlConfiguration SpawnConfig) throws IOException {
        if (blocked.contains(p)) {
            return;
        }
        plugin.HSConfig.spawn = SpawnConfig;
        plugin.HSConfig.spawn.save(plugin.HSConfig.spawnFile);
        plugin.HSConfig.reload("Silent");
    }


}
