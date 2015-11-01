package api;

import net.lapismc.HomeSpawn.HomeSpawn;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class PlayerData {

    private HomeSpawn plugin;

    public PlayerData(HomeSpawn plugin) {
        this.plugin = plugin;
    }

    public YamlConfiguration getHomeConfig(String playername) {
        File file2 = new File(plugin.getDataFolder().getAbsolutePath()
                + File.separator + "PlayerData" + File.separator
                + "PlayerNames" + File.separator + playername + ".yml");
        FileConfiguration getName = YamlConfiguration.loadConfiguration(file2);
        File Homes = new File(plugin.getDataFolder().getAbsolutePath()
                + File.separator + "PlayerData" + File.separator
                + getName.getString("UUID") + ".yml");
        YamlConfiguration getHome = YamlConfiguration.loadConfiguration(Homes);
        return getHome;
    }

    public void saveHomesConfig(YamlConfiguration HomeConfig) {
        String name = HomeConfig.getName();
        File file = new File(plugin.getDataFolder().getAbsolutePath() + File.separator + "PlayerData" + File.separator + name);
        try {
            HomeConfig.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
