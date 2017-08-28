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

package net.lapismc.HomeSpawn;

import net.lapismc.HomeSpawn.commands.*;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class HomeSpawnCommand implements CommandExecutor {

    private final HomeSpawn plugin;
    HomeSpawnHomesList homesList;
    private HomeSpawnHome home;
    private HomeSpawnSpawn spawn;
    private HomeSpawnDelHome delHome;
    private HomeSpawnDelSpawn delSpawn;
    private HomeSpawnHomePassword homePassword;
    private net.lapismc.HomeSpawn.commands.HomeSpawn homeSpawn;
    private HomeSpawnSetHome setHome;
    private HomeSpawnRenameHome renameHome;
    private HomeSpawnSetSpawn setSpawn;

    HomeSpawnCommand(HomeSpawn plugin) {
        this.plugin = plugin;
        plugin.getCommand("home").setExecutor(plugin.HSCommand);
        plugin.getCommand("sethome").setExecutor(plugin.HSCommand);
        plugin.getCommand("renamehome").setExecutor(plugin.HSCommand);
        plugin.getCommand("delhome").setExecutor(plugin.HSCommand);
        plugin.getCommand("homeslist").setExecutor(plugin.HSCommand);
        plugin.getCommand("spawn").setExecutor(plugin.HSCommand);
        plugin.getCommand("setspawn").setExecutor(plugin.HSCommand);
        plugin.getCommand("delspawn").setExecutor(plugin.HSCommand);
        plugin.getCommand("homepassword").setExecutor(plugin.HSCommand);
        plugin.getCommand("homespawn").setExecutor(plugin.HSCommand);
        this.delHome = new HomeSpawnDelHome(plugin);
        this.delSpawn = new HomeSpawnDelSpawn(plugin);
        this.home = new HomeSpawnHome(plugin);
        this.homesList = new HomeSpawnHomesList(plugin);
        this.setHome = new HomeSpawnSetHome(plugin);
        this.renameHome = new HomeSpawnRenameHome(plugin);
        this.setSpawn = new HomeSpawnSetSpawn(plugin);
        this.spawn = new HomeSpawnSpawn(plugin, this);
        this.homePassword = new HomeSpawnHomePassword(plugin);
        this.homeSpawn = new net.lapismc.HomeSpawn.commands.HomeSpawn(plugin);
        plugin.logger.info("Commands Registered!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (cmd.getName().equalsIgnoreCase("sethome")) {
            setHome.setHome(args, sender);
        } else if (cmd.getName().equalsIgnoreCase("renamehome")) {
            renameHome.renameHome(args, sender);
        } else if (cmd.getName().equalsIgnoreCase("home")) {
            home.home(args, sender);
        } else if (cmd.getName().equalsIgnoreCase("delhome")) {
            delHome.delHome(args, sender);
        } else if (cmd.getName().equalsIgnoreCase("setspawn")) {
            setSpawn.setSpawn(args, sender);
        } else if (cmd.getName().equals("spawn")) {
            spawn.spawn(sender);
        } else if (cmd.getName().equalsIgnoreCase("delspawn")) {
            delSpawn.delSpawn(args, sender);
        } else if (cmd.getName().equalsIgnoreCase("homeslist")) {
            homesList.homesList(sender);
        } else if (cmd.getName().equalsIgnoreCase("homespawn")) {
            homeSpawn.homeSpawn(args, sender);
        } else if (cmd.getName().equalsIgnoreCase("homepassword")) {
            homePassword.homePassword(args, sender);
        }
        return false;
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