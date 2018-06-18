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

package net.lapismc.HomeSpawn.playerdata;

import net.lapismc.HomeSpawn.HomeSpawn;
import net.lapismc.HomeSpawn.HomeSpawnPermissions;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

@SuppressWarnings("deprecation")
public class Home {

    private final HomeSpawn plugin;
    private final OfflinePlayer owner;
    private Location location;
    private String name;

    public Home(HomeSpawn plugin, String name, Location loc, OfflinePlayer owner) {
        this.plugin = plugin;
        this.name = name;
        this.location = loc;
        this.owner = owner;
    }

    public Home(HomeSpawn plugin, String name, Location loc, UUID ownersUuid) {
        this.plugin = plugin;
        this.name = name;
        this.location = loc;
        this.owner = Bukkit.getOfflinePlayer(ownersUuid);
    }

    public void teleportPlayer(Player p) {
        if (location == null) {
            location = (Location) plugin.HSConfig.getPlayerData(getOwner().getUniqueId()).get("Homes." + name);
        }
        HashMap<HomeSpawnPermissions.perm, Integer> perms = plugin.HSPermissions.getPlayerPermissions(p.getUniqueId());
        if (perms.get(HomeSpawnPermissions.perm.TeleportDelay) == 0) {
            if (!location.getChunk().isLoaded()) {
                location.getChunk().load();
            }
            if (p.isInsideVehicle()) {
                if (p.getVehicle() instanceof Horse) {
                    Horse horse = (Horse) p.getVehicle();
                    horse.eject();
                    horse.teleport(location);
                    p.teleport(location);
                    horse.setPassenger(p);
                }
            } else {
                p.teleport(location);
            }
            p.sendMessage(plugin.HSConfig.getColoredMessage("Home.SentHome"));
        } else {
            String waitRaw = plugin.HSConfig.getColoredMessage("Wait");
            String wait = waitRaw.replace("{time}", perms.get(HomeSpawnPermissions.perm.TeleportDelay).toString());
            p.sendMessage(wait);
            plugin.HomeSpawnLocations.put(p, location);
            plugin.HomeSpawnTimeLeft.put(p, perms.get(HomeSpawnPermissions.perm.TeleportDelay));
        }
    }

    public void teleportPlayerNow(Player p) {
        if (location == null) {
            location = (Location) plugin.HSConfig.getPlayerData(getOwner().getUniqueId()).get("Homes." + name);
        }
        if (!location.getChunk().isLoaded()) {
            location.getChunk().load();
        }
        if (p.isInsideVehicle()) {
            if (p.getVehicle() instanceof Horse) {
                Horse horse = (Horse) p.getVehicle();
                horse.eject();
                horse.teleport(location);
                p.teleport(location);
                horse.setPassenger(p);
            }
        } else {
            p.teleport(location);
        }
        p.sendMessage(plugin.HSConfig.getColoredMessage("Home.SentHome"));
        plugin.debug("Teleported " + p.getName());
    }

    public Location getLocation() {
        if (location == null) {
            location = (Location) plugin.HSConfig.getPlayerData(getOwner().getUniqueId()).get("Homes." + name);
        }
        return location;
    }

    public void setLocation(Location loc) {
        HomeSpawnPlayer p = plugin.getPlayer(owner.getUniqueId());
        YamlConfiguration getHomes = p.getConfig(false);
        getHomes.set("Homes." + name, loc);
        p.saveConfig(getHomes);
        location = loc;
    }

    @SuppressWarnings("WeakerAccess")
    public OfflinePlayer getOwner() {
        return owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String newName) {
        name = newName;
    }

    @Override
    public String toString() {
        return name;
    }
}
