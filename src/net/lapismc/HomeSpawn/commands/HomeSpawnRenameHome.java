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

import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.List;

public class HomeSpawnRenameHome {

    private net.lapismc.HomeSpawn.HomeSpawn plugin;

    public HomeSpawnRenameHome(net.lapismc.HomeSpawn.HomeSpawn p) {
        plugin = p;
    }

    public void renameHome(String[] args, Player p) {
        if (args.length == 2) {
            String oldHome = args[0];
            String newHome = args[1];
            YamlConfiguration homes = plugin.HSConfig.getPlayerData(p.getUniqueId());
            List<String> list = homes.getStringList("Homes.list");
            if (list.contains(oldHome)) {
                if (!list.contains(newHome)) {
                    Location loc = (Location) homes.get("Homes." + oldHome);
                    homes.set("Homes." + newHome, loc);
                    homes.set("Homes." + oldHome, null);
                    list.remove(oldHome);
                    list.add(newHome);
                    homes.set("Homes.list", list);
                    plugin.HSConfig.savePlayerData(p.getUniqueId(), homes);
                    p.sendMessage(plugin.HSConfig.getColoredMessage("Home.HomeRenamed"));
                } else {
                    p.sendMessage(plugin.HSConfig.getColoredMessage("Home.HomeAlreadyExists"));
                }
            } else {
                p.sendMessage(plugin.HSConfig.getColoredMessage("Home.NoHomeName"));
            }
        } else {
            p.sendMessage(plugin.SecondaryColor + "Usage: /renamehome (current name) (new name)");
        }
    }

}
