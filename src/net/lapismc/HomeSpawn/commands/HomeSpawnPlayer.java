package net.lapismc.HomeSpawn.commands;

import net.lapismc.HomeSpawn.HomeSpawn;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.ocpsoft.prettytime.PrettyTime;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class HomeSpawnPlayer {

    private HomeSpawn plugin;
    private PrettyTime p = new PrettyTime();

    public HomeSpawnPlayer(HomeSpawn plugin) {
        this.plugin = plugin;
    }

    public void HomeSpawnPlayer(String[] args, Player player) {
        HashMap<String, Integer> perms = plugin.HSPermissions.Permissions.get(plugin.HSPermissions.PlayerPermission.
                get(player.getUniqueId()));
        if (perms == null) {
            plugin.HSPermissions.init();
            perms = plugin.HSPermissions.Permissions.get(plugin.HSPermissions.PlayerPermission.
                    get(player.getUniqueId()));
        }
        if (perms.get("stats") == 0) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.HSConfig.messages.getString("NoPerms")));
            return;
        }
        if (args.length != 2 && args.length != 3) {
            help(player);
        } else if (args.length == 2) {
            String name = args[1];
            UUID uuid;
            YamlConfiguration homes;
            uuid = plugin.PlayertoUUID.get(name);
            if (uuid == null) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.HSConfig.messages.getString("NoPlayerData")));
                return;
            }
            homes = plugin.HSConfig.HomeConfigs.get(uuid);
            if (homes == null) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.HSConfig.messages.getString("NoPlayerData")));
                return;
            }
            HashMap<String, Integer> uuidperms = plugin.HSPermissions.Permissions.get(plugin.HSPermissions.PlayerPermission.
                    get(uuid));
            player.sendMessage(ChatColor.RED + "----- " + ChatColor.GOLD + "Stats for " + ChatColor.BLUE + name + ChatColor.RED + " -----");
            player.sendMessage(ChatColor.RED + "Players Permission: " + ChatColor.GOLD + plugin.HSPermissions.PlayerPermission.get(uuid).getName());
            String time;
            if (homes.get("login") == null) {
                time = "Before Player Stats Were Introduced";
            }
            if (homes.get("login") instanceof String && homes.getString("login") == "-") {
                if (Bukkit.getOfflinePlayer(uuid).isOnline()) {
                    time = "Now!";
                } else {
                    time = "Error!";
                }
            } else {
                time = p.format(new Date(homes.getLong("login")));
            }
            player.sendMessage(ChatColor.RED + "Player " + ChatColor.BLUE + name + ChatColor.RED + " was last online: "
                    + ChatColor.GOLD + time);
            String usedHomes = String.valueOf(homes.getInt(uuid.toString() + ".Numb"));
            player.sendMessage(ChatColor.GOLD + usedHomes + ChatColor.RED + " out of " + ChatColor.GOLD + perms.get("homes")
                    + " homes used");
            if (homes.getInt(uuid.toString() + ".Numb") > 0) {
                player.sendMessage(ChatColor.RED + "The players home(s) are:");
                List<String> list = homes.getStringList(player
                        .getUniqueId() + ".list");
                String list2 = list.toString();
                String list3 = list2.replace("[", " ");
                String StringList = list3.replace("]", " ");
                player.sendMessage(ChatColor.GOLD + StringList);
            }
        } else if (args.length == 3) {
            String name = args[1];
            UUID uuid;
            YamlConfiguration homes;
            uuid = plugin.PlayertoUUID.get(name);
            if (uuid == null) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.HSConfig.messages.getString("NoPlayerData")));
                return;
            }
            homes = plugin.HSConfig.HomeConfigs.get(uuid);
            if (homes == null) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.HSConfig.messages.getString("NoPlayerData")));
                return;
            }
            teleportPlayer(player, args[2], homes);
        } else {
            help(player);
        }
    }

    private void help(Player player) {
        player.sendMessage(ChatColor.RED + "--------" + ChatColor.GOLD + " Player Stats Help " + ChatColor.RED + "--------");
        player.sendMessage(ChatColor.RED + "/homespawn player:" + ChatColor.GOLD + " Displays this help");
        player.sendMessage(ChatColor.RED + "/homespawn player (name):" + ChatColor.GOLD + " Shows the stats of the player given");
        player.sendMessage(ChatColor.RED + "/homespawn player (name) (home name):" + ChatColor.GOLD + " Teleports you to that players" +
                " home of that name");
    }

    private void teleportPlayer(Player player, String home, YamlConfiguration getHomes) {
        if (home.equalsIgnoreCase("home") && getHomes.getString("HasHome").equalsIgnoreCase("yes")) {
            String uuid = getHomes.getString("name");
            int x = getHomes.getInt(uuid + ".x");
            int y = getHomes.getInt(uuid + ".y");
            int z = getHomes.getInt(uuid + ".z");
            float yaw = getHomes.getInt(uuid + ".Yaw");
            float pitch = getHomes.getInt(uuid + ".Pitch");
            String cworld = getHomes.getString(uuid
                    + ".world");
            World world = plugin.getServer().getWorld(
                    cworld);
            Location home2 = new Location(world, x, y, z,
                    yaw, pitch);
            home2.add(0.5, 0, 0.5);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.HSConfig.messages.getString("Home.SentHome")));
            player.teleport(home2);
            return;
        }
        if (getHomes.contains(home) && getHomes.getString(home + ".HasHome")
                .equalsIgnoreCase("yes")) {
            int x = getHomes.getInt(home + ".x");
            int y = getHomes.getInt(home + ".y");
            int z = getHomes.getInt(home + ".z");
            float yaw = getHomes.getInt(home + ".Yaw");
            float pitch = getHomes.getInt(home + ".Pitch");
            String cworld = getHomes.getString(home
                    + ".world");
            World world = plugin.getServer().getWorld(
                    cworld);
            Location home2 = new Location(world, x, y, z,
                    yaw, pitch);
            home2.add(0.5, 0, 0.5);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.HSConfig.messages.getString("Home.SentHome")));
            player.teleport(home2);
        } else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.HSConfig.messages.getString("Home.NoHomeName")));
        }
    }
}
