package net.lapismc.HomeSpawn.commands;

import net.lapismc.HomeSpawn.HomeSpawn;
import net.lapismc.HomeSpawn.HomeSpawnCommand;
import net.lapismc.HomeSpawn.api.events.HomeTeleportEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class HomeSpawnHome {

    private HomeSpawn plugin;
    private HomeSpawnCommand hsc;

    public HomeSpawnHome(HomeSpawn p, HomeSpawnCommand hsc) {
        this.plugin = p;
        this.hsc = hsc;
    }

    public void home(String[] args, Player player) {
        HashMap<String, Integer> perms = plugin.HSPermissions.Permissions.get
                (plugin.HSPermissions.PlayerPermission.get(player.getUniqueId()));
        if (perms == null) {
            plugin.HSPermissions.init();
            perms = plugin.HSPermissions.Permissions.get(plugin.HSPermissions.PlayerPermission.
                    get(player.getUniqueId()));
        }
        UUID uuid = this.plugin.HSConfig.PlayertoUUID.get(player.getName());
        YamlConfiguration getHomes = this.plugin.HSConfig.HomeConfigs.get(uuid);
        if (getHomes == null) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.HSConfig.messages.getString("Error.Config")));
            plugin.HSConfig.reload("Silent");
            return;
        }
        if (!getHomes.contains(player.getUniqueId()
                + ".list")) {
            getHomes.createSection(player.getUniqueId()
                    + ".list");
            this.plugin.HSConfig.savePlayerData(uuid);
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
                HomeTeleportEvent HTE = new HomeTeleportEvent(plugin, player, home, "Home");
                Bukkit.getPluginManager().callEvent(HTE);
                if (HTE.isCancelled()) {
                    player.sendMessage("Your teleport was cancelled because " + HTE.getCancelReason());
                    return;
                }
                hsc.TeleportPlayer(player, home, "HomeSpawnHome", "HomeSpawnHome");
            } else {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.HSConfig.messages.getString("HomeSpawnHome.NoHomeSet")));
            }
        } else if (args.length == 1) {
            String home = args[0];
            if (getHomes.contains(home + ".HasHome")) {
                if (!getHomes.getString(home + ".HasHome")
                        .equalsIgnoreCase("yes")) {
                    if (getHomes.getInt(player.getUniqueId() + ".Numb") > 0) {
                        if (!list.isEmpty()) {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.HSConfig.messages.getString("HomeSpawnHome.NoHomeName")));
                            String list2 = list.toString();
                            String list3 = list2.replace("[", " ");
                            String StringList = list3.replace("]",
                                    " ");
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.HSConfig.messages.getString("HomeSpawnHome.CurrentHomes")));
                            player.sendMessage(ChatColor.RED + StringList);

                        } else {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                    plugin.HSConfig.messages
                                            .getString("HomeSpawnHome.NoHomeSet")));
                        }
                    } else {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                plugin.HSConfig.messages
                                        .getString("HomeSpawnHome.NoHomeSet")));
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
                    HomeTeleportEvent HTE = new HomeTeleportEvent(plugin, player, home2, home);
                    Bukkit.getPluginManager().callEvent(HTE);
                    if (HTE.isCancelled()) {
                        player.sendMessage("Your teleport was cancelled because " + HTE.getCancelReason());
                        return;
                    }
                    hsc.TeleportPlayer(player, home2, "HomeSpawnHome", home);
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
                                plugin.HSConfig.messages
                                        .getString("HomeSpawnHome.NoHomeSet")));
                    }
                } else {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            plugin.HSConfig.messages
                                    .getString("HomeSpawnHome.NoHomeSet")));
                }
                return;
            }
        }
    }
}



