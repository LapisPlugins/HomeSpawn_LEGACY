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

import net.lapismc.HomeSpawn.api.events.HomeRenameEvent;
import net.lapismc.HomeSpawn.playerdata.Home;
import net.lapismc.HomeSpawn.playerdata.HomeSpawnPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.List;

public class HomeSpawnRenameHome {

    private net.lapismc.HomeSpawn.HomeSpawn plugin;

    public HomeSpawnRenameHome(net.lapismc.HomeSpawn.HomeSpawn p) {
        plugin = p;
    }

    public void renameHome(String[] args, CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.HSConfig.getMessage("Error.MustBePlayer"));
            return;
        }
        Player p = (Player) sender;
        HomeSpawnPlayer HSPlayer = new HomeSpawnPlayer(plugin, p.getUniqueId());
        if (args.length == 2) {
            String oldHome = args[0];
            String newHomeName = args[1];
            YamlConfiguration homes = HSPlayer.getConfig();
            List<String> list = HSPlayer.getHomesStringList();
            if (list.contains(oldHome)) {
                if (!list.contains(newHomeName)) {
                    Location loc = HSPlayer.getHome(oldHome).getLocation();
                    Home newHome = new Home(newHomeName, loc, p.getUniqueId());
                    HomeRenameEvent event = new HomeRenameEvent(p, HSPlayer.getHome(oldHome), newHome);
                    Bukkit.getPluginManager().callEvent(event);
                    if (event.isCancelled()) {
                        p.sendMessage(plugin.HSConfig.getColoredMessage("Error.ActionCancelled") + event.getReason());
                        return;
                    }
                    homes.set("Homes." + newHome, loc);
                    homes.set("Homes." + oldHome, null);
                    HSPlayer.removeHome(HSPlayer.getHome(oldHome));
                    HSPlayer.addHome(newHome);
                    list.remove(oldHome);
                    list.add(newHomeName);
                    homes.set("Homes.list", list);
                    HSPlayer.saveConfig(homes);
                    p.sendMessage(plugin.HSConfig.getColoredMessage("Home.HomeRenamed"));
                } else {
                    p.sendMessage(plugin.HSConfig.getColoredMessage("Home.HomeAlreadyExists"));
                }
            } else {
                p.sendMessage(plugin.HSConfig.getColoredMessage("Home.NoHomeName"));
            }
        } else {
            p.sendMessage(plugin.SecondaryColor + "Usage: /renamehome (current name) (new name)");
        }
    }

}
