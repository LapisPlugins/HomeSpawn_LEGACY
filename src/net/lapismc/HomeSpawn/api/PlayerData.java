package net.lapismc.HomeSpawn.api;

import net.lapismc.HomeSpawn.HomeSpawn;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

/**
 * api Class to get Player Data Files
 *
 * @author Dart2112
 */
class PlayerData {

    private final HomeSpawn plugin;

    public PlayerData(HomeSpawn plugin) {
        this.plugin = plugin;
    }

    /**
     * Returns the currently loaded Player Data file will the given Player name
     */
    public YamlConfiguration getHomeConfig(String PlayerName) {
        UUID uuid = plugin.PlayertoUUID.get(PlayerName);
        YamlConfiguration getHome = plugin.HomeConfigs.get(uuid);
        return getHome;
    }

    /**
     * Saves the given YamlConfiguration as the UUID in its file name
     *
     * @throws IOException
     */
    public void saveHomesConfig(YamlConfiguration HomeConfig) throws IOException {
        String name = HomeConfig.getName();
        File file = new File(plugin.getDataFolder().getAbsolutePath() + File.separator + "PlayerData" + File.separator + name);
        HomeConfig.save(file);
        plugin.reload("Silent");
    }

    /**
     * Returns the loaded list of Player names and UUIDs for getting a UUID from a Player name
     */
    public HashMap<String, UUID> PlayerNames() {
        return plugin.PlayertoUUID;
    }

}