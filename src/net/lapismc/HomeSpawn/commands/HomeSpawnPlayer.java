package net.lapismc.HomeSpawn.commands;

import net.lapismc.HomeSpawn.HomeSpawn;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.ocpsoft.prettytime.PrettyTime;

import java.util.*;

public class HomeSpawnPlayer {

    private HomeSpawn plugin;
    private PrettyTime p = new PrettyTime();

    public HomeSpawnPlayer(HomeSpawn plugin) {
        this.plugin = plugin;
    }

    public void HomeSpawnPlayer(String[] args, Player player) {
        HashMap<String, Integer> perms = plugin.Permissions.get(plugin.PlayerPermission.
                get(player.getUniqueId()));
        if (perms.get(player.getUniqueId()) == 0) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.messages.getString("NoPerms")));
            return;
        }
        if (args.length == 1) {
            help(player);
        } else if (args.length == 2) {
            String name = args[1];
            UUID uuid;
            YamlConfiguration homes;
            try {
                uuid = plugin.PlayertoUUID.get(name);
                homes = plugin.HomeConfigs.get(uuid);
            } catch (Exception e) {
                player.sendMessage("Player name incorrect or that player has no HomeSpawn data");
                return;
            }
            HashMap<String, Integer> uuidperms = plugin.Permissions.get(plugin.PlayerPermission.
                    get(uuid));
            player.sendMessage("----- Stats for " + name + " -----");
            player.sendMessage("Players Permission: " + plugin.PlayerPermission.get(uuid).getName());
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
            player.sendMessage("Player " + name + " was last online: " + time);
            player.sendMessage(plugin.getConfig().getInt(uuid.toString() + ".Numb") + " out of " + perms.get("homes")
                    + " homes used");
            if (homes.getInt(uuid.toString() + ".Numb") > 0) {
                player.sendMessage("The players home(s) are:");
                List<String> l = plugin.getConfig().getStringList(uuid.toString() + ".list");
                player.sendMessage(l.toString().replace('[', ' ').replace(']', ' '));
            }
        } else if (args.length == 3) {
            String name = args[1];
            UUID uuid;
            YamlConfiguration homes;
            try {
                uuid = plugin.PlayertoUUID.get(name);
                homes = plugin.HomeConfigs.get(uuid);
            } catch (Exception e) {
                player.sendMessage("Player name incorrect or that player has no HomeSpawn data");
                return;
            }
            teleportPlayer(player, args[2], homes);
        } else {
            help(player);
        }
    }

    private void help(Player player) {
        ArrayList<String> help = new ArrayList<String>();
        help.add("Player Stats Help");
        help.add("/homespawn player: Displays this help");
        help.add("/homespawn player (name): Shows the stats of the player given");
        help.add("/homespawn player (name) (home name): Teleports you to that players" +
                " home of that name");
        player.sendMessage((String[]) help.toArray());
    }

    private void teleportPlayer(Player player, String home, YamlConfiguration getHomes) {
        if (getHomes.getString(home + ".HasHome")
                .equalsIgnoreCase("yes")) {
            int x = getHomes.getInt(home + ".x");
            int y = getHomes.getInt(home + ".y");
            int z = getHomes.getInt(home + ".z");
            float yaw = getHomes.getInt(home + ".Yaw");
            float pitch = getHomes.getInt(home + ".Pitch");
            String cworld = getHomes.getString(home
                    + ".world");
            World world = this.plugin.getServer().getWorld(
                    cworld);
            Location home2 = new Location(world, x, y, z,
                    yaw, pitch);
            home2.add(0.5, 0, 0.5);
            player.sendMessage(ChatColor.GOLD
                    + plugin.messages.getString("Home.SentHome"));
            player.teleport(home2);
        } else {
            player.sendMessage("A home with that name doesn't exist!");
        }
    }
}
