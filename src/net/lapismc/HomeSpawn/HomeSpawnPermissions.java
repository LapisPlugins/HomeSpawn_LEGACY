/*
 * Copyright 2017 Benjamin Martin
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

package net.lapismc.HomeSpawn;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

public class HomeSpawnPermissions {

    private HashMap<Permission, HashMap<perm, Integer>> Permissions = new HashMap<>();
    private HashMap<UUID, Permission> PlayerPermission = new HashMap<>();
    private HomeSpawn plugin;

    protected HomeSpawnPermissions(HomeSpawn p) {
        plugin = p;
        loadPermissionMaps();
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                PlayerPermission = new HashMap<>();
            }
        }, 20 * 60 * 5, 20 * 60 * 5);
    }

    public HashMap<perm, Integer> getPlayerPermissions(UUID uuid) {
        Permission p = getPlayerPermission(uuid);
        if (!Permissions.containsKey(p) || Permissions.get(p).equals(null)) {
            loadPermissionMaps();
            return Permissions.get(p);
        } else {
            return Permissions.get(p);
        }
    }

    public Permission getPlayerPermission(UUID uuid) {
        Permission p = null;
        Player player = Bukkit.getPlayer(uuid);
        if (!PlayerPermission.containsKey(uuid) || PlayerPermission.get(uuid).equals(null)) {
            Integer priority = 0;
            for (Permission perm : Permissions.keySet()) {
                if (player.hasPermission(perm) &&
                        (Permissions.get(perm).get(HomeSpawnPermissions.perm.priority) > priority)) {
                    p = perm;
                }
            }
            if (p == null) {
                return null;
            } else {
                PlayerPermission.put(uuid, p);
            }
        } else {
            p = PlayerPermission.get(uuid);
        }
        return p;
    }

    private void loadPermissionMaps() {
        Permissions = new HashMap<>();
        HashMap<perm, Integer> nullPermMap = new HashMap<>();
        nullPermMap.put(perm.priority, 0);
        nullPermMap.put(perm.homes, 0);
        nullPermMap.put(perm.spawn, 1);
        nullPermMap.put(perm.customHomes, 0);
        nullPermMap.put(perm.TeleportDelay, 0);
        nullPermMap.put(perm.setSpawn, 0);
        nullPermMap.put(perm.updateNotify, 0);
        nullPermMap.put(perm.reload, 0);
        nullPermMap.put(perm.playerStats, 0);
        Permission np;
        if (Bukkit.getServer().getPluginManager().getPermission("homespawn.null") == null) {
            np = new Permission("homespawn.null", PermissionDefault.FALSE);
            Bukkit.getPluginManager().addPermission(np);
        } else {
            np = Bukkit.getServer().getPluginManager().getPermission("homespawn.null");
        }
        Permissions.put(np, nullPermMap);
        ConfigurationSection permsSection = plugin.getConfig().getConfigurationSection("Permissions");
        Set<String> perms = permsSection.getKeys(false);
        for (String perm : perms) {
            String permName = perm.replace(",", ".");
            int Default = plugin.getConfig().getInt("Permissions." + perm + ".default");
            int priority = plugin.getConfig().getInt("Permissions." + perm + ".priority");
            int homes = plugin.getConfig().getInt("Permissions." + perm + ".homes");
            int spawn = plugin.getConfig().getInt("Permissions." + perm + ".spawn");
            int cHomes = plugin.getConfig().getInt("Permissions." + perm + ".set custom homes");
            int TPD = plugin.getConfig().getInt("Permissions." + perm + ".TP delay");
            int sSpawn = plugin.getConfig().getInt("Permissions." + perm + ".setspawn");
            int updateNotify = plugin.getConfig().getInt("Permissions." + perm + ".updateNotify");
            int reload = plugin.getConfig().getInt("Permissions." + perm + ".reload");
            int stats = plugin.getConfig().getInt("Permissions." + perm + ".player stats");
            HashMap<perm, Integer> permMap = new HashMap<>();
            permMap.put(HomeSpawnPermissions.perm.priority, priority);
            permMap.put(HomeSpawnPermissions.perm.homes, homes);
            permMap.put(HomeSpawnPermissions.perm.spawn, spawn);
            permMap.put(HomeSpawnPermissions.perm.customHomes, cHomes);
            permMap.put(HomeSpawnPermissions.perm.TeleportDelay, TPD);
            permMap.put(HomeSpawnPermissions.perm.setSpawn, sSpawn);
            permMap.put(HomeSpawnPermissions.perm.updateNotify, updateNotify);
            permMap.put(HomeSpawnPermissions.perm.reload, reload);
            permMap.put(HomeSpawnPermissions.perm.playerStats, stats);
            for (perm p : permMap.keySet()) {
                if (permMap.get(p) == null) {
                    permMap.put(p, 0);
                    plugin.logger.severe("Permission " + permName + " is missing the " + p.toString()
                            + " value! It has been set to 0 by defult, please fix this" +
                            " in the config!");
                }
            }
            for (perm p : nullPermMap.keySet()) {
                if (!permMap.containsKey(p)) {
                    permMap.put(p, 0);
                    plugin.logger.severe("Permission " + permName + " is missing the " + p.toString()
                            + " value! It has been set to 0 by defult, please fix this" +
                            " in the config!");
                }
            }
            PermissionDefault PD = null;
            switch (Default) {
                case 1:
                    PD = PermissionDefault.TRUE;
                    break;
                case 2:
                    PD = PermissionDefault.OP;
                    break;
                case 0:
                default:
                    PD = PermissionDefault.FALSE;
            }
            Permission p;
            if (Bukkit.getServer().getPluginManager().getPermission(permName) == null) {
                p = new Permission(permName, PD);
                Bukkit.getPluginManager().addPermission(p);
            } else {
                p = Bukkit.getServer().getPluginManager().getPermission(permName);
            }
            Permissions.put(p, permMap);
            plugin.debug("Loaded permission " + p.getName());
        }
    }

    public enum perm {
        defult, priority, homes, spawn, customHomes, TeleportDelay, setSpawn, updateNotify, reload, playerStats;

        @Override
        public String toString() {
            return super.toString();
        }
    }

}
