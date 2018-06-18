/*
 * Copyright 2018 Benjamin Martin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

    private final HomeSpawn plugin;
    private final OfflinePlayer op;
    private final ArrayList<Home> homes = new ArrayList<>();
    private YamlConfiguration yaml;

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

    public UUID getUUID() {
        return op.getUniqueId();
    }

    public ArrayList<Home> getHomes() {
        return homes;
    }

    public Home getHome(String name) {
        for (Home h : getHomes()) {
            if (h.getName().equalsIgnoreCase(name)) {
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

    public boolean hasHome(String name) {
        for (Home home : getHomes()) {
            if (home.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    public void addHome(Home home) {
        homes.add(home);
    }

    public void removeHome(Home home) {
        homes.remove(home);
    }

    public int getPermissionValue(HomeSpawnPermissions.perm perm) {
        return plugin.HSPermissions.getPermissionValue(op.getUniqueId(), perm);
    }

    public boolean isPermitted(HomeSpawnPermissions.perm perm) {
        return plugin.HSPermissions.isPermitted(op.getUniqueId(), perm);
    }

    public YamlConfiguration getConfig(boolean force) {
        if (yaml == null || force) {
            yaml = plugin.HSConfig.getPlayerData(op.getUniqueId());
        }
        return yaml;
    }

    public void saveConfig(YamlConfiguration config) {
        yaml = config;
        plugin.HSConfig.savePlayerData(op.getUniqueId(), config);
    }

    public void reloadHomes() {
        homes.clear();
        loadHomes();
    }

    private void loadHomes() {
        getConfig(true);
        if (yaml.contains("Homes.list")) {
            List<String> homesList = yaml.getStringList("Homes.list");
            ConfigurationSection cs = yaml.getConfigurationSection("Homes");
            for (String key : cs.getKeys(false)) {
                if (!key.endsWith("list")) {
                    String name = key.replace("Homes.", "");
                    if (homesList.contains(name)) {
                        Location loc = (Location) yaml.get("Homes." + key);
                        Home h = new Home(plugin, name, loc, op);
                        addHome(h);
                    } else {
                        yaml.set(key, null);
                    }
                }
            }
        }
    }
}
