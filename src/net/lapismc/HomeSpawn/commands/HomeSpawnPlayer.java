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

package net.lapismc.HomeSpawn.commands;

import net.lapismc.HomeSpawn.HomeSpawn;
import net.lapismc.HomeSpawn.HomeSpawnPermissions;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.ocpsoft.prettytime.Duration;
import org.ocpsoft.prettytime.PrettyTime;
import org.ocpsoft.prettytime.units.JustNow;
import org.ocpsoft.prettytime.units.Millisecond;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

class HomeSpawnPlayer {

    private final HomeSpawn plugin;
    private final PrettyTime p = new PrettyTime();

    HomeSpawnPlayer(HomeSpawn pl) {
        plugin = pl;
        p.setLocale(Locale.ENGLISH);
        p.removeUnit(JustNow.class);
        p.removeUnit(Millisecond.class);
    }

    void homeSpawnPlayer(String[] args, CommandSender sender) {
        HashMap<HomeSpawnPermissions.perm, Integer> perms;
        if (sender instanceof Player) {
            Player p = (Player) sender;
            perms = plugin.HSPermissions.getPlayerPermissions(p.getUniqueId());
            if (perms.get(HomeSpawnPermissions.perm.playerStats) != 1) {
                p.sendMessage(plugin.HSConfig.getColoredMessage("NoPerms"));
                return;
            }
        }
        if (args.length == 2) {
            String name = args[1];
            //noinspection deprecation
            OfflinePlayer op = Bukkit.getOfflinePlayer(name);
            perms = plugin.HSPermissions.getPlayerPermissions(op.getUniqueId());
            if (perms == null) {
                sender.sendMessage(plugin.HSConfig.getColoredMessage("NoPlayerData"));
                return;
            }
            YamlConfiguration homes = plugin.HSConfig.getPlayerData(op.getUniqueId());
            if (homes == null) {
                sender.sendMessage(plugin.HSConfig.getColoredMessage("NoPlayerData"));
                return;
            }
            sender.sendMessage(ChatColor.RED + "----- " + ChatColor.GOLD + "Stats for " + ChatColor.BLUE + name + ChatColor.RED + " -----");
            if (op.isOnline()) {
                sender.sendMessage(ChatColor.RED + "Players Permission: " + ChatColor.GOLD + plugin.HSPermissions.getPlayerPermission(op.getUniqueId()).getName());
                String time;
                if (!(homes.get("login") instanceof Long)) {
                    time = "Error!";
                } else {
                    Date date = new Date(homes.getLong("login"));
                    List<Duration> durationList = p.calculatePreciseDuration(date);
                    time = p.format(durationList);
                }
                sender.sendMessage(ChatColor.RED + "Player " + ChatColor.BLUE + name + ChatColor.RED + " has been online since "
                        + ChatColor.GOLD + time);
            } else {
                String time;
                if (!(homes.get("logout") instanceof Long)) {
                    time = "Error!";
                } else {
                    Date date = new Date(homes.getLong("logout"));
                    List<Duration> durationList = p.calculatePreciseDuration(date);
                    time = p.format(durationList);
                }
                sender.sendMessage(ChatColor.RED + "Player " + ChatColor.BLUE + name + ChatColor.RED + " has been offline since "
                        + ChatColor.GOLD + time);
            }
            List<String> list = homes.getStringList("Homes.list");
            String usedHomes = String.valueOf(homes.getStringList("Homes.list").size());
            sender.sendMessage(ChatColor.GOLD + usedHomes + ChatColor.RED + " out of " + ChatColor.GOLD + perms.get(HomeSpawnPermissions.perm.homes)
                    + " homes used");
            if (!list.isEmpty()) {
                sender.sendMessage(ChatColor.RED + "The players home(s) are:");
                String list2 = list.toString();
                String list3 = list2.replace("[", " ");
                String StringList = list3.replace("]", " ");
                sender.sendMessage(ChatColor.GOLD + StringList);
            }
        } else if (args.length == 3) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(plugin.HSConfig.getMessage("Error.MustBePlayer"));
                return;
            }
            Player p = (Player) sender;
            String name = args[1];
            //noinspection deprecation
            OfflinePlayer op = Bukkit.getOfflinePlayer(name);
            YamlConfiguration homes = plugin.HSConfig.getPlayerData(op.getUniqueId());
            if (homes == null) {
                sender.sendMessage(plugin.HSConfig.getColoredMessage("NoPlayerData"));
                return;
            }
            teleportPlayer(p, args[2], homes);
        } else {
            help(sender);
        }
    }

    private void help(CommandSender sender) {
        sender.sendMessage(ChatColor.RED + "--------" + ChatColor.GOLD + " Player Stats Help " + ChatColor.RED + "--------");
        sender.sendMessage(ChatColor.RED + "/homespawn player:" + ChatColor.GOLD + " Displays this help");
        sender.sendMessage(ChatColor.RED + "/homespawn player (name):" + ChatColor.GOLD + " Shows the stats of the player given");
        sender.sendMessage(ChatColor.RED + "/homespawn player (name) (home name):" + ChatColor.GOLD + " Teleports you to that players" +
                " home of that name");
    }

    private void teleportPlayer(Player player, String home, YamlConfiguration getHomes) {
        if (home.equalsIgnoreCase("home") && getHomes.contains("Homes.Home")) {
            Location home2 = (Location) getHomes.get("Homes.Home");
            player.sendMessage(plugin.HSConfig.getColoredMessage("Home.SentHome"));
            player.teleport(home2);
            return;
        }
        if (getHomes.contains("Homes." + home)) {
            Location home2 = (Location) getHomes.get("Homes." + home);
            player.sendMessage(plugin.HSConfig.getColoredMessage("Home.SentHome"));
            player.teleport(home2);
        } else {
            player.sendMessage(plugin.HSConfig.getColoredMessage("Home.NoHomeName"));
        }
    }
}
