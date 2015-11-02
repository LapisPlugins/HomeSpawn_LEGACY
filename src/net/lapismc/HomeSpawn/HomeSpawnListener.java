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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HomeSpawnListener implements Listener {

    public YamlConfiguration getMessages = null;

    List<Player> Players = new ArrayList<Player>();

    private HomeSpawn plugin;
    private HomeSpawnCommand cmd;

    public HomeSpawnListener(HomeSpawn plugin) {
        this.plugin = plugin;
    }

    public HomeSpawnListener(HomeSpawnCommand cmd) {
        this.cmd = cmd;
    }

    public void setMessages() {
        plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable() {
            public void run() {
                getMessages = plugin.messages;
                HomeSpawnCommand.getMessages = plugin.messages;
                HomeSpawnCommand.getSpawn = plugin.spawn;
            }
        }, 1 * 20);
    }

    @EventHandler(priority = EventPriority.HIGH)
    void PlayerJoinEvent(final PlayerJoinEvent event) {
        Player player = event.getPlayer();
        File file = new File(plugin.getDataFolder() + File.separator
                + "PlayerData" + File.separator
                + player.getUniqueId().toString() + ".yml");
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
                    ItemStack commandBook = InstructionBook.getBook();
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
                getHomes.createSection(player.getName() + ".Numb");
                getHomes.save(file);
                getHomes.set("name", player.getUniqueId().toString());
                getHomes.set("HasHome", "No");
                getHomes.set(player.getUniqueId() + ".Numb", 0);
                getHomes.save(file);
                plugin.spawnnew(player);
            } catch (IOException e) {
                e.printStackTrace();
                plugin.logger
                        .severe("[HomeSpawn] Player Data File Creation Failed!");
                return;
            }
        }
        if (player.hasPermission("homespawn.admin")) {
            File file1 = new File(plugin.getDataFolder().getAbsolutePath()
                    + File.separator + "Update.yml");
            FileConfiguration getUpdate = YamlConfiguration
                    .loadConfiguration(file1);
            if (file1 != null || getUpdate.contains("Avail")
                    && getUpdate.getString("Avail").equalsIgnoreCase("true")) {
                if (!plugin.getConfig().getBoolean("AutoUpdate")
                        && plugin.getConfig().getBoolean("UpdateNotification")) {
                    player.sendMessage(ChatColor.GOLD
                            + "[HomeSpawn] An update is available on Bukkit Dev");
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void OnPlayerMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        Location From = e.getFrom();
        Location To = e.getTo();
        List<Integer> To1 = new ArrayList<Integer>();
        List<Integer> From1 = new ArrayList<Integer>();
        if (plugin.HomeSpawnLocations.containsKey(p)) {
            if (plugin.HomeSpawnTimeLeft.containsKey(p)) {
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
            Player p = ((Player) Hit);
            if (plugin.HomeSpawnTimeLeft.containsKey(p)) {
                if (Hitter instanceof Arrow) {
                    final Arrow arrow = (Arrow) Hitter;
                    if (arrow.getShooter() instanceof Player) {
                        plugin.HomeSpawnLocations.put(p, null);
                        p.sendMessage(ChatColor.GOLD
                                + "Teleport Canceled Because You Were Hit!");
                    } else if (arrow.getShooter() instanceof Skeleton) {
                        Players.add(p);
                        e.setCancelled(true);
                    }
                }
                if (Hitter instanceof Player || Hitter instanceof Wolf) {
                    plugin.HomeSpawnLocations.put(p, null);
                    p.sendMessage(ChatColor.GOLD
                            + "Teleport Canceled Because You Were Hit!");
                } else {
                    Players.add(p);
                    e.setCancelled(true);
                }
                if (Hitter instanceof Player || Hitter instanceof Wolf) {
                    plugin.HomeSpawnLocations.put(p, null);
                    plugin.HomeSpawnTimeLeft.remove(p);
                    p.sendMessage(ChatColor.GOLD
                            + "Teleport Canceled Because You Were Hit!");
                } else {
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
                    int x = getHomes.getInt(p.getUniqueId().toString() + ".x");
                    int y = getHomes.getInt(p.getUniqueId().toString() + ".y");
                    int z = getHomes.getInt(p.getUniqueId().toString() + ".z");
                    float yaw = getHomes.getInt(p.getUniqueId().toString()
                            + ".Yaw");
                    float pitch = getHomes.getInt(p.getUniqueId().toString()
                            + ".Pitch");
                    String cworld = getHomes.getString(p.getUniqueId()
                            .toString() + ".world");
                    World world = plugin.getServer().getWorld(cworld);
                    Location home = new Location(world, x, y, z, yaw, pitch);
                    home.add(0.5, 0, 0.5);
                    TeleportPlayer(p, home, "Home");
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
                    TeleportPlayer(p, home2, "Home");
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
        Player p = (Player) e.getPlayer();
        Inventory inv = plugin.HomesListInvs.get(p);
        inv.clear();
        plugin.HomesListInvs.put(p, inv);
    }

    public void TeleportPlayer(Player p, Location l, String r) {
        if (plugin.getConfig().getInt("TeleportTime") == 0
                || p.hasPermission("homespawn.bypassdelay")) {
            p.teleport(l);
            if (r.equalsIgnoreCase("Spawn")) {
                p.sendMessage(ChatColor.GOLD
                        + getMessages.getString("Spawn.SentToSpawn"));
            } else if (r.equalsIgnoreCase("Home")) {
                p.sendMessage(ChatColor.GOLD
                        + getMessages.getString("Home.SentHome"));
            }
        } else {
            String waitraw = ChatColor.GOLD + getMessages.getString("Wait");
            String Wait = waitraw.replace("{time}", ChatColor.RED
                    + plugin.getConfig().getString("TeleportTime")
                    + ChatColor.GOLD);
            p.sendMessage(Wait);
            plugin.HomeSpawnLocations.put(p, l);
            plugin.HomeSpawnTimeLeft.put(p,
                    plugin.getConfig().getInt("TeleportTime"));
        }

    }
}
