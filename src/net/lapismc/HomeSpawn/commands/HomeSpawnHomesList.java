package net.lapismc.HomeSpawn.commands;

import net.lapismc.HomeSpawn.HomeSpawn;
import net.lapismc.HomeSpawn.HomeSpawnCommand;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.List;

public class HomeSpawnHomesList {

    final public HashMap<Player, Inventory> HomesListInvs = new HashMap<>();
    private HomeSpawn plugin;
    private HomeSpawnCommand hsc;

    public HomeSpawnHomesList(HomeSpawn p, HomeSpawnCommand hsc) {
        this.plugin = p;
        this.hsc = hsc;
    }

    public void homesList(String[] args, Player player) {
        if (this.plugin.getConfig().getBoolean("InventoryMenu")) {
            hsc.showMenu(player);
            return;
        } else {
            YamlConfiguration getHomes = this.plugin.HSConfig.getPlayerData(player.getUniqueId());
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
                    player.sendMessage(plugin.HSConfig.getColoredMessage("Home.CurrentHomes"));
                    player.sendMessage(ChatColor.RED + StringList);

                } else {
                    player.sendMessage(plugin.HSConfig.getColoredMessage("Home.NoHomeSet"));
                }
            } else {
                player.sendMessage(plugin.HSConfig.getColoredMessage("Home.NoHomeSet"));
            }
        }
    }
}
