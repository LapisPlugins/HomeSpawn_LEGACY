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

public class SetHome {

    HomeSpawn plugin = null;

    public SetHome(HomeSpawn p) {
        this.plugin = p;
    }

    public void setHome(String[] args, Player player) {
        HashMap<String, Integer> perms = plugin.permissions.Permissions.get(plugin.permissions.PlayerPermission.
                get(player.getUniqueId()));
        UUID uuid = this.plugin.PlayertoUUID.get(player.getName());
        YamlConfiguration getHomes = this.plugin.HomeConfigs.get(uuid);
        if (!getHomes.contains(player.getUniqueId()
                + ".list")) {
            getHomes.createSection(player.getUniqueId()
                    + ".list");
            this.plugin.savePlayerData(uuid);
        }
        List<String> list = getHomes.getStringList(player
                .getUniqueId() + ".list");
        if (getHomes.getInt(player.getUniqueId()
                + ".Numb") >= perms.get("homes")) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.messages
                            .getString("Home.LimitReached")));
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
            HomeSetEvent HCE = new HomeSetEvent(player, player.getWorld(), "Home");
            Bukkit.getPluginManager().callEvent(HCE);
            if (HCE.isCancelled()) {
                return;
            }
            int HomesNumb = getHomes.getInt(player
                    .getUniqueId() + ".Numb");
            if (!getHomes.contains("HasHome")
                    || !getHomes.getString("HasHome").equals("Yes")) {
                getHomes.set(player.getUniqueId()
                        + ".Numb", HomesNumb + 1);
            }
            if (!list.contains("Home")) {
                list.add("Home");
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
                    plugin.messages.getString("Home.HomeSet")));
        } else if (args.length == 1) {
            if (perms.get("cHomes") == 1) {
                String home = args[0];
                if (home.equalsIgnoreCase("Home")) {
                    player.sendMessage(ChatColor.RED
                            + "You Cannot Use The Home Name \"Home\", Please Choose Another!");
                    return;
                }
                HomeSetEvent HCE = new HomeSetEvent(player, player.getWorld(), home);
                Bukkit.getPluginManager().callEvent(HCE);
                if (HCE.isCancelled()) {
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
                        plugin.messages.getString("Home.HomeSet")));
            } else {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.messages.getString("NoPerms")));
            }
        } else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.messages.getString("Error.Args+")));
        }
        this.plugin.savePlayerData(uuid);
    }

}
