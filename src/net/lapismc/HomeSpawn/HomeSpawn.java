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

import net.lapismc.HomeSpawn.playerdata.Home;
import net.lapismc.HomeSpawn.playerdata.HomeSpawnPlayer;
import net.lapismc.HomeSpawn.util.LapisUpdater;
import net.lapismc.HomeSpawn.util.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.*;
import java.util.logging.Logger;

public class HomeSpawn extends JavaPlugin {

    public final HashMap<Player, Home> HomeSpawnHomes = new HashMap<>();
    public final HashMap<Player, Integer> HomeSpawnTimeLeft = new HashMap<>();
    final Logger logger = getLogger();
    private final ArrayList<HomeSpawnPlayer> players = new ArrayList<>();
    public LapisUpdater lapisUpdater;
    public HomeSpawnPermissions HSPermissions;
    public HomeSpawnConfiguration HSConfig;
    public String PrimaryColor = ChatColor.GOLD.toString();
    public String SecondaryColor = ChatColor.RED.toString();

    @Override
    public void onEnable() {
        HSConfig = new HomeSpawnConfiguration(this);
        Enable();
        Update();
        HSPermissions = new HomeSpawnPermissions(this);
        new HomeSpawnCommand(this);
        teleportDelay();
        new Metrics(this);
    }

    private void Update() {
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            lapisUpdater = new LapisUpdater(this);
            //check for an update
            if (lapisUpdater.checkUpdate()) {
                //if there in an update but download is disabled and notification is enabled then notify in console
                if (getConfig().getBoolean("UpdateNotification") && !getConfig()
                        .getBoolean("DownloadUpdates")) {
                    logger.info("An update for HomeSpawn is available and can be" +
                            " downloaded and installed by running /homespawn update");
                } else if (getConfig().getBoolean("DownloadUpdates")) {
                    //if downloading updates is enabled then download it and notify console
                    lapisUpdater.downloadUpdate();
                    logger.info("Downloading Homespawn update, it will be installed " +
                            "on next restart!");
                }
            } else {
                //if there is no update and notify is enabled then notify console that there was no update
                if (getConfig().getBoolean("UpdateNotification")) {
                    logger.info("No Update Available");
                }
            }
        });
    }

    @Override
    public void onDisable() {
        Disable();
    }

    private void Enable() {
        logger.info("V." + getDescription().getVersion()
                + " Has Been Enabled!");
        new HomeSpawnFileWatcher(this);
        PluginManager pm = getServer().getPluginManager();
        HomeSpawnListener HSListener = new HomeSpawnListener(this);
        pm.registerEvents(HSListener, this);
    }

    private void Disable() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            HSConfig.unloadPlayerData(p.getUniqueId());
        }
        HandlerList.unregisterAll(this);
        logger.info("Plugin Has Been Disabled!");
    }

    public HomeSpawnPlayer getPlayer(UUID uuid) {
        //Loop through all players and check if any of them are the one we need
        for (HomeSpawnPlayer p : players) {
            if (p.getUUID() == uuid) {
                return p;
            }
        }
        //if not then we make a new one and save it for later
        HomeSpawnPlayer p = new HomeSpawnPlayer(this, uuid);
        players.add(p);
        return p;
    }

    void spawnNew(Player player) {
        //is run when a player joins for the first time
        //if there is a location set for spawn new
        if (HSConfig.spawn.contains("spawnnew")) {
            //get the location and teleport the player
            Location spawnNew = (Location) HSConfig.spawn.get("spawnnew");
            player.teleport(spawnNew);
            logger.info("Player " + player.getName()
                    + " was sent To the new spawn");
        } else {
            logger.info(HSConfig.getMessage("Spawn.NewPlayerNoNewSpawn"));
        }
    }

    public void help(CommandSender sender) {
        //Sends a customized help message based on what commands the player can use
        if (sender != null) {
            HashMap<HomeSpawnPermissions.perm, Integer> perms = HSPermissions.getPlayerPermissions(UUID.randomUUID());
            boolean isPlayer = false;
            if (sender instanceof Player) {
                Player p = (Player) sender;
                perms = HSPermissions.getPlayerPermissions(p.getUniqueId());
                isPlayer = true;
            }
            sender.sendMessage(ChatColor.GOLD + "---------------"
                    + ChatColor.RED + "Homespawn" + ChatColor.GOLD
                    + "---------------");
            if (isPlayer && perms.get(HomeSpawnPermissions.perm.customHomes) == 1 &&
                    perms.get(HomeSpawnPermissions.perm.homes) > 0) {
                sender.sendMessage(ChatColor.RED + "/home [name]:" + ChatColor.GOLD
                        + " Sends You To The Home Specified");
                sender.sendMessage(ChatColor.RED + "/sethome [name]:"
                        + ChatColor.GOLD
                        + " Sets Your Home At Your Current Location");
                if (perms.get(HomeSpawnPermissions.perm.playerStats) == 1) {
                    sender.sendMessage(ChatColor.RED + "/delhome [name] [player]:"
                            + ChatColor.GOLD + " Removes The Specified Home, If a player is provided it will attempt to delete that players home");
                } else {
                    sender.sendMessage(ChatColor.RED + "/delhome [name]:"
                            + ChatColor.GOLD + " Removes The Specified Home");
                }
            } else if (isPlayer && perms.get(HomeSpawnPermissions.perm.homes) > 0) {
                sender.sendMessage(ChatColor.RED + "/home:" + ChatColor.GOLD
                        + " Sends You To Your Home");
                sender.sendMessage(ChatColor.RED + "/sethome:"
                        + ChatColor.GOLD
                        + " Sets YourHome At Your Current Location");
                if (perms.get(HomeSpawnPermissions.perm.playerStats) == 1) {
                    sender.sendMessage(ChatColor.RED + "/delhome [name] [player]:"
                            + ChatColor.GOLD + " Removes The Specified Home, If a player is provided it will attempt to delete that players home");
                } else {
                    sender.sendMessage(ChatColor.RED + "/delhome:"
                            + ChatColor.GOLD + " Removes Your Home");
                }
            }
            if (isPlayer && perms.get(HomeSpawnPermissions.perm.spawn) == 1) {
                sender.sendMessage(ChatColor.RED + "/spawn:" + ChatColor.GOLD
                        + " Sends You To Spawn");
            }
            if (!getServer().getOnlineMode()) {
                sender.sendMessage(ChatColor.RED + "/homepassword help:"
                        + ChatColor.GOLD
                        + " Displays The Home Transfer Commands");
            }
            if (isPlayer && perms.get(HomeSpawnPermissions.perm.setSpawn) == 1) {
                sender.sendMessage(ChatColor.RED + "/setspawn:"
                        + ChatColor.GOLD + " Sets The Server Spawn");
                sender.sendMessage(ChatColor.RED + "/setspawn new:"
                        + ChatColor.GOLD
                        + " All New Players Will Be Sent To This Spawn");
                sender.sendMessage(ChatColor.RED + "/delspawn:"
                        + ChatColor.GOLD + " Removes The Server Spawn");
            }
            if (!isPlayer) {
                sender.sendMessage(ChatColor.RED + "/delspawn:"
                        + ChatColor.GOLD + " Removes The Server Spawn");
            }
            sender.sendMessage(ChatColor.RED + "/homespawn:"
                    + ChatColor.GOLD + " Displays Plugin Information");
            if (!isPlayer || perms.get(HomeSpawnPermissions.perm.reload).equals(1)) {
                sender.sendMessage(ChatColor.RED + "/homespawn reload:"
                        + ChatColor.GOLD + " Reloads The Plugin Configs");
            }
            if (!isPlayer || perms.get(HomeSpawnPermissions.perm.updateNotify).equals(1)) {
                sender.sendMessage(ChatColor.RED + "/homespawn update:"
                        + ChatColor.GOLD + " Installs updates if available");
            }
            sender.sendMessage(ChatColor.GOLD
                    + "-----------------------------------------");
        }
    }

    @SuppressWarnings("deprecation")
    private void teleportDelay() {
        //Handles the delay of teleporting
        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        scheduler.scheduleSyncRepeatingTask(this, () -> {
            //Don't run this code if there is no one waiting to teleport
            if (!HomeSpawnTimeLeft.isEmpty()) {
                HashMap<Player, Integer> timeLeft = HomeSpawnTimeLeft;
                Iterator<Player> it = timeLeft.keySet().iterator();
                try {
                    //Iterate over all of the waiting players using an iterator
                    while (it.hasNext()) {
                        Player p = it.next();
                        //if the location is null the teleport has probably been canceled
                        //so we will remove the player from the waiting list
                        if (HomeSpawnHomes.get(p) == null) {
                            it.remove();
                            HomeSpawnHomes.remove(p);
                        }
                        if (HomeSpawnTimeLeft.isEmpty()) {
                            return;
                        }
                        Iterator<Integer> iterator = timeLeft.values().iterator();
                        //noinspection WhileLoopReplaceableByForEach
                        while (iterator.hasNext()) {
                            //iterate over the list of time left for each player and reduce it by 1
                            Integer time = iterator.next();
                            int NewTime = time - 1;
                            //If the time left > 0 then we save the new time left and wait
                            //otherwise we teleport the player then remove them from the list
                            if (NewTime > 0) {
                                HomeSpawnTimeLeft.put(p, NewTime);
                            } else {
                                //Generates a fake home object to use the home objects advanced teleport code
                                Home h = HomeSpawnHomes.get(p);
                                h.teleportPlayerNow(p);
                                HomeSpawnHomes.remove(p);
                                HomeSpawnTimeLeft.remove(p);
                            }
                        }
                    }
                } catch (ConcurrentModificationException ignored) {
                }
            }
        }, 0, 20);
        scheduler.scheduleSyncRepeatingTask(this, () -> players.removeIf(p -> !Bukkit.getOfflinePlayer(p.getUUID()).isOnline()), 0, 60 * 20);
    }

    public void debug(String s) {
        if (getConfig().getBoolean("Debug")) {
            logger.info("Homespawn Debug: " + s);
        }
    }
}
