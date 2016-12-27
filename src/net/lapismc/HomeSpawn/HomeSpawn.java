package net.lapismc.HomeSpawn;

import net.lapismc.HomeSpawn.Metrics.Graph;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HomeSpawn extends JavaPlugin {

    public final Logger logger = getLogger();
    public final HashMap<String, UUID> PlayertoUUID = new HashMap<>();
    final HashMap<Player, Location> HomeSpawnLocations = new HashMap<>();
    final HashMap<Player, Integer> HomeSpawnTimeLeft = new HashMap<>();
    final HashMap<Player, Inventory> HomesListInvs = new HashMap<>();
    public HomeSpawn plugin;
    public LapisUpdater updater;
    public HomeSpawnPermissions HSPermissions;
    public HomeSpawnListener HSListener;
    public HomeSpawnConfiguration HSConfig;

    @Override
    public void onEnable() {
        Enable();
        Update();
        HSPermissions = new HomeSpawnPermissions(this);
        HSPermissions.init();
        Commands();
        CommandDelay();
        Metrics();
    }

    private void Metrics() {
        try {
            Metrics metrics = new Metrics(this);
            Graph averageHomesGraph = metrics.createGraph("Average Number Of Homes");
            int homes = 0;
            int files = HSConfig.HomeConfigs.size();
            for (YamlConfiguration yaml : HSConfig.HomeConfigs.values()) {
                homes = homes + yaml.getInt(yaml.getString("name") + ".Numb");
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
                updater = new LapisUpdater(p, "Homespawn.jar", "Dart2112", "HomeSpawn", "master");
                String ID = getConfig().getBoolean("BetaVersions") == true ? "beta" : "stable";
                if (updater.checkUpdate(ID)) {
                    if (getConfig().getBoolean("UpdateNotification") && !getConfig()
                            .getBoolean("DownloadUpdates")) {
                        logger.info("An update for HomeSpawn is available and can be" +
                                " downloaded and installed by running /homespawn update");
                    } else if (getConfig().getBoolean("DownloadUpdates")) {
                        updater.downloadUpdate(ID);
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
        logger.info("Plugin Has Been Disabled!");
        HandlerList.unregisterAll();
    }

    public void spawnNew(Player player) {
        if (HSConfig.spawn.get("spawnnew.SpawnSet") != null
                && HSConfig.spawn.getString("spawnnew.SpawnSet").equalsIgnoreCase(
                "yes")) {
            int x = HSConfig.spawn.getInt("spawnnew.X");
            int y = HSConfig.spawn.getInt("spawnnew.Y");
            int z = HSConfig.spawn.getInt("spawnnew.Z");
            float yaw = HSConfig.spawn.getInt("spawnnew.Yaw");
            float pitch = HSConfig.spawn.getInt("spawnnew.Pitch");
            String cworld = HSConfig.spawn.getString("spawnnew.World");
            World world = getServer().getWorld(cworld);
            Location spawnnew = new Location(world, x, y, z, yaw, pitch);
            spawnnew.add(0.5, 0, 0.5);
            player.teleport(spawnnew);
            logger.info("Player " + player.getName()
                    + " Was Sent To New Spawn");
        } else {
            logger.info("There Is No New Spawn Set And Therefore The Player Wasn't Sent To The New Spawn");
        }
    }


    public void help(Player player) {
        if (player != null) {
            player.sendMessage(ChatColor.GOLD + "---------------"
                    + ChatColor.RED + "Homespawn" + ChatColor.GOLD
                    + "---------------");
            if (HSPermissions.Permissions.get(HSPermissions.PlayerPermission.get(player.getUniqueId())).get("cHomes") == 1 &&
                    HSPermissions.Permissions.get(HSPermissions.PlayerPermission.get(player.getUniqueId())).get("homes") > 0) {
                player.sendMessage(ChatColor.RED + "/home [name]:" + ChatColor.GOLD
                        + " Sends You To The Home Specified");
                player.sendMessage(ChatColor.RED + "/sethome [name]:"
                        + ChatColor.GOLD
                        + " Sets Your Home At Your Current Location");
                player.sendMessage(ChatColor.RED + "/delhome [name]:"
                        + ChatColor.GOLD + " Removes The Specified Home");
            } else if (HSPermissions.Permissions.get(HSPermissions.PlayerPermission.get
                    (player.getUniqueId())).get("homes") > 0) {
                player.sendMessage(ChatColor.RED + "/home:" + ChatColor.GOLD
                        + " Sends You To Your Home");
                player.sendMessage(ChatColor.RED + "/sethome:"
                        + ChatColor.GOLD
                        + " Sets Your Home At Your Current Location");
                player.sendMessage(ChatColor.RED + "/delhome:"
                        + ChatColor.GOLD + " Removes Your Home");
            }
            if (HSPermissions.Permissions.get(HSPermissions.PlayerPermission.get
                    (player.getUniqueId())).get("spawn") == 1) {
                player.sendMessage(ChatColor.RED + "/spawn:" + ChatColor.GOLD
                        + " Sends You To Spawn");
            }
            if (!getServer().getOnlineMode()) {
                player.sendMessage(ChatColor.RED + "/homepassword help:"
                        + ChatColor.GOLD
                        + " Displays The Home Transfer Commands");
            }
            if (HSPermissions.Permissions.get(HSPermissions.PlayerPermission.get(player.getUniqueId())).get("sSpawn").equals(1)) {
                player.sendMessage(ChatColor.RED + "/setspawn:"
                        + ChatColor.GOLD + " Sets The Server Spawn");
                player.sendMessage(ChatColor.RED + "/setspawn new:"
                        + ChatColor.GOLD
                        + " All New Players Will Be Sent To This Spawn");
                player.sendMessage(ChatColor.RED + "/delspawn:"
                        + ChatColor.GOLD + " Removes The Server Spawn");
            }
            player.sendMessage(ChatColor.RED + "/homespawn:"
                    + ChatColor.GOLD + " Displays Plugin Information");
            if (HSPermissions.Permissions.get(HSPermissions.PlayerPermission.get(player.getUniqueId())).get("reload").equals(1)) {
                player.sendMessage(ChatColor.RED + "/homespawn reload:"
                        + ChatColor.GOLD + " Reloads The Plugin Configs");
            }
            if (HSPermissions.Permissions.get(HSPermissions.PlayerPermission.get(player.getUniqueId())).get("updateNotify").equals(1)) {
                player.sendMessage(ChatColor.RED + "/homespawn update (beta/stable):"
                        + ChatColor.GOLD + " Installs updates if available, you can also choose beta or stable.");
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
            if (args.length == 1) {
                Player p = (Player) sender;
                YamlConfiguration pd = HSConfig.HomeConfigs.get(p.getUniqueId());
                l.addAll(pd.getStringList(p.getUniqueId() + ".list"));
            }
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
                    for (Player p : HomeSpawnTimeLeft.keySet()) {
                        if (HomeSpawnLocations.get(p) == null) {
                            HomeSpawnTimeLeft.remove(p);
                            HomeSpawnLocations.remove(p);
                        }
                        if (HomeSpawnTimeLeft.isEmpty()) {
                            return;
                        }
                        Collection<Integer> values = HomeSpawnTimeLeft.values();
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
        HomeSpawnComponents hsc = new HomeSpawnComponents();
        hsc.init(this);
    }
}
