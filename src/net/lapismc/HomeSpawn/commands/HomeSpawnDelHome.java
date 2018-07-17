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
import net.lapismc.HomeSpawn.api.events.HomeDeleteEvent;
import net.lapismc.HomeSpawn.playerdata.Home;
import net.lapismc.HomeSpawn.playerdata.HomeSpawnPlayer;
import net.lapismc.HomeSpawn.util.LapisCommand;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class HomeSpawnDelHome extends LapisCommand {

    private final HomeSpawn plugin;

    public HomeSpawnDelHome(HomeSpawn p) {
        super("delhome", "Removes your home, this means you cant use /home until you reset your home with /sethome", new ArrayList<>());
        this.plugin = p;
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.HSConfig.getMessage("Error.MustBePlayer"));
            return;
        }
        Player p = (Player) sender;
        HomeSpawnPlayer HSPlayer = new HomeSpawnPlayer(plugin, p.getUniqueId());
        YamlConfiguration getHomes = HSPlayer.getConfig(false);
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
                HSPlayer.reloadHomes();
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
            if (HSPlayer.hasHome(homeName)) {
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
                getHomes.set("Homes." + home.getName(), null);
                HSPlayer.removeHome(home);
                if (list.contains(home.getName())) {
                    list.remove(home.getName());
                    getHomes.set("Homes.list", list);
                }
                HSPlayer.saveConfig(getHomes);
                HSPlayer.reloadHomes();
            }
        } else if (args.length == 2) {
            boolean permitted = plugin.HSPermissions.isPermitted(p.getUniqueId(), HomeSpawnPermissions.perm.playerStats);
            if (!permitted) {
                p.sendMessage(plugin.HSConfig.getColoredMessage("NoPerms"));
                return;
            }
            String homeName = args[0];
            String playerName = args[1];
            //noinspection deprecation
            OfflinePlayer op = Bukkit.getOfflinePlayer(playerName);
            if (!op.hasPlayedBefore()) {
                sender.sendMessage(plugin.HSConfig.getColoredMessage("NoPlayerData"));
            }
            HSPlayer = plugin.getPlayer(op.getUniqueId());
            if (HSPlayer.hasHome(homeName)) {
                getHomes = HSPlayer.getConfig(false);
                Home h = HSPlayer.getHome(homeName);
                HSPlayer.removeHome(h);
                p.sendMessage(plugin.HSConfig.getColoredMessage("Home.HomeRemoved"));
                getHomes.set("Homes." + h.getName(), null);
                HSPlayer.removeHome(h);
                if (list.contains(h.getName())) {
                    list.remove(h.getName());
                    getHomes.set("Homes.list", list);
                }
                HSPlayer.saveConfig(getHomes);
                HSPlayer.reloadHomes();
            } else {
                sender.sendMessage(plugin.HSConfig.getColoredMessage("Home.NoHomeName"));
            }
        } else {
            p.sendMessage(plugin.HSConfig.getColoredMessage("Error.Args+"));
        }
    }

}
