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
import net.lapismc.HomeSpawn.api.events.HomeDelEvent;
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
        YamlConfiguration getHomes = plugin.HSConfig.getPlayerData(p.getUniqueId());
        List<String> list = getHomes.getStringList("Homes.list");
        if (args.length == 0) {
            if (list.contains("Home")) {
                HomeDelEvent HDE = new HomeDelEvent(p, p.getLocation(), "Home");
                Bukkit.getPluginManager().callEvent(HDE);
                if (HDE.isCancelled()) {
                    p.sendMessage("Your home has not been deleted because " + HDE.getReason());
                    return;
                }
                p.sendMessage(plugin.HSConfig.getColoredMessage("Home.HomeRemoved"));
                getHomes.set("Homes.Home", null);
                if (list.contains("Home")) {
                    list.remove("Home");
                    getHomes.set("Homes.list", list);
                }
                this.plugin.HSConfig.savePlayerData(p.getUniqueId(), getHomes);
            } else {
                p.sendMessage(plugin.HSConfig.getColoredMessage("Home.NoHomeSet"));
                if (!list.isEmpty()) {
                    String list2 = list.toString();
                    String list3 = list2.replace("[", " ");
                    String StringList = list3.replace("]", " ");
                    p.sendMessage(plugin.HSConfig.getColoredMessage("Home.CurrentHomes"));
                    p.sendMessage(plugin.SecondaryColor + StringList);
                } else {
                    p.sendMessage(plugin.HSConfig.getColoredMessage("Home.NoHomeSet"));
                }
            }
        } else if (args.length == 1) {
            String home = args[0];
            if (!list.contains(home)) {
                p.sendMessage(plugin.HSConfig.getColoredMessage("Home.NoHomeName"));
                if (!list.isEmpty()) {
                    String list2 = list.toString();
                    String list3 = list2.replace("[", " ");
                    String StringList = list3.replace("]", " ");
                    p.sendMessage(plugin.HSConfig.getColoredMessage("Home.CurrentHomes"));
                    p.sendMessage(plugin.SecondaryColor + StringList);
                } else {
                    p.sendMessage(plugin.HSConfig.getColoredMessage("Home.NoHomeSet"));
                }
            } else {
                HomeDelEvent HDE = new HomeDelEvent(p, p.getLocation(), home);
                Bukkit.getPluginManager().callEvent(HDE);
                if (HDE.isCancelled()) {
                    p.sendMessage("Your home has not been deleted because " + HDE.getReason());
                    return;
                }
                p.sendMessage(plugin.HSConfig.getColoredMessage("Home.HomeRemoved"));
                getHomes.set(home, null);
                if (list.contains(home)) {
                    list.remove(home);
                    getHomes.set("Homes.list", list);
                }
                this.plugin.HSConfig.savePlayerData(p.getUniqueId(), getHomes);
            }
        } else {
            p.sendMessage(plugin.HSConfig.getColoredMessage("Error.Args+"));
        }
    }

}
