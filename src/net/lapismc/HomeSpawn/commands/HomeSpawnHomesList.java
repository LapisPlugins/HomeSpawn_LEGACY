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
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class HomeSpawnHomesList extends LapisCommand {

    private final HomeSpawn plugin;

    public HomeSpawnHomesList(HomeSpawn p) {
        super("homeslist", "Shows the players current homes", new ArrayList<>(Arrays.asList("listhomes", "listhome", "homelist")));
        this.plugin = p;
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
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
        new HomesListUI(hsPlayer).displayTo(p);
    }

    private class HomesListUI extends MenuPagged<Home> {

        final Random r = new Random(System.currentTimeMillis());
        final OfflinePlayer op;

        HomesListUI(HomeSpawnPlayer p) {
            super(9 * 2, null, p.getHomes());
            op = p.getOfflinePlayer();
            setTitle(getMenuTitle());
        }

        @Override
        protected String getMenuTitle() {
            return op == null ? "Player" : op.getName() + "'s homes";
        }

        @Override
        protected ItemStack convertToItemStack(Home home) {
            return ItemCreator.of(Material.WOOL).color(DyeColor.values()[(r.nextInt(DyeColor.values().length))])
                    .name(home.getName()).build().make();
        }

        @Override
        protected void onMenuClickPaged(Player player, Home home, ClickType clickType) {
            if (clickType.isLeftClick()) {
                player.closeInventory();
                home.teleportPlayer(player);
            }
        }

        @Override
        protected boolean updateButtonOnClick() {
            return false;
        }

        @Override
        protected String[] getInfo() {
            return new String[]{
                    "This is a list of your current homes", "", "Left click to teleport!"
            };
        }
    }
}
