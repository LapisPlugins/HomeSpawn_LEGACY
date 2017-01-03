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

import java.util.List;

public class HomeSpawnHome {

    private HomeSpawn plugin;
    private HomeSpawnCommand hsc;

    public HomeSpawnHome(HomeSpawn p, HomeSpawnCommand hsc) {
        this.plugin = p;
        this.hsc = hsc;
    }

    public void home(String[] args, Player player) {
        YamlConfiguration getHomes = this.plugin.HSConfig.getPlayerData(player.getUniqueId());
        if (getHomes == null) {
            player.sendMessage(plugin.HSConfig.getColoredMessage("Error.Config"));
            plugin.HSConfig.reload("Silent");
            return;
        }
        if (!getHomes.contains(player.getUniqueId()
                + ".list")) {
            getHomes.createSection(player.getUniqueId()
                    + ".list");
            this.plugin.HSConfig.savePlayerData(player.getUniqueId(), getHomes);
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
                hsc.TeleportPlayer(player, home, "Home", "Home");
            } else {
                player.sendMessage(plugin.HSConfig.getColoredMessage("Home.NoHomeSet"));
            }
        } else if (args.length == 1) {
            String home = args[0];
            if (getHomes.contains(home + ".HasHome")) {
                if (!getHomes.getString(home + ".HasHome")
                        .equalsIgnoreCase("yes")) {
                    if (getHomes.getInt(player.getUniqueId() + ".Numb") > 0) {
                        if (!list.isEmpty()) {
                            player.sendMessage(plugin.HSConfig.getColoredMessage("Home.NoHomeName"));
                            String list2 = list.toString();
                            String list3 = list2.replace("[", " ");
                            String StringList = list3.replace("]",
                                    " ");
                            player.sendMessage(plugin.HSConfig.getColoredMessage("Home.CurrentHomes"));
                            player.sendMessage(ChatColor.RED + StringList);

                        } else {
                            player.sendMessage(plugin.HSConfig.getColoredMessage("Home.NoHomeSet"));
                        }
                    } else {
                        player.sendMessage(plugin.HSConfig.getColoredMessage("Home.NoHomeSet"));
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
                        player.sendMessage(plugin.HSConfig.getColoredMessage("Home.NoHomeSet"));
                    }
                } else {
                    player.sendMessage(plugin.HSConfig.getColoredMessage("Home.NoHomeSet"));
                }
                return;
            }
        }
    }
}



