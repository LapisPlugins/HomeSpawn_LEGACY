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

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class HomeSpawnListener implements Listener {

    private List<Player> Players = new ArrayList<>();
    private HomeSpawn plugin;

    public HomeSpawnListener(HomeSpawn plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void PlayerJoinEvent(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        File file = new File(plugin.getDataFolder() + File.separator
                + "PlayerData" + File.separator
                + player.getUniqueId() + ".yml");
        YamlConfiguration getHomes = plugin.HSConfig.getPlayerData(player.getUniqueId());
        if (file == null) {
            plugin.logger.severe("Player " + player.getName()
                    + "'s Data File Is Null!");
            return;
        }
        if (!file.exists()) {
            try {
                file.createNewFile();
                getHomes.set("UUID", player.getUniqueId().toString());
                getHomes.set("UserName", player.getName());
                getHomes.set("Permission", "-");
                getHomes.set("login", System.currentTimeMillis());
                getHomes.set("logout", "-");
                getHomes.set("Homes.list", new ArrayList<String>());
                getHomes.save(file);
                plugin.spawnNew(player);
                if (plugin.getConfig().getBoolean("CommandBook")) {
                    PlayerInventory pi = player.getInventory();
                    HomeSpawnBook book = new HomeSpawnBook(plugin);
                    ItemStack commandBook = book.getBook();
                    pi.addItem(commandBook);
                }
            } catch (IOException e) {
                e.printStackTrace();
                plugin.logger
                        .severe("[HomeSpawn] Player Data File Creation Failed!");
                return;
            }
        }
        getHomes = plugin.HSConfig.getPlayerData(player.getUniqueId());
        HashMap<HomeSpawnPermissions.perm, Integer> perms = plugin.HSPermissions.getPlayerPermissions(player.getUniqueId());
        if (!player.getName().equals(getHomes.getString("UserName"))) {
            plugin.logger.info("Player " + getHomes.getString("UserName") + " has changed their name to " + player.getName());
            getHomes.set("UserName", player.getName());
        }
        if (perms.get(HomeSpawnPermissions.perm.updateNotify) == 1) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
                @Override
                public void run() {
                    if (!plugin.getConfig().getBoolean("DownloadUpdates") && plugin.lapisUpdater.checkUpdate("HomeSpawn")) {
                        player.sendMessage(ChatColor.DARK_GRAY
                                + "[" + ChatColor.AQUA + "HomeSpawn" + ChatColor.DARK_GRAY
                                + "]" + ChatColor.GOLD + " An update is available! run \"/homespawn update\"" +
                                " to install it!");
                    }
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
        if (plugin.HomeSpawnLocations.containsKey(p)) {
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
                if (From1.equals(To1)) {
                    return;
                } else {
                    if (!Players.contains(p)) {
                        plugin.HomeSpawnLocations.put(p, null);
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
                        plugin.HomeSpawnLocations.put(p, null);
                        p.sendMessage(plugin.HSConfig.getColoredMessage("TeleportCancelPvP"));
                    } else if (arrow.getShooter() instanceof Skeleton) {
                        Players.add(p);
                        e.setCancelled(true);
                    }
                }
                if (Hitter instanceof Wolf) {
                    Wolf wolf = (Wolf) Hitter;
                    if (wolf.isTamed()) {
                        plugin.HomeSpawnLocations.put(p, null);
                        p.sendMessage(plugin.HSConfig.getColoredMessage("TeleportCancelPvP"));
                    } else {
                        Players.add(p);
                        e.setCancelled(true);
                    }
                }
                if (Hitter instanceof Player) {
                    plugin.HomeSpawnLocations.put(p, null);
                    plugin.HomeSpawnTimeLeft.remove(p);
                    p.sendMessage(plugin.HSConfig.getColoredMessage("TeleportCancelPvP"));
                } else {
                    Players.add(p);
                    e.setCancelled(false);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void invInteract(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if (e.getInventory().equals(plugin.HSCommand.homesList.HomesListInvs.get(p))) {
            String name = e.getCurrentItem().getItemMeta().getDisplayName();
            String name1 = ChatColor.stripColor(name);
            YamlConfiguration getHomes = plugin.HSConfig.getPlayerData(p.getUniqueId());
            if (name1.equalsIgnoreCase("Home")) {
                if (getHomes.getStringList("Homes.list").contains(name1)) {
                    Location home = (Location) getHomes.get("Homes." + name1);
                    home.add(0.5, 0, 0.5);
                    TeleportPlayer(p, home);
                }
            } else {
                if (getHomes.getStringList("Homes.list").contains(name1)) {
                    Location home2 = (Location) getHomes.get("Homes." + name1);
                    home2.add(0.5, 0, 0.5);
                    TeleportPlayer(p, home2);
                }
            }
            e.getWhoClicked().closeInventory();
            Inventory inv = plugin.HSCommand.homesList.HomesListInvs.get(p);
            inv.clear();
            plugin.HSCommand.homesList.HomesListInvs.put(p, inv);
        } else {
            return;
        }

    }

    @EventHandler
    public void onInvExit(InventoryCloseEvent e) {
        if (!(e.getPlayer() == null && e.getInventory() == null)) {
            Player p = (Player) e.getPlayer();
            if (plugin.HSCommand.homesList.HomesListInvs.containsKey(p) && e.getInventory().getName() ==
                    plugin.HSCommand.homesList.HomesListInvs.get(p).getName()) {
                Inventory inv = plugin.HSCommand.homesList.HomesListInvs.get(p);
                inv.clear();
                plugin.HSCommand.homesList.HomesListInvs.put(p, inv);
            }
        }
    }

    private void TeleportPlayer(Player p, Location l) {
        HashMap<HomeSpawnPermissions.perm, Integer> perms = plugin.HSPermissions.getPlayerPermissions(p.getUniqueId());
        if (perms.get(HomeSpawnPermissions.perm.TeleportDelay) == 0) {
            p.teleport(l);
            p.sendMessage(plugin.HSConfig.getColoredMessage("Home.SentHome"));
        } else {
            String waitraw = plugin.HSConfig.getColoredMessage("Wait");
            String Wait = waitraw.replace("{time}", perms.get(HomeSpawnPermissions.perm.TeleportDelay).toString());
            p.sendMessage(Wait);
            plugin.HomeSpawnLocations.put(p, l);
            plugin.HomeSpawnTimeLeft.put(p, perms.get(HomeSpawnPermissions.perm.TeleportDelay));
        }

    }
}
