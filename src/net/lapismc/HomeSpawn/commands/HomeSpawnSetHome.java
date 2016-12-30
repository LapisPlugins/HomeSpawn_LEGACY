package net.lapismc.HomeSpawn.commands;

import net.lapismc.HomeSpawn.HomeSpawn;
import net.lapismc.HomeSpawn.api.events.HomeSetEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class HomeSpawnSetHome {

    HomeSpawn plugin = null;

    public HomeSpawnSetHome(HomeSpawn p) {
        this.plugin = p;
    }

    public void setHome(String[] args, Player player) {
        HashMap<String, Integer> perms = plugin.HSPermissions.Permissions.get(plugin.HSPermissions.PlayerPermission.
                get(player.getUniqueId()));
        if (perms == null) {
            plugin.HSPermissions.init();
            perms = plugin.HSPermissions.Permissions.get(plugin.HSPermissions.PlayerPermission.
                    get(player.getUniqueId()));
        }
        UUID uuid = this.plugin.HSConfig.PlayertoUUID.get(player.getName());
        YamlConfiguration getHomes = this.plugin.HSConfig.HomeConfigs.get(uuid);
        if (!getHomes.contains(player.getUniqueId()
                + ".list")) {
            getHomes.createSection(player.getUniqueId()
                    + ".list");
            this.plugin.HSConfig.savePlayerData(uuid);
        }
        List<String> list = getHomes.getStringList(player
                .getUniqueId() + ".list");
        if (getHomes.getInt(player.getUniqueId()
                + ".Numb") >= perms.get("homes")) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.HSConfig.messages
                            .getString("HomeSpawnHome.LimitReached")));
            return;
        }

        if (args.length == 0) {
            getHomes.createSection(player.getUniqueId()
                    .toString());
            if (!getHomes.contains(player.getUniqueId() + ".Numb")) {
                getHomes.createSection(player.getUniqueId() + ".Numb");
                getHomes.set(player.getUniqueId()
                        + ".Numb", "0");
            }
            if (!getHomes.contains("HasHome")) {
                getHomes.createSection("HasHome");
            }
            HomeSetEvent HCE = new HomeSetEvent(plugin, player, player.getLocation(), "Home");
            Bukkit.getPluginManager().callEvent(HCE);
            if (HCE.isCancelled()) {
                player.sendMessage("Your home has not been set because " + HCE.getReason());
                return;
            }
            int HomesNumb = getHomes.getInt(player
                    .getUniqueId() + ".Numb");
            if (!getHomes.contains("HasHome")
                    || !getHomes.getString("HasHome").equals("Yes")) {
                getHomes.set(player.getUniqueId()
                        + ".Numb", HomesNumb + 1);
            }
            if (!list.contains("HomeSpawnHome")) {
                list.add("HomeSpawnHome");
                getHomes.set(player.getUniqueId()
                        + ".list", list);
            }
            getHomes.set(
                    player.getUniqueId() + ".x",
                    player.getLocation().getBlockX());
            getHomes.set(
                    player.getUniqueId() + ".y",
                    player.getLocation().getBlockY());
            getHomes.set(
                    player.getUniqueId() + ".z",
                    player.getLocation().getBlockZ());
            getHomes.set(player.getUniqueId()
                    + ".world", player.getWorld().getName());
            getHomes.set(player.getUniqueId()
                    + ".Yaw", player.getLocation().getYaw());
            getHomes.set(player.getUniqueId()
                    + ".Pitch", player.getLocation().getPitch());
            getHomes.set("HasHome", "Yes");
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.HSConfig.messages.getString("HomeSpawnHome.HomeSet")));
        } else if (args.length == 1) {
            if (perms.get("cHomes") == 1) {
                String home = args[0];
                if (home.equalsIgnoreCase("HomeSpawnHome")) {
                    player.sendMessage(ChatColor.RED
                            + "You Cannot Use The HomeSpawnHome Name \"HomeSpawnHome\", Please Choose Another!");
                    return;
                }
                HomeSetEvent HCE = new HomeSetEvent(plugin, player, player.getLocation(), home);
                Bukkit.getPluginManager().callEvent(HCE);
                if (HCE.isCancelled()) {
                    player.sendMessage("Your home has not been set because " + HCE.getReason());
                    return;
                }
                if (!getHomes.contains(home + ".HasHome")) {
                    getHomes.createSection(home + ".HasHome");
                }
                if (!getHomes.contains(player.getUniqueId() + ".Numb")) {
                    getHomes.createSection(player.getUniqueId() + ".Numb");
                    getHomes.set(player.getUniqueId() + ".Numb", "0");
                }
                int HomesNumb = getHomes.getInt(player
                        .getUniqueId() + ".Numb");
                if (!getHomes.contains(home + ".HasHome")
                        || !getHomes.get(home + ".HasHome")
                        .equals("Yes")) {
                    getHomes.set(player.getUniqueId() + ".Numb",
                            HomesNumb + 1);
                }
                if (!list.contains(home)) {
                    list.add(home);
                    getHomes.set(player.getUniqueId() + ".list", list);
                }
                getHomes.set(home + ".x", player.getLocation()
                        .getBlockX());
                getHomes.set(home + ".y", player.getLocation()
                        .getBlockY());
                getHomes.set(home + ".z", player.getLocation()
                        .getBlockZ());
                getHomes.set(home + ".world", player.getWorld()
                        .getName());
                getHomes.set(home + ".Yaw", player
                        .getLocation().getYaw());
                getHomes.set(home + ".Pitch", player
                        .getLocation().getPitch());
                getHomes.set(home + ".HasHome", "Yes");
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.HSConfig.messages.getString("HomeSpawnHome.HomeSet")));
            } else {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.HSConfig.messages.getString("NoPerms")));
            }
        } else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.HSConfig.messages.getString("Error.Args+")));
        }
        this.plugin.HSConfig.savePlayerData(uuid);
    }

}
