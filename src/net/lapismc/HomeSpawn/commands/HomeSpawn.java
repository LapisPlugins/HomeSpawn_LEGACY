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

import net.lapismc.HomeSpawn.HomeSpawnPermissions;
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
        HashMap<HomeSpawnPermissions.perm, Integer> perms = plugin.HSPermissions.getPlayerPermissions(player.getUniqueId());
        if (args.length == 0) {
            player.sendMessage(ChatColor.GOLD + "---------------"
                    + ChatColor.RED + "Homespawn" + ChatColor.GOLD
                    + "---------------");
            player.sendMessage(ChatColor.RED + "Author:"
                    + ChatColor.GOLD + " Dart2112");
            player.sendMessage(ChatColor.RED + "Version: "
                    + ChatColor.GOLD
                    + this.plugin.getDescription().getVersion());
            String version = System.getProperty("java.version");
            int pos = version.indexOf('.');
            pos = version.indexOf('.', pos + 1);
            Double versionDouble = Double.parseDouble(version.substring(0, pos));
            player.sendMessage(ChatColor.RED + "Java Version: " + ChatColor.GOLD
                    + versionDouble);
            player.sendMessage(ChatColor.RED + "Spigot:"
                    + ChatColor.GOLD + " https://goo.gl/aWby6W");
            player.sendMessage(ChatColor.RED
                    + "Use /homespawn Help For Commands!");
            player.sendMessage(ChatColor.GOLD
                    + "-----------------------------------------");
        } else if (args.length == 1 && !args[0].equalsIgnoreCase("player")) {
            if (args[0].equalsIgnoreCase("reload")) {
                if (perms.get(HomeSpawnPermissions.perm.reload) == 1) {
                    this.plugin.HSConfig.reload(player);
                } else {
                    player.sendMessage(ChatColor.RED
                            + "You Don't Have Permission To Do That");
                }
            } else if (args[0].equalsIgnoreCase("help")) {
                this.plugin.help(player);
            }
        } else if (args.length > 0) {
            if (args[0].equalsIgnoreCase("update")) {
                if (perms.get(HomeSpawnPermissions.perm.updateNotify) == 1) {
                    if (plugin.lapisUpdater.checkUpdate()) {
                        player.sendMessage(ChatColor.GOLD + "Update found, Downloading it now\n it will be installed on next server restart");
                        plugin.lapisUpdater.downloadUpdate();
                    } else {
                        player.sendMessage(ChatColor.GOLD + "No updates found");
                    }
                } else {
                    player.sendMessage(plugin.HSConfig.getColoredMessage("NoPerms"));
                }
            } else if (args[0].equalsIgnoreCase("player")) {
                homeSpawnPlayer.homeSpawnPlayer(args, player);
            }
        } else {
            player.sendMessage("That Is Not A Recognised Command, Use /homespawn help For " +
                    "Commands");
        }
    }

}
