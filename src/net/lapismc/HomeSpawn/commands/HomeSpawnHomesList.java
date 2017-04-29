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
        } else {
            YamlConfiguration getHomes = this.plugin.HSConfig.getPlayerData(player.getUniqueId());
            List<String> list = getHomes.getStringList("Homes.list");
            if (!list.isEmpty()) {
                String list2 = list.toString();
                String list3 = list2.replace("[", " ");
                String StringList = list3.replace("]", " ");
                player.sendMessage(plugin.HSConfig.getColoredMessage("Home.CurrentHomes"));
                player.sendMessage(ChatColor.RED + StringList);
            } else {
                player.sendMessage(plugin.HSConfig.getColoredMessage("Home.NoHomeSet"));
            }
        }
    }
}
