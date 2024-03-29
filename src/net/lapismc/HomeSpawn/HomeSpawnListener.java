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

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

class HomeSpawnListener implements Listener {

    private final HomeSpawn plugin;
    private List<Player> Players = new ArrayList<>();

    HomeSpawnListener(HomeSpawn plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void PlayerJoinEvent(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        File file = new File(plugin.getDataFolder() + File.separator
                + "PlayerData" + File.separator
                + player.getUniqueId() + ".yml");
        plugin.HSConfig.getPlayerData(player.getUniqueId());
        YamlConfiguration getHomes;
        if (!file.exists()) {
            plugin.HSConfig.generateNewPlayerData(file, player);
        }
        getHomes = plugin.HSConfig.getPlayerData(player.getUniqueId());
        HashMap<HomeSpawnPermissions.perm, Integer> perms = plugin.HSPermissions.getPlayerPermissions(player.getUniqueId());
        if (!player.getName().equals(getHomes.getString("UserName")) && getHomes.getString("UserName") != null) {
            plugin.logger.info("Player " + getHomes.getString("UserName") + " has changed their name to " + player.getName());
            getHomes.set("UserName", player.getName());
        }
        if (perms.get(HomeSpawnPermissions.perm.updateNotify) == 1) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                if (!plugin.getConfig().getBoolean("DownloadUpdates") && plugin.lapisUpdater.checkUpdate()) {
                    player.sendMessage(ChatColor.DARK_GRAY
                            + "[" + ChatColor.AQUA + "HomeSpawn" + ChatColor.DARK_GRAY
                            + "]" + ChatColor.GOLD + " An update is available! run \"/homespawn update\"" +
                            " to install it!");
                }
            });
        }
        Date date = new Date();
        getHomes.set("login", date.getTime());
        plugin.HSConfig.savePlayerData(player.getUniqueId(), getHomes);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void PlayerQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        YamlConfiguration homes = plugin.HSConfig.getPlayerData(p.getUniqueId());
        Date date = new Date();
        homes.set("logout", date.getTime());
        plugin.HSConfig.savePlayerData(p.getUniqueId(), homes);
        plugin.HSConfig.unloadPlayerData(p.getUniqueId());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void OnPlayerMove(PlayerMoveEvent e) {
        if (Players == null) {
            Players = new ArrayList<>();
        }
        Player p = e.getPlayer();
        if (plugin.HomeSpawnHomes.containsKey(p)) {
            if (plugin.HomeSpawnTimeLeft.containsKey(p)) {
                Location From = e.getFrom();
                Location To = e.getTo();
                List<Integer> To1 = new ArrayList<>();
                List<Integer> From1 = new ArrayList<>();
                To1.add(To.getBlockX());
                To1.add(To.getBlockY());
                To1.add(To.getBlockZ());
                From1.add(From.getBlockX());
                From1.add(From.getBlockY());
                From1.add(From.getBlockZ());
                if (!From1.equals(To1)) {
                    if (!Players.contains(p)) {
                        plugin.HomeSpawnHomes.put(p, null);
                        plugin.HomeSpawnTimeLeft.remove(p);
                        p.sendMessage(plugin.HSConfig.getColoredMessage("TeleportCancelMove"));
                    } else {
                        e.setCancelled(true);
                        plugin.HomeSpawnTimeLeft.put(p, 1);
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void OnPlayerDamage(EntityDamageByEntityEvent e) {
        Entity Hitter = e.getDamager();
        Entity Hit = e.getEntity();
        if (Hit instanceof Player) {
            Player p = (Player) Hit;
            if (plugin.HomeSpawnTimeLeft.containsKey(p)) {
                if (Hitter instanceof Arrow) {
                    Arrow arrow = (Arrow) Hitter;
                    if (arrow.getShooter() instanceof Player) {
                        plugin.HomeSpawnHomes.put(p, null);
                        p.sendMessage(plugin.HSConfig.getColoredMessage("TeleportCancelPvP"));
                        e.setCancelled(true);
                    } else if (arrow.getShooter() instanceof Skeleton) {
                        Players.add(p);
                        e.setCancelled(true);
                    }
                }
                if (Hitter instanceof Wolf) {
                    Wolf wolf = (Wolf) Hitter;
                    if (wolf.isTamed()) {
                        plugin.HomeSpawnHomes.put(p, null);
                        p.sendMessage(plugin.HSConfig.getColoredMessage("TeleportCancelPvP"));
                    } else {
                        Players.add(p);
                        e.setCancelled(true);
                    }
                }
                if (Hitter instanceof Player) {
                    plugin.HomeSpawnHomes.put(p, null);
                    plugin.HomeSpawnTimeLeft.remove(p);
                    p.sendMessage(plugin.HSConfig.getColoredMessage("TeleportCancelPvP"));
                } else {
                    Players.add(p);
                    e.setCancelled(false);
                }
            }
        }
    }
}
