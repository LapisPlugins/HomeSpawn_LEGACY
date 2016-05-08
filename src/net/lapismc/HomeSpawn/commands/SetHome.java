package net.lapismc.HomeSpawn.commands;

import net.lapismc.HomeSpawn.HomeSpawn;
import net.lapismc.HomeSpawn.HomeSpawnCommand;
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
        HashMap<String, Integer> perms = plugin.Permissions.get(plugin.PlayerPermission.get(player.getUniqueId()));
        if (perms == null) {
            player.sendMessage("Perms is null");
        }
        if (plugin == null) {
            player.sendMessage("Plugin is null");
        }
        if (plugin.PlayerPermission.get(player.getUniqueId()) == null) {
            player.sendMessage("PlayerPermission is null");
        }
        if (plugin.Permissions.get(plugin.PlayerPermission.get(player.getUniqueId())) == null) {
            player.sendMessage("Permissions in general is null");
        }
        if (plugin.Permissions.get(plugin.PlayerPermission.get(player.getUniqueId())).get("Homes") == null) {
            player.sendMessage("Homes is null");
        }
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
            player.sendMessage(ChatColor.RED
                    + HomeSpawnCommand.getMessages
                    .getString("Home.LimitReached"));
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
            getHomes.createSection(player.getUniqueId() + ".x");
            getHomes.createSection(player.getUniqueId() + ".y");
            getHomes.createSection(player.getUniqueId() + ".z");
            getHomes.createSection(player.getUniqueId() + ".world");
            getHomes.createSection(player.getUniqueId() + ".Yaw");
            getHomes.createSection(player.getUniqueId() + ".Pitch");
            if (!getHomes.contains("HasHome")) {
                getHomes.createSection("HasHome");
            }
            int HomesNumb = getHomes.getInt(player
                    .getUniqueId() + ".Numb");
            if (!getHomes.contains(player.getUniqueId() + ".HasHome")
                    || !getHomes.getString(
                    player.getUniqueId()
                            + ".HasHome").equals("Yes")) {
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
            player.sendMessage(ChatColor.GOLD
                    + HomeSpawnCommand.getMessages.getString("Home.HomeSet"));
        } else if (args.length == 1) {
            if (perms.get("set custom homes") == 1) {
                String home = args[0];
                if (home.equals("Home")) {
                    player.sendMessage(ChatColor.RED
                            + "You Cannot Use The Home Name \"Home\", Please Choose Another!");
                    return;
                }
                getHomes.createSection(home);
                getHomes.createSection(home + ".x");
                getHomes.createSection(home + ".y");
                getHomes.createSection(home + ".z");
                getHomes.createSection(home + ".world");
                getHomes.createSection(home + ".Yaw");
                getHomes.createSection(home + ".Pitch");
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
                player.sendMessage(ChatColor.GOLD
                        + HomeSpawnCommand.getMessages.getString("Home.HomeSet"));
            } else {
                player.sendMessage(ChatColor.DARK_RED + HomeSpawnCommand.getMessages.getString("NoPerms"));
            }
        } else {
            player.sendMessage(ChatColor.RED
                    + HomeSpawnCommand.getMessages.getString("Error.Args+"));
        }
        this.plugin.savePlayerData(uuid);
    }
}
