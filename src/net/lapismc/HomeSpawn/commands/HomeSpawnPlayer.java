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
import org.bukkit.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.ocpsoft.prettytime.PrettyTime;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class HomeSpawnPlayer {

    private HomeSpawn plugin;
    private PrettyTime p = new PrettyTime();

    public HomeSpawnPlayer(HomeSpawn plugin) {
        this.plugin = plugin;
    }

    public void HomeSpawnPlayer(String[] args, Player player) {
        HashMap<HomeSpawnPermissions.perm, Integer> perms = plugin.HSPermissions
                .getPlayerPermissions(player.getUniqueId());
        if (perms.get(HomeSpawnPermissions.perm.playerStats) != 1) {
            player.sendMessage(plugin.HSConfig.getColoredMessage("NoPerms"));
            return;
        }
        if (args.length != 2 && args.length != 3) {
            help(player);
        } else if (args.length == 2) {
            String name = args[1];
            OfflinePlayer op = Bukkit.getOfflinePlayer(name);
            perms = plugin.HSPermissions.getPlayerPermissions(op.getUniqueId());
            if (perms == null) {
                player.sendMessage(plugin.HSConfig.getColoredMessage("NoPlayerData"));
                return;
            }
            YamlConfiguration homes = plugin.HSConfig.getPlayerData(op.getUniqueId());
            if (homes == null) {
                player.sendMessage(plugin.HSConfig.getColoredMessage("NoPlayerData"));
                return;
            }
            player.sendMessage(ChatColor.RED + "----- " + ChatColor.GOLD + "Stats for " + ChatColor.BLUE + name + ChatColor.RED + " -----");
            if (op.isOnline()) {
                player.sendMessage(ChatColor.RED + "Players Permission: " + ChatColor.GOLD + plugin.HSPermissions.getPlayerPermission(op.getUniqueId()).getName());
            }
            String time;
            if (homes.get("login") == null) {
                time = "Before Player Stats Were Introduced";
            }
            if (!(homes.get("login") instanceof Integer)) {
                if (op.isOnline()) {
                    time = "Now!";
                } else {
                    time = "Error!";
                }
            } else {
                time = p.format(new Date(homes.getLong("login")));
            }
            player.sendMessage(ChatColor.RED + "Player " + ChatColor.BLUE + name + ChatColor.RED + " was last online: "
                    + ChatColor.GOLD + time);
            String usedHomes = String.valueOf(homes.getInt(op.getUniqueId().toString() + ".Numb"));
            player.sendMessage(ChatColor.GOLD + usedHomes + ChatColor.RED + " out of " + ChatColor.GOLD + perms.get(HomeSpawnPermissions.perm.homes)
                    + " homes used");
            if (homes.getInt(op.getUniqueId().toString() + ".Numb") > 0) {
                player.sendMessage(ChatColor.RED + "The players home(s) are:");
                List<String> list = homes.getStringList(player
                        .getUniqueId() + ".list");
                String list2 = list.toString();
                String list3 = list2.replace("[", " ");
                String StringList = list3.replace("]", " ");
                player.sendMessage(ChatColor.GOLD + StringList);
            }
        } else if (args.length == 3) {
            String name = args[1];
            OfflinePlayer op = Bukkit.getOfflinePlayer(name);
            YamlConfiguration homes = plugin.HSConfig.getPlayerData(op.getUniqueId());
            if (homes == null) {
                player.sendMessage(plugin.HSConfig.getColoredMessage("NoPlayerData"));
                return;
            }
            teleportPlayer(player, args[2], homes);
        } else {
            help(player);
        }
    }

    private void help(Player player) {
        player.sendMessage(ChatColor.RED + "--------" + ChatColor.GOLD + " Player Stats Help " + ChatColor.RED + "--------");
        player.sendMessage(ChatColor.RED + "/homespawn player:" + ChatColor.GOLD + " Displays this help");
        player.sendMessage(ChatColor.RED + "/homespawn player (name):" + ChatColor.GOLD + " Shows the stats of the player given");
        player.sendMessage(ChatColor.RED + "/homespawn player (name) (home name):" + ChatColor.GOLD + " Teleports you to that players" +
                " home of that name");
    }

    private void teleportPlayer(Player player, String home, YamlConfiguration getHomes) {
        if (home.equalsIgnoreCase("home") && getHomes.getString("HasHome").equalsIgnoreCase("yes")) {
            String uuid = getHomes.getString("name");
            int x = getHomes.getInt(uuid + ".x");
            int y = getHomes.getInt(uuid + ".y");
            int z = getHomes.getInt(uuid + ".z");
            float yaw = getHomes.getInt(uuid + ".Yaw");
            float pitch = getHomes.getInt(uuid + ".Pitch");
            String cworld = getHomes.getString(uuid
                    + ".world");
            World world = plugin.getServer().getWorld(
                    cworld);
            Location home2 = new Location(world, x, y, z,
                    yaw, pitch);
            home2.add(0.5, 0, 0.5);
            player.sendMessage(plugin.HSConfig.getColoredMessage("Home.SentHome"));
            player.teleport(home2);
            return;
        }
        if (getHomes.contains(home) && getHomes.getString(home + ".HasHome")
                .equalsIgnoreCase("yes")) {
            int x = getHomes.getInt(home + ".x");
            int y = getHomes.getInt(home + ".y");
            int z = getHomes.getInt(home + ".z");
            float yaw = getHomes.getInt(home + ".Yaw");
            float pitch = getHomes.getInt(home + ".Pitch");
            String cworld = getHomes.getString(home
                    + ".world");
            World world = plugin.getServer().getWorld(
                    cworld);
            Location home2 = new Location(world, x, y, z,
                    yaw, pitch);
            home2.add(0.5, 0, 0.5);
            player.sendMessage(plugin.HSConfig.getColoredMessage("Home.SentHome"));
            player.teleport(home2);
        } else {
            player.sendMessage(plugin.HSConfig.getColoredMessage("Home.NoHomeName"));
        }
    }
}
