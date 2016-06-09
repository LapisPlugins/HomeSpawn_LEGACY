package net.lapismc.HomeSpawn;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
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
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.permissions.Permission;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class HomeSpawnListener implements Listener {

    private final List<Player> Players = new ArrayList<>();
    private YamlConfiguration getMessages;
    private LapisUpdater updater;
    private HomeSpawn plugin;

    public HomeSpawnListener(HomeSpawn plugin) {
        this.plugin = plugin;
    }

    public void setMessages() {
        plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, new Runnable() {
            public void run() {
                updater = new LapisUpdater(plugin);
                getMessages = plugin.messages;
                HomeSpawnCommand.getMessages = plugin.messages;
                HomeSpawnCommand.getSpawn = plugin.spawn;
            }
        }, 20);
    }

    @EventHandler(priority = EventPriority.HIGH)
    void PlayerJoinEvent(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        File file = new File(plugin.getDataFolder() + File.separator
                + "PlayerData" + File.separator
                + player.getUniqueId() + ".yml");
        FileConfiguration getHomes = YamlConfiguration.loadConfiguration(file);
        File file2 = new File(plugin.getDataFolder().getAbsolutePath()
                + File.separator + "PlayerData" + File.separator
                + "PlayerNames" + File.separator + player.getName() + ".yml");
        FileConfiguration getName = YamlConfiguration.loadConfiguration(file2);
        if (file == null || file2 == null) {
            plugin.logger.severe("Player " + player.getName()
                    + "'s Data File Is Null!");
            return;
        }
        if (!file2.exists()) {
            try {
                file2.createNewFile();
                getName.createSection("Name");
                getName.createSection("UUID");
                getName.save(file2);
                getName.set("Name", player.getName());
                getName.set("UUID", player.getUniqueId().toString());
                getName.save(file2);
                if (plugin.getConfig().getBoolean("CommandBook")) {
                    PlayerInventory pi = player.getInventory();
                    InstructionBook book = new InstructionBook(plugin);
                    ItemStack commandBook = book.getBook();
                    pi.addItem(commandBook);
                }
            } catch (IOException e) {
                e.printStackTrace();
                plugin.logger
                        .severe("[HomeSpawn] Player Name Data File Creation Failed!");
                return;
            }
        }
        if (!file.exists()) {
            try {
                file.createNewFile();
                getHomes.createSection("name");
                getHomes.createSection("HasHome");
                getHomes.createSection(player.getUniqueId() + ".Numb");
                getHomes.save(file);
                getHomes.set("name", player.getUniqueId().toString());
                getHomes.set("HasHome", "No");
                getHomes.set(player.getUniqueId() + ".Numb", 0);
                getHomes.save(file);
                plugin.spawnNew(player);
                plugin.loadPlayerData();
                plugin.loadName();
            } catch (IOException e) {
                e.printStackTrace();
                plugin.logger
                        .severe("[HomeSpawn] Player Data File Creation Failed!");
                return;
            }
        }
        int priority = -1;
        for (Permission p : plugin.Permissions.keySet()) {
            if (player.hasPermission(p)) {
                if (priority == -1 || plugin.Permissions.get(p).get("priority") > priority) {
                    plugin.PlayerPermission.put(player.getUniqueId(), p);
                    priority = plugin.Permissions.get(p).get("priority");
                }
            }
        }
        if (!plugin.PlayerPermission.containsKey(player.getUniqueId())) {
            Permission nulled = new Permission("homespawn.null");
            plugin.PlayerPermission.put(player.getUniqueId(), nulled);
        }
        plugin.logger.info("Player " + player.getName() + " has been given the permission " +
                plugin.PlayerPermission.get(player.getUniqueId()).getName());
        HashMap<String, Integer> perms = plugin.Permissions.get(plugin.PlayerPermission.get(player.getUniqueId()));
        if (perms.get("updateNotify") == 1) {
            if (!plugin.getConfig().getBoolean("DownloadUpdates") && updater.checkUpdate("main")) {
                player.sendMessage(ChatColor.DARK_GRAY
                        + "[" + ChatColor.AQUA + "HomeSpawn" + ChatColor.DARK_GRAY
                        + "]" + ChatColor.GOLD + " An update is available! check the changes" +
                        " at https://www.spigotmc.org/resources/homespawn.14108/updates," +
                        " or run \"/homespawn update\" to install it");
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void OnPlayerMove(PlayerMoveEvent e) {
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
                        p.sendMessage(ChatColor.GOLD
                                + "Teleport Canceled Because You Moved!");
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
                        p.sendMessage(ChatColor.GOLD
                                + "Teleport Canceled Because You Were Hit!");
                    } else if (arrow.getShooter() instanceof Skeleton) {
                        Players.add(p);
                        e.setCancelled(true);
                    }
                }
                if (Hitter instanceof Wolf) {
                    Wolf wolf = (Wolf) Hitter;
                    if (wolf.isTamed()) {
                        plugin.HomeSpawnLocations.put(p, null);
                        p.sendMessage(ChatColor.GOLD
                                + "Teleport Canceled Because You Were Hit!");
                    } else {
                        Players.add(p);
                        e.setCancelled(true);
                    }
                }
                if (Hitter instanceof Player) {
                    plugin.HomeSpawnLocations.put(p, null);
                    plugin.HomeSpawnTimeLeft.remove(p);
                    p.sendMessage(ChatColor.GOLD
                            + "Teleport Canceled Because You Were Hit!");
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
        if (e.getInventory().equals(plugin.HomesListInvs.get(p))) {
            String name = e.getCurrentItem().getItemMeta().getDisplayName();
            String name1 = ChatColor.stripColor(name);
            File file2 = new File(plugin.getDataFolder().getAbsolutePath()
                    + File.separator + "PlayerData" + File.separator
                    + "PlayerNames" + File.separator + p.getName() + ".yml");
            FileConfiguration getName = YamlConfiguration
                    .loadConfiguration(file2);
            File Homes = new File(plugin.getDataFolder().getAbsolutePath()
                    + File.separator + "PlayerData" + File.separator
                    + getName.getString("UUID") + ".yml");
            YamlConfiguration getHomes = YamlConfiguration
                    .loadConfiguration(Homes);
            if (name1.equalsIgnoreCase("Home")) {
                if (getHomes.getString("HasHome").equalsIgnoreCase("yes")) {
                    int x = getHomes.getInt(p.getUniqueId() + ".x");
                    int y = getHomes.getInt(p.getUniqueId() + ".y");
                    int z = getHomes.getInt(p.getUniqueId() + ".z");
                    float yaw = getHomes.getInt(p.getUniqueId()
                            + ".Yaw");
                    float pitch = getHomes.getInt(p.getUniqueId()
                            + ".Pitch");
                    String cworld = getHomes.getString(p.getUniqueId() + ".world");
                    World world = plugin.getServer().getWorld(cworld);
                    Location home = new Location(world, x, y, z, yaw, pitch);
                    home.add(0.5, 0, 0.5);
                    TeleportPlayer(p, home);
                }
            } else {
                if (getHomes.getString(name1 + ".HasHome").equalsIgnoreCase(
                        "yes")) {
                    int x = getHomes.getInt(name1 + ".x");
                    int y = getHomes.getInt(name1 + ".y");
                    int z = getHomes.getInt(name1 + ".z");
                    float yaw = getHomes.getInt(name1 + ".Yaw");
                    float pitch = getHomes.getInt(name1 + ".Pitch");
                    String cworld = getHomes.getString(name1 + ".world");
                    World world = plugin.getServer().getWorld(cworld);
                    Location home2 = new Location(world, x, y, z, yaw, pitch);
                    home2.add(0.5, 0, 0.5);
                    TeleportPlayer(p, home2);
                }
            }
            Inventory inv = plugin.HomesListInvs.get(p);
            inv.clear();
            plugin.HomesListInvs.put(p, inv);
        } else {
            return;
        }

    }

    @EventHandler
    public void onInvExit(InventoryCloseEvent e) {
        if (!(e.getPlayer() == null && e.getInventory() == null)) {
            Player p = (Player) e.getPlayer();
            if (plugin.HomesListInvs.containsKey(p) && Objects.equals(e.getInventory().getName(), plugin.HomesListInvs.get(p).getName())) {
                Inventory inv = plugin.HomesListInvs.get(p);
                inv.clear();
                plugin.HomesListInvs.put(p, inv);
            }
        }
    }

    private void TeleportPlayer(Player p, Location l) {
        HashMap<String, Integer> perms = plugin.Permissions.get(plugin.PlayerPermission.get(p.getUniqueId()));
        if (perms.get("TPD") == 0) {
            p.teleport(l);
            p.sendMessage(ChatColor.GOLD + getMessages.getString("Home.SentHome"));
        } else {
            String waitraw = ChatColor.GOLD + getMessages.getString("Wait");
            String Wait = waitraw.replace("{time}", ChatColor.RED
                    + perms.get("TPD").toString()
                    + ChatColor.GOLD);
            p.sendMessage(Wait);
            plugin.HomeSpawnLocations.put(p, l);
            plugin.HomeSpawnTimeLeft.put(p, perms.get("TPD"));
        }

    }
}
