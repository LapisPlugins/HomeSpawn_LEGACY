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
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;

public class HomeSpawnSetHome {

    private HomeSpawn plugin = null;

    public HomeSpawnSetHome(HomeSpawn p) {
        this.plugin = p;
    }

    public void setHome(String[] args, CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.HSConfig.getMessage("Error.MustBePlayer"));
            return;
        }
        Player p = (Player) sender;
        HashMap<HomeSpawnPermissions.perm, Integer> perms = plugin.HSPermissions.getPlayerPermissions(p.getUniqueId());
        YamlConfiguration getHomes = this.plugin.HSConfig.getPlayerData(p.getUniqueId());
        List<String> list = getHomes.getStringList("Homes.list");
        if (list.size() >= perms.get(HomeSpawnPermissions.perm.homes)) {
            p.sendMessage(plugin.HSConfig.getColoredMessage("Home.LimitReached"));
            return;
        }

        if (args.length == 0) {
            HomeSetEvent HCE = new HomeSetEvent(p, p.getLocation(), "Home");
            Bukkit.getPluginManager().callEvent(HCE);
            if (HCE.isCancelled()) {
                p.sendMessage("Your home has not been set because " + HCE.getReason());
                return;
            }
            if (!list.contains("Home")) {
                list.add("Home");
                getHomes.set("Homes.list", list);
            }
            getHomes.set("Homes.Home", p.getLocation());
            p.sendMessage(plugin.HSConfig.getColoredMessage("Home.HomeSet"));
        } else if (args.length == 1) {
            if (perms.get(HomeSpawnPermissions.perm.customHomes) == 1) {
                String home = args[0];
                if (home.equalsIgnoreCase("Home")) {
                    p.sendMessage(plugin.SecondaryColor + "You Cannot Use The HomeSpawnHome Name \"Home\", Please Choose Another!");
                    return;
                }
                HomeSetEvent HCE = new HomeSetEvent(p, p.getLocation(), home);
                Bukkit.getPluginManager().callEvent(HCE);
                if (HCE.isCancelled()) {
                    p.sendMessage("Your home has not been set because " + HCE.getReason());
                    return;
                }
                if (!list.contains(home)) {
                    list.add(home);
                    getHomes.set("Homes.list", list);
                }
                getHomes.set("Homes." + home, p.getLocation());
                p.sendMessage(plugin.HSConfig.getColoredMessage("Home.HomeSet"));
            } else {
                p.sendMessage(plugin.HSConfig.getColoredMessage("NoPerms"));
            }
        } else {
            p.sendMessage(plugin.HSConfig.getColoredMessage("Error.Args+"));
        }
        this.plugin.HSConfig.savePlayerData(p.getUniqueId(), getHomes);
    }

}
