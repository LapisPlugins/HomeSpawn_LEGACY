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

import net.lapismc.HomeSpawn.Metrics.Graph;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HomeSpawn extends JavaPlugin {

    public final Logger logger = getLogger();
    final HashMap<Player, Location> HomeSpawnLocations = new HashMap<>();
    final HashMap<Player, Integer> HomeSpawnTimeLeft = new HashMap<>();
    public LapisUpdater lapisUpdater;
    public HomeSpawnCommand HSCommand;
    public HomeSpawnComponents HSComponents;
    public HomeSpawnPermissions HSPermissions;
    public HomeSpawnListener HSListener;
    public HomeSpawnConfiguration HSConfig;

    @Override
    public void onEnable() {
        HSConfig = new HomeSpawnConfiguration(this);
        Enable();
        Update();
        HSPermissions = new HomeSpawnPermissions(this);
        Commands();
        CommandDelay();
        Metrics();
    }

    private void Metrics() {
        try {
            Metrics metrics = new Metrics(this);
            Graph averageHomesGraph = metrics.createGraph("Average Number Of Homes");
            int homes = 0;
            File playerData = new File(this.getDataFolder() + File.separator + "PlayerData");
            int files = playerData.listFiles().length - 1;
            for (File f : playerData.listFiles()) {
                if (f.getName() != "Passwords.yml" && !f.isDirectory()) {
                    YamlConfiguration yaml = YamlConfiguration.loadConfiguration(f);
                    homes = homes + yaml.getStringList("Homes.List").size();
                }
            }
            int average;
            if (files != 0) {
                average = homes % files == 0 ? homes / files : homes / files + 1;
            } else {
                average = 0;
            }
            averageHomesGraph.addPlotter(new Metrics.Plotter(average + "") {
                @Override
                public int getValue() {
                    return 1;
                }
            });
            metrics.start();
            debug("Send stats to metrics");
        } catch (IOException e) {
            this.logger.log(Level.SEVERE, "An error has occurred while trying to" +
                    " start HomeSpawn metrics");
            this.logger.log(Level.SEVERE, "The error follows, Please report it to dart2112");
            e.printStackTrace();
        }
    }

    private void Update() {
        final HomeSpawn p = this;
        Bukkit.getScheduler().runTaskAsynchronously(this, new Runnable() {
            @Override
            public void run() {
                lapisUpdater = new LapisUpdater(p, "Homespawn.jar", "Dart2112", "HomeSpawn", "master");
                if (lapisUpdater.checkUpdate("HomeSpawn")) {
                    if (getConfig().getBoolean("UpdateNotification") && !getConfig()
                            .getBoolean("DownloadUpdates")) {
                        logger.info("An update for HomeSpawn is available and can be" +
                                " downloaded and installed by running /homespawn update");
                    } else if (getConfig().getBoolean("DownloadUpdates")) {
                        lapisUpdater.downloadUpdate("HomeSpawn");
                        logger.info("Downloading Homespawn update, it will be installed " +
                                "on next restart!");
                    }
                } else {
                    if (getConfig().getBoolean("UpdateNotification")) {
                        logger.info("No Update Available");
                    }
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
        PluginManager pm = getServer().getPluginManager();
        HSListener = new HomeSpawnListener(this);
        pm.registerEvents(HSListener, this);
    }

    private void Disable() {
        HSConfig.saveLogs();
        for (Player p : Bukkit.getOnlinePlayers()) {
            HSConfig.unloadPlayerData(p.getUniqueId());
        }
        HandlerList.unregisterAll(this);
        logger.info("Plugin Has Been Disabled!");
    }

    public void spawnNew(Player player) {
        if (HSConfig.spawn.contains("spawnnew")) {
            Location spawnnew = (Location) HSConfig.spawn.get("spawnnew");
            player.teleport(spawnnew);
            logger.info("Player " + player.getName()
                    + " was sent To the new spawn");
        } else {
            logger.info(HSConfig.getMessage("Spawn.NewPlayerNoNewSpawn"));
        }
    }

    public void help(Player player) {
        if (player != null) {
            HashMap<HomeSpawnPermissions.perm, Integer> perms = HSPermissions.getPlayerPermissions(player.getUniqueId());
            player.sendMessage(ChatColor.GOLD + "---------------"
                    + ChatColor.RED + "Homespawn" + ChatColor.GOLD
                    + "---------------");
            if (perms.get(HomeSpawnPermissions.perm.customHomes) == 1 &&
                    perms.get(HomeSpawnPermissions.perm.homes) > 0) {
                player.sendMessage(ChatColor.RED + "/home [name]:" + ChatColor.GOLD
                        + " Sends You To The HomeSpawnHome Specified");
                player.sendMessage(ChatColor.RED + "/sethome [name]:"
                        + ChatColor.GOLD
                        + " Sets Your HomeSpawnHome At Your Current Location");
                player.sendMessage(ChatColor.RED + "/delhome [name]:"
                        + ChatColor.GOLD + " Removes The Specified HomeSpawnHome");
            } else if (perms.get(HomeSpawnPermissions.perm.homes) > 0) {
                player.sendMessage(ChatColor.RED + "/home:" + ChatColor.GOLD
                        + " Sends You To Your HomeSpawnHome");
                player.sendMessage(ChatColor.RED + "/sethome:"
                        + ChatColor.GOLD
                        + " Sets Your HomeSpawnHome At Your Current Location");
                player.sendMessage(ChatColor.RED + "/delhome:"
                        + ChatColor.GOLD + " Removes Your HomeSpawnHome");
            }
            if (perms.get(HomeSpawnPermissions.perm.spawn) == 1) {
                player.sendMessage(ChatColor.RED + "/spawn:" + ChatColor.GOLD
                        + " Sends You To HomeSpawnSpawn");
            }
            if (!getServer().getOnlineMode()) {
                player.sendMessage(ChatColor.RED + "/homepassword help:"
                        + ChatColor.GOLD
                        + " Displays The HomeSpawnHome Transfer Commands");
            }
            if (perms.get(HomeSpawnPermissions.perm.setSpawn) == 1) {
                player.sendMessage(ChatColor.RED + "/setspawn:"
                        + ChatColor.GOLD + " Sets The Server HomeSpawnSpawn");
                player.sendMessage(ChatColor.RED + "/setspawn new:"
                        + ChatColor.GOLD
                        + " All New Players Will Be Sent To This HomeSpawnSpawn");
                player.sendMessage(ChatColor.RED + "/delspawn:"
                        + ChatColor.GOLD + " Removes The Server HomeSpawnSpawn");
            }
            player.sendMessage(ChatColor.RED + "/homespawn:"
                    + ChatColor.GOLD + " Displays Plugin Information");
            if (perms.get(HomeSpawnPermissions.perm.reload).equals(1)) {
                player.sendMessage(ChatColor.RED + "/homespawn reload:"
                        + ChatColor.GOLD + " Reloads The Plugin HomeSpawnConfigs");
            }
            if (perms.get(HomeSpawnPermissions.perm.updateNotify).equals(1)) {
                player.sendMessage(ChatColor.RED + "/homespawn update:"
                        + ChatColor.GOLD + " Installs updates if available");
            }
            player.sendMessage(ChatColor.GOLD
                    + "-----------------------------------------");
        } else {
            return;
        }
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String alias,
                                      String[] args) {
        if (command.getName().equalsIgnoreCase("home") || command.getName().equalsIgnoreCase("delhome")) {
            List<String> l = new ArrayList<>();
            Player p = (Player) sender;
            YamlConfiguration playerData = HSConfig.getPlayerData(p.getUniqueId());
            l.addAll(playerData.getStringList("Homes.list"));
            debug("Tab Completed for " + sender.getName());
            return l;
        }
        return null;
    }

    private void CommandDelay() {
        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                if (!HomeSpawnTimeLeft.isEmpty()) {
                    HashMap<Player, Integer> timeLeft = (HashMap<Player, Integer>) HomeSpawnTimeLeft.clone();
                    for (Player p : timeLeft.keySet()) {
                        if (HomeSpawnLocations.get(p) == null) {
                            HomeSpawnTimeLeft.remove(p);
                            HomeSpawnLocations.remove(p);
                        }
                        if (HomeSpawnTimeLeft.isEmpty()) {
                            return;
                        }
                        Collection<Integer> values = timeLeft.values();
                        for (int Time : values) {
                            int NewTime = Time - 1;
                            if (NewTime > 0) {
                                HomeSpawnTimeLeft.put(p, NewTime);
                            } else if (NewTime <= 0) {
                                Location Tele = HomeSpawnLocations.get(p);
                                if (!(Tele == null)) {
                                    if (!Tele.getChunk().isLoaded()) {
                                        Tele.getChunk().load();
                                    }
                                    p.teleport(Tele);
                                    debug("Teleported " + p.getName());
                                    p.sendMessage(ChatColor.GOLD
                                            + "Teleporting...");
                                    HomeSpawnTimeLeft.remove(p);
                                    HomeSpawnLocations.remove(p);
                                } else {
                                    HomeSpawnTimeLeft.remove(p);
                                    HomeSpawnLocations.remove(p);
                                }
                            }
                        }
                    }
                }
            }
        }, 0, 20);
    }

    public void debug(String s) {
        if (getConfig().getBoolean("Debug")) {
            logger.info("Homespawn Debug: " + s);
        }
    }

    private void Commands() {
        HSComponents = new HomeSpawnComponents();
        HSComponents.init(this);
    }
}
