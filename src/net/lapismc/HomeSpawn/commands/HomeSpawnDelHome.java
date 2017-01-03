package net.lapismc.HomeSpawn.commands;

import net.lapismc.HomeSpawn.HomeSpawn;
import net.lapismc.HomeSpawn.api.events.HomeDelEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.List;

public class HomeSpawnDelHome {

    private HomeSpawn plugin;

    public HomeSpawnDelHome(HomeSpawn p) {
        this.plugin = p;
    }

    public void delHome(String[] args, Player player) {
        YamlConfiguration getHomes = plugin.HSConfig.getPlayerData(player.getUniqueId());
        if (!getHomes.contains(player.getUniqueId()
                + ".list")) {
            getHomes.createSection(player.getUniqueId()
                    + ".list");
            plugin.HSConfig.savePlayerData(player.getUniqueId(), getHomes);
        }
        List<String> list = getHomes.getStringList(player
                .getUniqueId() + ".list");
        if (args.length == 0) {
            int HomeNumb = getHomes.getInt(player.getUniqueId() + ".Numb");
            if (getHomes.getString("HasHome")
                    .equalsIgnoreCase("no")
                    || !getHomes.contains("HasHome")) {
                HomeDelEvent HDE = new HomeDelEvent(plugin, player, player.getLocation(), "Home");
                Bukkit.getPluginManager().callEvent(HDE);
                if (HDE.isCancelled()) {
                    player.sendMessage("Your home has not been deleted because " + HDE.getReason());
                    return;
                }
                player.sendMessage(plugin.HSConfig.getColoredMessage("Home.NoHomeSet"));
            } else if (getHomes.getString("HasHome")
                    .equalsIgnoreCase("yes")) {
                player.sendMessage(plugin.HSConfig.getColoredMessage("Home.HomeRemoved"));
                getHomes.set("HasHome", "No");
                getHomes.set(player.getUniqueId()
                        + ".Numb", HomeNumb - 1);
                if (list.contains("Home")) {
                    list.remove("Home");
                    getHomes.set(player.getUniqueId()
                            + ".list", list);
                }
                this.plugin.HSConfig.savePlayerData(player.getUniqueId(), getHomes);
            } else {
                player.sendMessage(plugin.HSConfig.getColoredMessage("Home.NoHomeSet"));
                if (getHomes.getInt(player.getUniqueId()
                        + ".Numb") > 0) {
                    if (!list.isEmpty()) {
                        String list2 = list.toString();
                        String list3 = list2.replace("[", " ");
                        String StringList = list3.replace("]", " ");
                        player.sendMessage(plugin.HSConfig.getColoredMessage("Home.CurrentHomes"));
                        player.sendMessage(ChatColor.RED
                                + StringList);
                    } else {
                        player.sendMessage(plugin.HSConfig.getColoredMessage("Home.NoHomeSet"));
                    }
                } else {
                    player.sendMessage(plugin.HSConfig.getColoredMessage("Home.NoHomeSet"));
                }
            }
        } else if (args.length == 1) {
            String home = args[0];
            int HomeNumb = getHomes.getInt(player.getUniqueId() + ".Numb");
            if (!getHomes.contains(home + ".HasHome")
                    || getHomes.getString(home + ".HasHome")
                    .equalsIgnoreCase("no")) {
                player.sendMessage(plugin.HSConfig.getColoredMessage("Home.NoHomeName"));
                if (getHomes.getInt(player.getUniqueId()
                        + ".Numb") > 0) {
                    if (!list.isEmpty()) {
                        String list2 = list.toString();
                        String list3 = list2.replace("[", " ");
                        String StringList = list3.replace("]", " ");
                        player.sendMessage(plugin.HSConfig.getColoredMessage("Home.CurrentHomes"));
                        player.sendMessage(ChatColor.RED
                                + StringList);
                    } else {
                        player.sendMessage(plugin.HSConfig.getColoredMessage("Home.NoHomeSet"));
                    }
                } else {
                    player.sendMessage(plugin.HSConfig.getColoredMessage("Home.NoHomeSet"));
                }
            } else if (getHomes.getString(home + ".HasHome")
                    .equalsIgnoreCase("yes")) {
                HomeDelEvent HDE = new HomeDelEvent(plugin, player, player.getLocation(), home);
                Bukkit.getPluginManager().callEvent(HDE);
                if (HDE.isCancelled()) {
                    player.sendMessage("Your home has not been deleted because " + HDE.getReason());
                    return;
                }
                player.sendMessage(plugin.HSConfig.getColoredMessage("Home.HomeRemoved"));
                getHomes.set(home + ".HasHome", "No");
                getHomes.set(player.getUniqueId()
                        + ".Numb", HomeNumb - 1);
                if (list.contains(home)) {
                    list.remove(home);
                    getHomes.set(player.getUniqueId()
                            + ".list", list);
                }
                this.plugin.HSConfig.savePlayerData(player.getUniqueId(), getHomes);
            } else {
                player.sendMessage(plugin.HSConfig.getColoredMessage("Home.NoHomeName"));
                if (getHomes.getInt(player.getUniqueId()
                        + ".Numb") > 0) {
                    if (!list.isEmpty()) {
                        String list2 = list.toString();
                        String list3 = list2.replace("[", " ");
                        String StringList = list3.replace("]", " ");
                        player.sendMessage(plugin.HSConfig.getColoredMessage("Home.CurrentHomes"));
                        player.sendMessage(ChatColor.RED
                                + StringList);
                    } else {
                        player.sendMessage(plugin.HSConfig.getColoredMessage("Home.NoHomeSet"));
                    }
                } else {
                    player.sendMessage(plugin.HSConfig.getColoredMessage("Home.NoHomeSet"));
                }
            }
        } else {
            player.sendMessage(plugin.HSConfig.getColoredMessage("Error.Args+"));
        }
    }

}
