/*
 * Copyright 2018 Benjamin Martin
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

import me.kangarko.ui.UIDesignerAPI;
import me.kangarko.ui.menu.menues.MenuPagged;
import me.kangarko.ui.model.ItemCreator;
import net.lapismc.HomeSpawn.HomeSpawn;
import net.lapismc.HomeSpawn.playerdata.Home;
import net.lapismc.HomeSpawn.playerdata.HomeSpawnPlayer;
import net.lapismc.HomeSpawn.util.EasyComponent;
import net.lapismc.HomeSpawn.util.LapisCommand;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class HomeSpawnHomesList extends LapisCommand {

    final public HashMap<Player, Inventory> HomesListInventories = new HashMap<>();
    private final HomeSpawn plugin;

    public HomeSpawnHomesList(HomeSpawn p) {
        super("homeslist", "Shows the players current homes", new ArrayList<>());
        this.plugin = p;
    }

    @Override
    public void onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
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
                EasyComponent component = new EasyComponent(plugin.HSConfig.getColoredMessage("Home.CurrentHomes"))
                        .append("\n");
                for (Home home : list) {
                    component.append(plugin.SecondaryColor + home.getName() + "  ")
                            .onClickRunCmd("/home " + home.getName())
                            .onHover(plugin.PrimaryColor + "Click to teleport");
                }
                component.append("\n" + plugin.HSConfig.getColoredMessage("Home.ClickToTeleport"));
                component.send(p);
            } else {
                p.sendMessage(plugin.HSConfig.getColoredMessage("Home.NoHomeSet"));
            }
        }
    }

    private void showMenu(Player p) {
        UIDesignerAPI.setPlugin(plugin);
        HomeSpawnPlayer hsPlayer = plugin.getPlayer(p.getUniqueId());
        new homesListUI(hsPlayer).displayTo(p);
        /*
        This is the old menu code
         YamlConfiguration getHomes = this.plugin.HSConfig.getPlayerData(p.getUniqueId());
         List<String> homes = getHomes.getStringList("Homes.list");
         if (homes.isEmpty()) {
         p.sendMessage(ChatColor.translateAlternateColorCodes('&',
         plugin.HSConfig.getColoredMessage("Home.NoHomeSet")));
         return;
         }
         Random r = new Random(System.currentTimeMillis());
         int slots = homes.size() % 9 == 0 ? homes.size() / 9 : homes.size() / 9 + 1;
         if (HomesListInventories.containsKey(p)) {
         if (!(HomesListInventories.get(p).getSize() == slots * 9)) {
         Inventory inv = Bukkit.createInventory(p, 9 * slots, ChatColor.GOLD + p.getName() + "'s Homes List");
         HomesListInventories.put(p, inv);
         }
         } else {
         Inventory inv = Bukkit.createInventory(p, 9 * slots, ChatColor.GOLD + p.getName() + "'s Homes List");
         HomesListInventories.put(p, inv);
         }
         for (String home : homes) {
         ItemStack i = new Wool(DyeColor.values()[(r.nextInt(DyeColor.values().length))]).toItemStack(1);
         ItemMeta im = i.getItemMeta();
         im.setDisplayName(ChatColor.GOLD + home);
         im.setLore(Arrays.asList(ChatColor.GOLD + "Click To Teleport To",
         ChatColor.RED + home));
         i.setItemMeta(im);
         HomesListInventories.get(p).addItem(i);
         }
         p.openInventory(HomesListInventories.get(p));
         */
    }

    private class homesListUI extends MenuPagged<Home> {

        Random r = new Random(System.currentTimeMillis());

        homesListUI(HomeSpawnPlayer p) {
            super(9 * 2, null, p.getHomes());
        }

        @Override
        protected String getMenuTitle() {
            return "Your homes";
        }

        @Override
        protected ItemStack convertToItemStack(Home home) {
            return ItemCreator.of(Material.WOOL).color(DyeColor.values()[(r.nextInt(DyeColor.values().length))])
                    .name(home.getName()).build().make();
        }

        @Override
        protected void onMenuClickPaged(Player player, Home home, ClickType clickType) {
            if (clickType.isLeftClick()) {
                Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> home.teleportPlayer(player), 1);
            }
        }

        @Override
        protected String[] getInfo() {
            return new String[]{
                    "This is a list of your current homes", "", "Left click to teleport!"
            };
        }
    }
}
