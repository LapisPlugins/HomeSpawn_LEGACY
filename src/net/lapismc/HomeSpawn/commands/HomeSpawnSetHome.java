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
import net.lapismc.HomeSpawn.api.events.HomeSetEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;

public class HomeSpawnSetHome {

    HomeSpawn plugin = null;

    public HomeSpawnSetHome(HomeSpawn p) {
        this.plugin = p;
    }

    public void setHome(String[] args, Player player) {
        HashMap<HomeSpawnPermissions.perm, Integer> perms = plugin.HSPermissions.getPlayerPermissions(player.getUniqueId());
        YamlConfiguration getHomes = this.plugin.HSConfig.getPlayerData(player.getUniqueId());
        if (!getHomes.contains(player.getUniqueId()
                + ".list")) {
            getHomes.createSection(player.getUniqueId()
                    + ".list");
            this.plugin.HSConfig.savePlayerData(player.getUniqueId(), getHomes);
        }
        List<String> list = getHomes.getStringList(player
                .getUniqueId() + ".list");
        if (getHomes.getInt(player.getUniqueId()
                + ".Numb") >= perms.get(HomeSpawnPermissions.perm.homes)) {
            player.sendMessage(plugin.HSConfig.getColoredMessage("Home.LimitReached"));
            return;
        }

        if (args.length == 0) {
            if (!getHomes.contains(player.getUniqueId().toString() + ".Numb")) {
                getHomes.set(player.getUniqueId().toString() + ".Numb", 0);
            }
            HomeSetEvent HCE = new HomeSetEvent(plugin, player, player.getLocation(), "Home");
            Bukkit.getPluginManager().callEvent(HCE);
            if (HCE.isCancelled()) {
                player.sendMessage("Your home has not been set because " + HCE.getReason());
                return;
            }
            int HomesNumb = getHomes.getInt(player.getUniqueId() + ".Numb");
            if (!list.contains("Home")) {
                getHomes.set(player.getUniqueId() + ".Numb", HomesNumb + 1);
                list.add("Home");
                getHomes.set(player.getUniqueId()
                        + ".list", list);
            }
            getHomes.set(player.getUniqueId() + ".x",
                    player.getLocation().getBlockX());
            getHomes.set(player.getUniqueId() + ".y",
                    player.getLocation().getBlockY());
            getHomes.set(player.getUniqueId() + ".z",
                    player.getLocation().getBlockZ());
            getHomes.set(player.getUniqueId()
                    + ".world", player.getWorld().getName());
            getHomes.set(player.getUniqueId()
                    + ".Yaw", player.getLocation().getYaw());
            getHomes.set(player.getUniqueId()
                    + ".Pitch", player.getLocation().getPitch());
            getHomes.set("HasHome", "Yes");
            player.sendMessage(plugin.HSConfig.getColoredMessage("Home.HomeSet"));
        } else if (args.length == 1) {
            if (perms.get(HomeSpawnPermissions.perm.customHomes) == 1) {
                String home = args[0];
                if (home.equalsIgnoreCase("Home")) {
                    player.sendMessage(ChatColor.RED
                            + "You Cannot Use The HomeSpawnHome Name \"Home\", Please Choose Another!");
                    return;
                }
                HomeSetEvent HCE = new HomeSetEvent(plugin, player, player.getLocation(), home);
                Bukkit.getPluginManager().callEvent(HCE);
                if (HCE.isCancelled()) {
                    player.sendMessage("Your home has not been set because " + HCE.getReason());
                    return;
                }
                if (!getHomes.contains(player.getUniqueId() + ".Numb")) {
                    getHomes.set(player.getUniqueId() + ".Numb", 0);
                }
                int HomesNumb = getHomes.getInt(player.getUniqueId() + ".Numb");
                if (!list.contains(home)) {
                    list.add(home);
                    getHomes.set(player.getUniqueId() + ".Numb", HomesNumb + 1);
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
                player.sendMessage(plugin.HSConfig.getColoredMessage("Home.HomeSet"));
            } else {
                player.sendMessage(plugin.HSConfig.getColoredMessage("NoPerms"));
            }
        } else {
            player.sendMessage(plugin.HSConfig.getColoredMessage("Error.Args+"));
        }
        this.plugin.HSConfig.savePlayerData(player.getUniqueId(), getHomes);
    }

}
