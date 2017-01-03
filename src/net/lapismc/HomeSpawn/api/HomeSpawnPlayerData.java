package net.lapismc.HomeSpawn.api;

import net.lapismc.HomeSpawn.HomeSpawn;
import net.lapismc.HomeSpawn.HomeSpawnComponents;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * api Class to get Player Data Files
 *
 * @author Dart2112
 */
public class HomeSpawnPlayerData {

    private HomeSpawn plugin;
    private ArrayList<Plugin> blocked = new ArrayList<>();

    public HomeSpawnPlayerData(Plugin plugin) {
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
     * Returns the currently loaded Player Data file will the given Player name
     */
    public YamlConfiguration getHomeConfig(Plugin p, Player player) {
        if (blocked.contains(p)) {
            return null;
        }
        YamlConfiguration getHome = plugin.HSConfig.getPlayerData(player.getUniqueId());
        return getHome;
    }

    /**
     * Saves the given YamlConfiguration as the UUID in its file name
     *
     * @throws IOException
     */
    public void saveHomesConfig(Plugin p, YamlConfiguration HomeConfig) throws IOException {
        if (blocked.contains(p)) {
            return;
        }
        String name = HomeConfig.getName();
        File file = new File(plugin.getDataFolder().getAbsolutePath() + File.separator + "PlayerData" + File.separator + name);
        HomeConfig.save(file);
        plugin.HSConfig.reload("Silent");
    }

}
