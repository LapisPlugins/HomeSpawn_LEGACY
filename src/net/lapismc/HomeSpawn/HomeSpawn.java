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
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.File;
import java.util.*;
import java.util.logging.Logger;

public class HomeSpawn extends JavaPlugin {

    final Logger logger = getLogger();
    final HashMap<Player, Location> HomeSpawnLocations = new HashMap<>();
    final HashMap<Player, Integer> HomeSpawnTimeLeft = new HashMap<>();
    public LapisUpdater lapisUpdater;
    public HomeSpawnPermissions HSPermissions;
    public HomeSpawnConfiguration HSConfig;
    public String PrimaryColor = ChatColor.GOLD.toString();
    public String SecondaryColor = ChatColor.RED.toString();
    HomeSpawnCommand HSCommand;

    @Override
    public void onEnable() {
        HSConfig = new HomeSpawnConfiguration(this);
        Enable();
        Update();
        HSPermissions = new HomeSpawnPermissions(this);
        new HomeSpawnCommand(this);
        CommandDelay();
        Metrics();
    }

    @SuppressWarnings("ConstantConditions")
    private void Metrics() {
        Metrics metrics = new Metrics(this);
        int homes = 0;
        File playerData = new File(this.getDataFolder() + File.separator + "PlayerData");
        int files = playerData.listFiles().length - 1;
        for (File f : playerData.listFiles()) {
            if (!f.getName().equals("Passwords.yml") && !f.isDirectory()) {
                YamlConfiguration yaml = YamlConfiguration.loadConfiguration(f);
                homes += yaml.getStringList("Homes.List").size();
            }
        }
        Integer average;
        if (files != 0) {
            average = homes % files == 0 ? homes / files : homes / files + 1;
        } else {
            average = 0;
        }
        metrics.addCustomChart(new Metrics.SimplePie("average_number_of_homes") {
            @Override
            public String getValue() {
                return average.toString();
            }
        });
        debug("Send stats to metrics");
    }

    private void Update() {
        final HomeSpawn p = this;
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            lapisUpdater = new LapisUpdater(p, "Homespawn", "Dart2112", "HomeSpawn", "master");
            if (lapisUpdater.checkUpdate()) {
                if (getConfig().getBoolean("UpdateNotification") && !getConfig()
                        .getBoolean("DownloadUpdates")) {
                    logger.info("An update for HomeSpawn is available and can be" +
                            " downloaded and installed by running /homespawn update");
                } else if (getConfig().getBoolean("DownloadUpdates")) {
                    lapisUpdater.downloadUpdate();
                    logger.info("Downloading Homespawn update, it will be installed " +
                            "on next restart!");
                }
            } else {
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

    void spawnNew(Player player) {
        if (HSConfig.spawn.contains("spawnnew")) {
            Location spawnnew = (Location) HSConfig.spawn.get("spawnnew");
            player.teleport(spawnnew);
            logger.info("Player " + player.getName()
                    + " was sent To the new spawn");
        } else {
            logger.info(HSConfig.getMessage("Spawn.NewPlayerNoNewSpawn"));
        }
    }

    public void help(CommandSender sender) {
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
                sender.sendMessage(ChatColor.RED + "/delhome [name]:"
                        + ChatColor.GOLD + " Removes The Specified Home");
            } else if (isPlayer && perms.get(HomeSpawnPermissions.perm.homes) > 0) {
                sender.sendMessage(ChatColor.RED + "/home:" + ChatColor.GOLD
                        + " Sends You To Your Home");
                sender.sendMessage(ChatColor.RED + "/sethome:"
                        + ChatColor.GOLD
                        + " Sets YourHome At Your Current Location");
                sender.sendMessage(ChatColor.RED + "/delhome:"
                        + ChatColor.GOLD + " Removes Your Home");
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

    @SuppressWarnings("deprecation")
    private void CommandDelay() {
        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        scheduler.scheduleSyncRepeatingTask(this, () -> {
            if (!HomeSpawnTimeLeft.isEmpty()) {
                HashMap<Player, Integer> timeLeft = HomeSpawnTimeLeft;
                Iterator<Player> it = timeLeft.keySet().iterator();
                try {
                    while (it.hasNext()) {
                        Player p = it.next();
                        if (HomeSpawnLocations.get(p) == null) {
                            it.remove();
                            HomeSpawnLocations.remove(p);
                        }
                        if (HomeSpawnTimeLeft.isEmpty()) {
                            return;
                        }
                        Iterator<Integer> iterator = timeLeft.values().iterator();
                        //noinspection WhileLoopReplaceableByForEach
                        while (iterator.hasNext()) {
                            Integer time = iterator.next();
                            int NewTime = time - 1;
                            if (NewTime > 0) {
                                HomeSpawnTimeLeft.put(p, NewTime);
                            } else {
                                Location Tele = HomeSpawnLocations.get(p);
                                if (!(Tele == null)) {
                                    if (!Tele.getChunk().isLoaded()) {
                                        Tele.getChunk().load();
                                    }
                                    if (p.isInsideVehicle()) {
                                        if (p.getVehicle() instanceof Horse) {
                                            Horse horse = (Horse) p.getVehicle();
                                            horse.eject();
                                            horse.teleport(Tele);
                                            p.teleport(Tele);
                                            horse.setPassenger(p);
                                        }
                                    } else {
                                        p.teleport(Tele);
                                    }
                                    p.sendMessage(HSConfig.getColoredMessage("Home.SentHome"));

                                    debug("Teleported " + p.getName());
                                    p.sendMessage(ChatColor.GOLD
                                            + "Teleporting...");
                                    it.remove();
                                    HomeSpawnLocations.remove(p);
                                } else {
                                    it.remove();
                                    HomeSpawnLocations.remove(p);
                                }
                            }
                        }
                    }
                } catch (ConcurrentModificationException ignored) {
                }
            }
        }, 0, 20);
    }

    void debug(String s) {
        if (getConfig().getBoolean("Debug")) {
            logger.info("Homespawn Debug: " + s);
        }
    }
}
