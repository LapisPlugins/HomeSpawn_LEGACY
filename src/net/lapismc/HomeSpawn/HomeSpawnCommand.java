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

package net.lapismc.HomeSpawn;

import net.lapismc.HomeSpawn.commands.*;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HomeSpawnCommand implements TabCompleter {

    private final HomeSpawn plugin;
    HomeSpawnHomesList homesList;

    HomeSpawnCommand(HomeSpawn plugin) {
        List<String> disabledCommands = plugin.getConfig().getStringList("DisabledCommands");
        this.plugin = plugin;
        if (!disabledCommands.contains("homespawn"))
            new net.lapismc.HomeSpawn.commands.HomeSpawn(plugin);
        if (!disabledCommands.contains("delhome"))
            new HomeSpawnDelHome(plugin);
        if (!disabledCommands.contains("delspawn"))
            new HomeSpawnDelSpawn(plugin);
        if (!disabledCommands.contains("home"))
            new HomeSpawnHome(plugin);
        if (!disabledCommands.contains("homeslist"))
            homesList = new HomeSpawnHomesList(plugin);
        if (!disabledCommands.contains("sethome"))
            new HomeSpawnSetHome(plugin);
        if (!disabledCommands.contains("renamehome"))
            new HomeSpawnRenameHome(plugin);
        if (!disabledCommands.contains("setspawn"))
            new HomeSpawnSetSpawn(plugin);
        if (!disabledCommands.contains("spawn"))
            new HomeSpawnSpawn(plugin, this);
        if (!disabledCommands.contains("homepassword"))
            new HomeSpawnHomePassword(plugin);
        plugin.logger.info("Commands Registered!");
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String alias,
                                      String[] args) {
        //checks if a player is attempting to tab complete a home name
        if (command.getName().equalsIgnoreCase("home") || command.getName().equalsIgnoreCase("delhome")) {
            Player p = (Player) sender;
            YamlConfiguration playerData = plugin.getPlayer(p.getUniqueId()).getConfig(false);
            //Gets the list of the players homes and returns it for the tab complete to deal with
            List<String> l = new ArrayList<>();
            for (String home : playerData.getStringList("Homes.list")) {
                if (args.length > 0) {
                    if (home.toLowerCase().startsWith(args[0].toLowerCase())) {
                        l.add(home);
                    }
                } else {
                    l.add(home);
                }
            }
            plugin.debug("Tab Completed for " + sender.getName());
            return l;
        }
        return null;
    }


    @SuppressWarnings("deprecation")
    public void TeleportPlayer(Player p, Location l, String r) {
        HashMap<HomeSpawnPermissions.perm, Integer> perms = plugin.HSPermissions.getPlayerPermissions(p.getUniqueId());
        if (perms.get(HomeSpawnPermissions.perm.TeleportDelay) == 0) {
            if (!l.getChunk().isLoaded()) {
                l.getChunk().load();
            }
            if (p.isInsideVehicle()) {
                if (p.getVehicle() instanceof Horse) {
                    Horse horse = (Horse) p.getVehicle();
                    horse.eject();
                    horse.teleport(l);
                    p.teleport(l);
                    horse.setPassenger(p);
                }
            } else {
                p.teleport(l);
            }
            if (r.equalsIgnoreCase("Spawn")) {
                p.sendMessage(plugin.HSConfig.getColoredMessage("Spawn.SentToSpawn"));
            } else if (r.equalsIgnoreCase("Home")) {
                p.sendMessage(plugin.HSConfig.getColoredMessage("Home.SentHome"));
            }
        } else {
            String waitraw = plugin.HSConfig.getColoredMessage("Wait");
            String Wait = waitraw.replace("{time}", perms.get(HomeSpawnPermissions.perm.TeleportDelay).toString());
            p.sendMessage(Wait);
            this.plugin.HomeSpawnLocations.put(p, l);
            this.plugin.HomeSpawnTimeLeft.put(p, perms.get(HomeSpawnPermissions.perm.TeleportDelay));
        }
    }

}