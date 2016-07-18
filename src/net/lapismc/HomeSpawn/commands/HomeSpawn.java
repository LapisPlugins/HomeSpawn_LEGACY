package net.lapismc.HomeSpawn.commands;

import net.lapismc.HomeSpawn.LapisUpdater;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class HomeSpawn {

    private net.lapismc.HomeSpawn.HomeSpawn plugin;
    private HomeSpawnPlayer homeSpawnPlayer;

    public HomeSpawn(net.lapismc.HomeSpawn.HomeSpawn p) {
        this.plugin = p;
        this.homeSpawnPlayer = new HomeSpawnPlayer(plugin);
    }

    public void homeSpawn(String[] args, Player player) {
        HashMap<String, Integer> perms = plugin.Permissions.get(
                plugin.PlayerPermission.get(player.getUniqueId()));
        if (args[0] == "player") {
            homeSpawnPlayer.HomeSpawnPlayer(args, player);
        }
        if (args.length == 0) {
            player.sendMessage(ChatColor.GOLD + "---------------"
                    + ChatColor.RED + "Homespawn" + ChatColor.GOLD
                    + "---------------");
            player.sendMessage(ChatColor.RED + "Author:"
                    + ChatColor.GOLD + " Dart2112");
            player.sendMessage(ChatColor.RED + "Version: "
                    + ChatColor.GOLD
                    + this.plugin.getDescription().getVersion());
            player.sendMessage(ChatColor.RED + "Spigot:"
                    + ChatColor.GOLD + " https://goo.gl/aWby6W");
            player.sendMessage(ChatColor.RED
                    + "Use /homespawn Help For Commands!");
            player.sendMessage(ChatColor.GOLD
                    + "-----------------------------------------");
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("reload")) {
                if (perms.get("reload") == 1) {
                    this.plugin.reload(player);
                } else {
                    player.sendMessage(ChatColor.RED
                            + "You Don't Have Permission To Do That");
                }
            } else if (args[0].equalsIgnoreCase("help")) {
                this.plugin.help(player);
            }
        } else if (args.length > 0) {
            if (args[0].equalsIgnoreCase("update")) {
                if (perms.get("updateNotify") == 1) {
                    LapisUpdater updater = new LapisUpdater(plugin);
                    if (args.length == 1) {
                        String ID = plugin.getConfig().getBoolean("BetaVersions")
                                ? "beta" : "stable";
                        if (updater.downloadUpdate(ID)) {
                            player.sendMessage(ChatColor.GOLD + "Downloading Update...");
                            player.sendMessage(ChatColor.GOLD + "The update will be installed"
                                    + " when the server next starts!");
                        } else {
                            player.sendMessage(ChatColor.GOLD + "Updating failed!");
                        }
                    } else if (args.length == 2) {
                        String ID = args[1];
                        if (updater.downloadUpdate(ID)) {
                            player.sendMessage(ChatColor.GOLD + "Downloading Update...");
                            player.sendMessage(ChatColor.GOLD + "The update will be installed"
                                    + " when the server next starts!");
                        } else {
                            player.sendMessage(ChatColor.GOLD + "Updating failed!");
                        }
                    } else {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                plugin.messages.getString("Error.Args")));
                    }
                } else {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            plugin.messages.getString("NoPerms")));
                }
            }
        } else {
            player.sendMessage("That Is Not A Recognised Command, Use /homespawn help For " +
                    "Commands");
        }
    }

}
