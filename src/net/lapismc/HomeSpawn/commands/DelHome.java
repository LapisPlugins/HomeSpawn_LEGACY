package net.lapismc.HomeSpawn.commands;

import net.lapismc.HomeSpawn.HomeSpawn;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class DelHome {

    private HomeSpawn plugin;

    public DelHome(HomeSpawn p) {
        this.plugin = p;
    }

    public void delHome(String[] args, Player player) {
        UUID uuid = plugin.PlayertoUUID.get(player.getName());
        YamlConfiguration getHomes = plugin.HSConfig.HomeConfigs.get(uuid);
        if (!getHomes.contains(player.getUniqueId()
                + ".list")) {
            getHomes.createSection(player.getUniqueId()
                    + ".list");
            plugin.HSConfig.savePlayerData(uuid);
        }
        List<String> list = getHomes.getStringList(player
                .getUniqueId() + ".list");
        if (args.length == 0) {
            int HomeNumb = getHomes.getInt(player.getUniqueId() + ".Numb");
            if (getHomes.getString("HasHome")
                    .equalsIgnoreCase("no")
                    || !getHomes.contains("HasHome")) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.HSConfig.messages.getString("Home.NoHomeSet")));
            } else if (getHomes.getString("HasHome")
                    .equalsIgnoreCase("yes")) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.HSConfig.messages.getString("Home.HomeRemoved")));
                getHomes.set("HasHome", "No");
                getHomes.set(player.getUniqueId()
                        + ".Numb", HomeNumb - 1);
                if (list.contains("Home")) {
                    list.remove("Home");
                    getHomes.set(player.getUniqueId()
                            + ".list", list);
                }
                this.plugin.HSConfig.savePlayerData(uuid);
            } else {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.HSConfig.messages.getString("Home.NoHomeSet")));
                if (getHomes.getInt(player.getUniqueId()
                        + ".Numb") > 0) {
                    if (!list.isEmpty()) {
                        String list2 = list.toString();
                        String list3 = list2.replace("[", " ");
                        String StringList = list3.replace("]", " ");
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.HSConfig.messages.getString("Home.CurrentHomes")));
                        player.sendMessage(ChatColor.RED
                                + StringList);
                    } else {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                plugin.HSConfig.messages
                                        .getString("Home.NoHomeSet")));
                    }
                } else {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            plugin.HSConfig.messages
                                    .getString("Home.NoHomeSet")));
                }
            }
        } else if (args.length == 1) {
            String home = args[0];
            int HomeNumb = getHomes.getInt(player.getUniqueId() + ".Numb");
            if (!getHomes.contains(home + ".HasHome")
                    || getHomes.getString(home + ".HasHome")
                    .equalsIgnoreCase("no")) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.HSConfig.messages.getString("Home.NoHomeName")));
                if (getHomes.getInt(player.getUniqueId()
                        + ".Numb") > 0) {
                    if (!list.isEmpty()) {
                        String list2 = list.toString();
                        String list3 = list2.replace("[", " ");
                        String StringList = list3.replace("]", " ");
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.HSConfig.messages.getString("Home.CurrentHomes")));
                        player.sendMessage(ChatColor.RED
                                + StringList);
                    } else {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                plugin.HSConfig.messages
                                        .getString("Home.NoHomeSet")));
                    }
                } else {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            plugin.HSConfig.messages
                                    .getString("Home.NoHomeSet")));
                }
            } else if (getHomes.getString(home + ".HasHome")
                    .equalsIgnoreCase("yes")) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.HSConfig.messages.getString("Home.HomeRemoved")));
                getHomes.set(home + ".HasHome", "No");
                getHomes.set(player.getUniqueId()
                        + ".Numb", HomeNumb - 1);
                if (list.contains(home)) {
                    list.remove(home);
                    getHomes.set(player.getUniqueId()
                            + ".list", list);
                }
                this.plugin.HSConfig.savePlayerData(uuid);
            } else {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.HSConfig.messages.getString("Home.NoHomeName")));
                if (getHomes.getInt(player.getUniqueId()
                        + ".Numb") > 0) {
                    if (!list.isEmpty()) {
                        String list2 = list.toString();
                        String list3 = list2.replace("[", " ");
                        String StringList = list3.replace("]", " ");
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.HSConfig.messages.getString("Home.CurrentHomes")));
                        player.sendMessage(ChatColor.RED
                                + StringList);
                    } else {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                plugin.HSConfig.messages
                                        .getString("Home.NoHomeSet")));
                    }
                } else {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            plugin.HSConfig.messages
                                    .getString("Home.NoHomeSet")));
                }
            }
        } else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.HSConfig.messages.getString("Error.Args+")));
        }
    }

}
