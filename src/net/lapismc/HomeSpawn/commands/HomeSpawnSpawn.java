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
import net.lapismc.HomeSpawn.api.events.SpawnTeleportEvent;
import net.lapismc.HomeSpawn.util.LapisCommand;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;

public class HomeSpawnSpawn extends LapisCommand {

    private final HomeSpawn plugin;

    public HomeSpawnSpawn(HomeSpawn p) {
        super("spawn", "Sends you to the place that spawn was set", new ArrayList<>());
        plugin = p;
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.HSConfig.getMessage("Error.MustBePlayer"));
            return;
        }
        Player p = (Player) sender;
        HashMap<HomeSpawnPermissions.perm, Integer> perms = plugin.HSPermissions.getPlayerPermissions(p.getUniqueId());
        if (perms.get(HomeSpawnPermissions.perm.spawn) == 1) {
            if (plugin.HSConfig.spawn.get("spawn") != null) {
                Location Spawn = (Location) plugin.HSConfig.spawn.get("spawn");
                SpawnTeleportEvent STE = new SpawnTeleportEvent(p, Spawn);
                Bukkit.getPluginManager().callEvent(STE);
                if (STE.isCancelled()) {
                    p.sendMessage(plugin.PrimaryColor + "Your teleport was cancelled because " + STE.getCancelReason());
                    return;
                }
                TeleportPlayer(p, Spawn);
            } else {
                p.sendMessage(plugin.HSConfig.getColoredMessage("Spawn.NotSet"));
            }
        } else {
            p.sendMessage(plugin.HSConfig.getColoredMessage("NoPerms"));
        }
    }

    @SuppressWarnings("deprecation")
    private void TeleportPlayer(Player p, Location l) {
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
            p.sendMessage(plugin.HSConfig.getColoredMessage("Spawn.SentToSpawn"));
        } else {
            String waitRaw = plugin.HSConfig.getColoredMessage("Wait");
            String Wait = waitRaw.replace("{time}", perms.get(HomeSpawnPermissions.perm.TeleportDelay).toString());
            p.sendMessage(Wait);
            this.plugin.HomeSpawnLocations.put(p, l);
            this.plugin.HomeSpawnTimeLeft.put(p, perms.get(HomeSpawnPermissions.perm.TeleportDelay));
        }
    }

}
