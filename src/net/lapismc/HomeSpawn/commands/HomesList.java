package net.lapismc.HomeSpawn.commands;

import net.lapismc.HomeSpawn.HomeSpawn;
import net.lapismc.HomeSpawn.HomeSpawnCommand;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class HomesList {

    private HomeSpawn plugin;
    private HomeSpawnCommand hsc;

    public HomesList(HomeSpawn p, HomeSpawnCommand hsc) {
        this.plugin = p;
        this.hsc = hsc;
    }

    public void homesList(String[] args, Player player) {
        if (this.plugin.getConfig().getBoolean("InventoryMenu")) {
            hsc.showMenu(player);
            return;
        } else {
            UUID uuid = this.plugin.PlayertoUUID.get(player.getName());
            YamlConfiguration getHomes = this.plugin.HomeConfigs.get(uuid);
            if (!getHomes.contains(player.getUniqueId()
                    + ".list")) {
                getHomes.createSection(player.getUniqueId()
                        + ".list");
            }
            List<String> list = getHomes.getStringList(player
                    .getUniqueId() + ".list");
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
                    player.sendMessage(ChatColor.DARK_RED
                            + HomeSpawnCommand.getMessages.getString("Home.NoHomeSet"));
                }
            } else {
                player.sendMessage(ChatColor.DARK_RED
                        + HomeSpawnCommand.getMessages.getString("Home.NoHomeSet"));
            }
        }
    }

}
