package net.lapismc.HomeSpawn.commands;

import net.lapismc.HomeSpawn.HomeSpawn;
import net.lapismc.HomeSpawn.HomeSpawnCommand;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Home {

    private HomeSpawn plugin;
    private HomeSpawnCommand hsc;

    public Home(HomeSpawn p, HomeSpawnCommand hsc) {
        this.plugin = p;
        this.hsc = hsc;
    }

    public void home(String[] args, Player player) {
        HashMap<String, Integer> perms = plugin.Permissions.get
                (plugin.PlayerPermission.get(player.getUniqueId()));
        UUID uuid = this.plugin.PlayertoUUID.get(player.getName());
        YamlConfiguration getHomes = this.plugin.HomeConfigs.get(uuid);
        if (getHomes == null) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.messages.getString("Error.Config")));
            plugin.reload(null);
            return;
        }
        if (!getHomes.contains(player.getUniqueId()
                + ".list")) {
            getHomes.createSection(player.getUniqueId()
                    + ".list");
            this.plugin.savePlayerData(uuid);
        }
        List<String> list = getHomes.getStringList(player
                .getUniqueId() + ".list");
        if (args.length == 0) {
            if (getHomes.getString("HasHome")
                    .equalsIgnoreCase("yes")) {
                int x = getHomes.getInt(player.getUniqueId() + ".x");
                int y = getHomes.getInt(player.getUniqueId() + ".y");
                int z = getHomes.getInt(player.getUniqueId() + ".z");
                float yaw = getHomes.getInt(player.getUniqueId() + ".Yaw");
                float pitch = getHomes.getInt(player.getUniqueId() + ".Pitch");
                String cworld = getHomes.getString(player
                        .getUniqueId() + ".world");
                World world = this.plugin.getServer().getWorld(cworld);
                Location home = new Location(world, x, y, z, yaw,
                        pitch);
                home.add(0.5, 0, 0.5);
                hsc.TeleportPlayer(player, home, "Home", "Home");
            } else {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.messages.getString("Home.NoHomeSet")));
            }
        } else if (args.length == 1) {
            String home = args[0];
            if (getHomes.contains(home + ".HasHome")) {
                if (!getHomes.getString(home + ".HasHome")
                        .equalsIgnoreCase("yes")) {
                    if (getHomes.getInt(player.getUniqueId() + ".Numb") > 0) {
                        if (!list.isEmpty()) {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.messages.getString("Home.NoHomeName")));
                            String list2 = list.toString();
                            String list3 = list2.replace("[", " ");
                            String StringList = list3.replace("]",
                                    " ");
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.messages.getString("Home.CurrentHomes")));
                            player.sendMessage(ChatColor.RED + StringList);

                        } else {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                    plugin.messages
                                            .getString("Home.NoHomeSet")));
                        }
                    } else {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                plugin.messages
                                        .getString("Home.NoHomeSet")));
                    }
                    return;
                }

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
                    hsc.TeleportPlayer(player, home2, "Home", home);
                }
            } else {
                player.sendMessage(ChatColor.RED
                        + "A home with this name does not exist!");
                if (getHomes.getInt(player.getUniqueId()
                        + ".Numb") > 0) {
                    if (!list.isEmpty()) {
                        String list2 = list.toString();
                        String list3 = list2.replace("[", " ");
                        String StringList = list3.replace("]", " ");
                        player.sendMessage(ChatColor.GOLD
                                + "Your Current Homes Are:");
                        player.sendMessage(ChatColor.RED + StringList);


                    } else {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                plugin.messages
                                        .getString("Home.NoHomeSet")));
                    }
                } else {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            plugin.messages
                                    .getString("Home.NoHomeSet")));
                }
                return;
            }
        }
    }
}



