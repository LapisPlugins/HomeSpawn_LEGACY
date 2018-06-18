/*
 * Copyright 2018 Benjamin Martin
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
import net.lapismc.HomeSpawn.api.events.HomeMoveEvent;
import net.lapismc.HomeSpawn.api.events.HomeSetEvent;
import net.lapismc.HomeSpawn.playerdata.Home;
import net.lapismc.HomeSpawn.playerdata.HomeSpawnPlayer;
import net.lapismc.HomeSpawn.util.LapisCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class HomeSpawnSetHome extends LapisCommand {

    private HomeSpawn plugin;

    public HomeSpawnSetHome(HomeSpawn p) {
        super("sethome", "Sets your home so you can use /home to get back there", new ArrayList<>());
        this.plugin = p;
    }

    @Override
    public void onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.HSConfig.getMessage("Error.MustBePlayer"));
            return;
        }
        Player p = (Player) sender;
        HomeSpawnPlayer HSPlayer = new HomeSpawnPlayer(plugin, p.getUniqueId());
        YamlConfiguration getHomes = HSPlayer.getConfig(false);
        List<String> list = getHomes.getStringList("Homes.list");
        if (HSPlayer.getHomes().size() >= HSPlayer.getPermissionValue(HomeSpawnPermissions.perm.homes)) {
            p.sendMessage(plugin.HSConfig.getColoredMessage("Home.LimitReached"));
            return;
        }

        if (args.length == 0) {
            Home home = new Home(plugin, "Home", p.getLocation(), p.getUniqueId());
            if (!list.contains("Home")) {
                HomeSetEvent HCE = new HomeSetEvent(p, home);
                Bukkit.getPluginManager().callEvent(HCE);
                if (HCE.isCancelled()) {
                    p.sendMessage("Your home has not been set because " + HCE.getReason());
                    return;
                }
            } else {
                Home oldHome = HSPlayer.getHome("Home");
                HomeMoveEvent HCE = new HomeMoveEvent(p, oldHome, home);
                Bukkit.getPluginManager().callEvent(HCE);
                if (HCE.isCancelled()) {
                    p.sendMessage("Your home has moved been set because " + HCE.getReason());
                    return;
                }
                HSPlayer.removeHome(oldHome);
            }
            if (!list.contains("Home")) {
                list.add("Home");
                getHomes.set("Homes.list", list);
            }
            home.setLocation(p.getLocation());
            HSPlayer.addHome(home);
            HSPlayer.saveConfig(getHomes);
            HSPlayer.reloadHomes();
            p.sendMessage(plugin.HSConfig.getColoredMessage("Home.HomeSet"));
        } else if (args.length == 1) {
            if (HSPlayer.isPermitted(HomeSpawnPermissions.perm.customHomes)) {
                String homeName = args[0];
                if (homeName.equalsIgnoreCase("Home")) {
                    p.sendMessage(plugin.SecondaryColor + "You Cannot Use The HomeSpawnHome Name \"Home\", Please Choose Another!");
                    return;
                }
                Home home = new Home(plugin, homeName, p.getLocation(), p.getUniqueId());
                if (!list.contains(homeName)) {
                    HomeSetEvent HCE = new HomeSetEvent(p, home);
                    Bukkit.getPluginManager().callEvent(HCE);
                    if (HCE.isCancelled()) {
                        p.sendMessage("Your home has not been set because " + HCE.getReason());
                        return;
                    }
                } else {
                    Home oldHome = HSPlayer.getHome(homeName);
                    HomeMoveEvent HCE = new HomeMoveEvent(p, oldHome, home);
                    Bukkit.getPluginManager().callEvent(HCE);
                    if (HCE.isCancelled()) {
                        p.sendMessage("Your home has not been moved been because " + HCE.getReason());
                        return;
                    }
                    HSPlayer.removeHome(oldHome);
                }
                if (!list.contains(homeName)) {
                    list.add(homeName);
                    getHomes.set("Homes.list", list);
                }
                home.setLocation(p.getLocation());
                HSPlayer.addHome(home);
                HSPlayer.saveConfig(getHomes);
                HSPlayer.reloadHomes();
                p.sendMessage(plugin.HSConfig.getColoredMessage("Home.HomeSet"));
            } else {
                p.sendMessage(plugin.HSConfig.getColoredMessage("NoPerms"));
            }
        } else {
            p.sendMessage(plugin.HSConfig.getColoredMessage("Error.Args+"));
        }
    }

}
