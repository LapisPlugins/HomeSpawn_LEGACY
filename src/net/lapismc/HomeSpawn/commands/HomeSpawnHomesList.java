/*
 * Copyright 2017 Benjamin Martin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.lapismc.HomeSpawn.commands;

import net.lapismc.HomeSpawn.HomeSpawn;
import net.lapismc.HomeSpawn.playerdata.Home;
import net.lapismc.HomeSpawn.playerdata.HomeSpawnPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Wool;

import java.util.*;

public class HomeSpawnHomesList {

    final public HashMap<Player, Inventory> HomesListInvs = new HashMap<>();
    private HomeSpawn plugin;

    public HomeSpawnHomesList(HomeSpawn p) {
        this.plugin = p;
    }

    public void homesList(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.HSConfig.getMessage("Error.MustBePlayer"));
            return;
        }
        Player p = (Player) sender;
        if (this.plugin.getConfig().getBoolean("InventoryMenu")) {
            showMenu(p);
        } else {
            HomeSpawnPlayer HSPlayer = new HomeSpawnPlayer(plugin, p.getUniqueId());
            List<Home> list = HSPlayer.getHomes();
            if (!list.isEmpty()) {
                p.sendMessage(plugin.HSConfig.getColoredMessage("Home.CurrentHomes"));
                p.sendMessage(plugin.SecondaryColor + HSPlayer.getHomesList());
            } else {
                p.sendMessage(plugin.HSConfig.getColoredMessage("Home.NoHomeSet"));
            }
        }
    }

    private void showMenu(Player p) {
        YamlConfiguration getHomes = this.plugin.HSConfig.getPlayerData(p.getUniqueId());
        List<String> homes = getHomes.getStringList("Homes.list");
        if (homes.isEmpty()) {
            p.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.HSConfig.getColoredMessage("Home.NoHomeSet")));
            return;
        }
        ArrayList<DyeColor> dc = new ArrayList<>();
        dc.add(DyeColor.BLACK);
        dc.add(DyeColor.BLUE);
        dc.add(DyeColor.GRAY);
        dc.add(DyeColor.GREEN);
        dc.add(DyeColor.MAGENTA);
        dc.add(DyeColor.ORANGE);
        Random r = new Random(System.currentTimeMillis());
        int slots = homes.size() % 9 == 0 ? homes.size() / 9 : homes.size() / 9 + 1;
        if (HomesListInvs.containsKey(p)) {
            if (!(HomesListInvs.get(p).getSize() == slots * 9)) {
                Inventory inv = Bukkit.createInventory(p, 9 * slots, ChatColor.GOLD + p.getName() + "'s Homes List");
                HomesListInvs.put(p, inv);
            }
        } else {
            Inventory inv = Bukkit.createInventory(p, 9 * slots, ChatColor.GOLD + p.getName() + "'s Homes List");
            HomesListInvs.put(p, inv);
        }
        for (String home : homes) {
            ItemStack i = new Wool(dc.get(r.nextInt(5))).toItemStack(1);
            ItemMeta im = i.getItemMeta();
            im.setDisplayName(ChatColor.GOLD + home);
            im.setLore(Arrays.asList(ChatColor.GOLD + "Click To Teleport To",
                    ChatColor.RED + home));
            i.setItemMeta(im);
            HomesListInvs.get(p).addItem(i);
        }
        p.openInventory(HomesListInvs.get(p));
    }
}
