package net.lapismc.HomeSpawn.playerdata;

import net.lapismc.HomeSpawn.HomeSpawn;
import net.lapismc.HomeSpawn.HomeSpawnPermissions;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
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

    public Home getHome(String name) {
        for (Home h : getHomes()) {
            if (h.getName().equals(name)) {
                return h;
            }
        }
        return null;
    }

    public String getHomesList() {
        return homes.stream().map(Object::toString).collect(Collectors.joining(", "));
    }

    public List<String> getHomesStringList() {
        List<String> stringList = new ArrayList<>();
        for (Home h : homes) {
            stringList.add(h.toString());
        }
        return stringList;
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

    public int getPermissionValue(HomeSpawnPermissions.perm perm) {
        return plugin.HSPermissions.getPermissionValue(op.getUniqueId(), perm);
    }

    public boolean isPermitted(HomeSpawnPermissions.perm perm) {
        return plugin.HSPermissions.isPermitted(op.getUniqueId(), perm);
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
