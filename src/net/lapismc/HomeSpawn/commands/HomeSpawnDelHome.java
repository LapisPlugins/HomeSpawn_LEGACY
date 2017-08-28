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
import net.lapismc.HomeSpawn.api.events.HomeDeleteEvent;
import net.lapismc.HomeSpawn.playerdata.Home;
import net.lapismc.HomeSpawn.playerdata.HomeSpawnPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.List;

public class HomeSpawnDelHome {

    private HomeSpawn plugin;

    public HomeSpawnDelHome(HomeSpawn p) {
        this.plugin = p;
    }

    public void delHome(String[] args, CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.HSConfig.getMessage("Error.MustBePlayer"));
            return;
        }
        Player p = (Player) sender;
        HomeSpawnPlayer HSPlayer = new HomeSpawnPlayer(plugin, p.getUniqueId());
        YamlConfiguration getHomes = HSPlayer.getConfig();
        List<String> list = HSPlayer.getHomesStringList();
        if (args.length == 0) {
            if (list.contains("Home")) {
                Home home = HSPlayer.getHome("Home");
                HomeDeleteEvent HDE = new HomeDeleteEvent(p, home);
                Bukkit.getPluginManager().callEvent(HDE);
                if (HDE.isCancelled()) {
                    p.sendMessage("Your home has not been deleted because " + HDE.getReason());
                    return;
                }
                p.sendMessage(plugin.HSConfig.getColoredMessage("Home.HomeRemoved"));
                getHomes.set("Homes.Home", null);
                HSPlayer.removeHome(home);
                if (list.contains("Home")) {
                    list.remove("Home");
                    getHomes.set("Homes.list", list);
                }
                HSPlayer.saveConfig(getHomes);
            } else {
                p.sendMessage(plugin.HSConfig.getColoredMessage("Home.NoHomeSet"));
                if (!list.isEmpty()) {
                    p.sendMessage(plugin.HSConfig.getColoredMessage("Home.CurrentHomes"));
                    p.sendMessage(plugin.SecondaryColor + HSPlayer.getHomesList());
                } else {
                    p.sendMessage(plugin.HSConfig.getColoredMessage("Home.NoHomeSet"));
                }
            }
        } else if (args.length == 1) {
            String homeName = args[0];
            if (!list.contains(homeName)) {
                p.sendMessage(plugin.HSConfig.getColoredMessage("Home.NoHomeName"));
                if (!list.isEmpty()) {
                    p.sendMessage(plugin.HSConfig.getColoredMessage("Home.CurrentHomes"));
                    p.sendMessage(plugin.SecondaryColor + HSPlayer.getHomesList());
                } else {
                    p.sendMessage(plugin.HSConfig.getColoredMessage("Home.NoHomeSet"));
                }
            } else {
                Home home = HSPlayer.getHome(homeName);
                HomeDeleteEvent HDE = new HomeDeleteEvent(p, home);
                Bukkit.getPluginManager().callEvent(HDE);
                if (HDE.isCancelled()) {
                    p.sendMessage("Your home has not been deleted because " + HDE.getReason());
                    return;
                }
                p.sendMessage(plugin.HSConfig.getColoredMessage("Home.HomeRemoved"));
                getHomes.set(homeName, null);
                HSPlayer.removeHome(home);
                if (list.contains(homeName)) {
                    list.remove(homeName);
                    getHomes.set("Homes.list", list);
                }
                HSPlayer.saveConfig(getHomes);
            }
        } else {
            p.sendMessage(plugin.HSConfig.getColoredMessage("Error.Args+"));
        }
    }

}
