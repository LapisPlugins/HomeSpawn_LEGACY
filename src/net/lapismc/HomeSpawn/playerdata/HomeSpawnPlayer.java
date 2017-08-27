package net.lapismc.HomeSpawn.playerdata;

import net.lapismc.HomeSpawn.HomeSpawn;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class HomeSpawnPlayer {

    private HomeSpawn plugin;
    private OfflinePlayer op;
    private YamlConfiguration yaml;
    private ArrayList<Home> homes = new ArrayList<>();

    public HomeSpawnPlayer(HomeSpawn plugin, OfflinePlayer op) {
        this.plugin = plugin;
        this.op = op;
        loadHomes();
    }

    public HomeSpawnPlayer(HomeSpawn plugin, UUID uuid) {
        this.plugin = plugin;
        this.op = Bukkit.getOfflinePlayer(uuid);
        loadHomes();
    }

    public ArrayList<Home> getHomes() {
        return homes;
    }

    public String getHomesList() {
        String list2 = homes.toString();
        String list3 = list2.replace("[", " ");
        return list3.replace("]", " ");
    }

    public void addHome(Home home) {
        homes.add(home);
    }

    public boolean removeHome(Home home) {
        if (homes.contains(home)) {
            homes.remove(home);
            return true;
        }
        return false;
    }

    public YamlConfiguration getConfig() {
        if (yaml == null) {
            yaml = plugin.HSConfig.getPlayerData(op.getUniqueId());
        }
        return yaml;
    }

    public void saveConfig(YamlConfiguration config) {
        yaml = config;
        plugin.HSConfig.savePlayerData(op.getUniqueId(), config);
    }

    private void loadHomes() {
        getConfig();
        List<String> homesList = yaml.getStringList("Homes.list");
        ConfigurationSection cs = yaml.getConfigurationSection("Homes");
        for (String key : cs.getKeys(false)) {
            if (!key.endsWith("list")) {
                String name = key.replace("Homes.", "");
                if (homesList.contains(name)) {
                    Location loc = (Location) yaml.get(key);
                    Home h = new Home(name, loc, op);
                    addHome(h);
                } else {
                    yaml.set(key, null);
                }
            }
        }
    }
}
