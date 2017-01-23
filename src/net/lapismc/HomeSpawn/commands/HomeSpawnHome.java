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
import net.lapismc.HomeSpawn.HomeSpawnCommand;
import net.lapismc.HomeSpawn.api.events.HomeTeleportEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.List;

public class HomeSpawnHome {

    private HomeSpawn plugin;
    private HomeSpawnCommand hsc;

    public HomeSpawnHome(HomeSpawn p, HomeSpawnCommand hsc) {
        this.plugin = p;
        this.hsc = hsc;
    }

    public void home(String[] args, Player player) {
        YamlConfiguration getHomes = this.plugin.HSConfig.getPlayerData(player.getUniqueId());
        List<String> list = getHomes.getStringList("Homes.list");
        if (args.length == 0) {
            if (list.contains("Home")) {
                Location home = (Location) getHomes.get("Homes.Home");
                HomeTeleportEvent HTE = new HomeTeleportEvent(plugin, player, home, "Home");
                Bukkit.getPluginManager().callEvent(HTE);
                if (HTE.isCancelled()) {
                    player.sendMessage(ChatColor.GOLD + "Your teleport was cancelled because " + HTE.getCancelReason());
                    return;
                }
                hsc.TeleportPlayer(player, home, "Home", "Home");
            } else {
                player.sendMessage(plugin.HSConfig.getColoredMessage("Home.NoHomeSet"));
            }
        } else if (args.length == 1) {
            String home = args[0];
            if (!list.contains(home)) {
                if (!list.isEmpty()) {
                    player.sendMessage(plugin.HSConfig.getColoredMessage("Home.NoHomeName"));
                    String list2 = list.toString();
                    String list3 = list2.replace("[", " ");
                    String StringList = list3.replace("]",
                            " ");
                    player.sendMessage(plugin.HSConfig.getColoredMessage("Home.CurrentHomes"));
                    player.sendMessage(ChatColor.RED + StringList);
                } else {
                    player.sendMessage(plugin.HSConfig.getColoredMessage("Home.NoHomeSet"));
                }
            } else {
                Location home2 = (Location) getHomes.get("Homes." + home);
                HomeTeleportEvent HTE = new HomeTeleportEvent(plugin, player, home2, home);
                Bukkit.getPluginManager().callEvent(HTE);
                if (HTE.isCancelled()) {
                    player.sendMessage(ChatColor.GOLD + "Your teleport was cancelled because " + HTE.getCancelReason());
                    return;
                }
                hsc.TeleportPlayer(player, home2, "Home", home);
            }
        }
    }
}



