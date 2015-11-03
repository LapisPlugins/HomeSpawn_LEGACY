package API;

import net.lapismc.HomeSpawn.HomeSpawn;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class PlayerData {

    private HomeSpawn plugin;

    public PlayerData(HomeSpawn plugin) {
        this.plugin = plugin;
    }

    public YamlConfiguration getHomeConfig(String PlayerName) {
        String UUID = plugin.PlayertoUUID.get(PlayerName);
        YamlConfiguration getHome = plugin.HomeConfigs.get(UUID);
        return getHome;
    }

    public void saveHomesConfig(YamlConfiguration HomeConfig) {
        String name = HomeConfig.getName();
        File file = new File(plugin.getDataFolder().getAbsolutePath() + File.separator + "PlayerData" + File.separator + name);
        try {
            HomeConfig.save(file);
            plugin.reload("Silent");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public HashMap<String, String> PlayerNames() {
        return plugin.PlayertoUUID;
    }

}
