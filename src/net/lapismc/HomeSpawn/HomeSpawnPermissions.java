package net.lapismc.HomeSpawn;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import java.util.HashMap;
import java.util.Set;

public class HomeSpawnPermissions {

    private HomeSpawn plugin;

    protected HomeSpawnPermissions(HomeSpawn p) {
        plugin = p;
    }

    protected void init() {
        plugin.Permissions.clear();
        HashMap<String, Integer> nullPermMap = new HashMap<>();
        nullPermMap.put("priority", 0);
        nullPermMap.put("homes", 0);
        nullPermMap.put("spawn", 1);
        nullPermMap.put("cHomes", 0);
        nullPermMap.put("TPD", 0);
        nullPermMap.put("sSpawn", 0);
        nullPermMap.put("updateNotify", 0);
        nullPermMap.put("reload", 0);
        nullPermMap.put("stats", 0);
        Permission np;
        if (Bukkit.getServer().getPluginManager().getPermission("homespawn.null") == null) {
            np = new Permission("homespawn.null", PermissionDefault.FALSE);
            Bukkit.getPluginManager().addPermission(np);
        } else {
            np = Bukkit.getServer().getPluginManager().getPermission("homespawn.null");
        }
        plugin.Permissions.put(np, nullPermMap);
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
            HashMap<String, Integer> permMap = new HashMap<>();
            permMap.put("priority", priority);
            permMap.put("homes", homes);
            permMap.put("spawn", spawn);
            permMap.put("cHomes", cHomes);
            permMap.put("TPD", TPD);
            permMap.put("sSpawn", sSpawn);
            permMap.put("updateNotify", updateNotify);
            permMap.put("reload", reload);
            permMap.put("stats", stats);
            for (String s : permMap.keySet()) {
                if (permMap.get(s) == null) {
                    permMap.put(s, 0);
                    plugin.logger.severe("Permission " + permName + " is missing the " + s
                            + " value! It has been set to 0 by defult, please fix this" +
                            " in the config!");
                }
            }
            for (String s : nullPermMap.keySet()) {
                if (!permMap.containsKey(s)) {
                    permMap.put(s, 0);
                    plugin.logger.severe("Permission " + permName + " is missing the " + s
                            + " value! It has been set to 0 by defult, please fix this" +
                            " in the config!");
                }
            }
            PermissionDefault PD = null;
            switch (Default) {
                case 1:
                    PD = PermissionDefault.TRUE;
                case 2:
                    PD = PermissionDefault.OP;
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
            plugin.Permissions.put(p, permMap);
            plugin.debug("Loaded permission " + p.getName());
        }
        plugin.logger.info("Permissions Loaded!");
    }

}
