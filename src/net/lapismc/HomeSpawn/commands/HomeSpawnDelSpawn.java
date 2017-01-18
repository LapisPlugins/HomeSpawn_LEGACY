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
import net.lapismc.HomeSpawn.HomeSpawnPermissions;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.HashMap;

public class HomeSpawnDelSpawn {

    private HomeSpawn plugin;

    public HomeSpawnDelSpawn(HomeSpawn p) {
        this.plugin = p;
    }

    public void delSpawn(String[] args, Player player) {
        HashMap<HomeSpawnPermissions.perm, Integer> perms = plugin.HSPermissions.getPlayerPermissions(player.getUniqueId());
        if (perms.get(HomeSpawnPermissions.perm.setSpawn) == 1) {
            if (args.length == 0) {
                if (!plugin.HSConfig.spawn.contains("spawn")) {
                    player.sendMessage(plugin.HSConfig.getColoredMessage("Spawn.NotSet"));
                } else {
                    plugin.HSConfig.spawn.set("spawn", null);
                    player.sendMessage(plugin.HSConfig.getColoredMessage("Spawn.Removed"));
                    try {
                        plugin.HSConfig.spawn.save(plugin.HSConfig.spawnFile);
                        plugin.HSConfig.reload("silent");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else if (args[0].equalsIgnoreCase("new")) {
                if (!plugin.HSConfig.spawn.contains("spawnnew")) {
                    player.sendMessage(plugin.HSConfig.getColoredMessage("Spawn.NotSet"));
                } else {
                    plugin.HSConfig.spawn.set("spawnnew", null);
                    player.sendMessage(plugin.HSConfig.getColoredMessage("Spawn.Removed"));
                    try {
                        plugin.HSConfig.spawn.save(plugin.HSConfig.spawnFile);
                        plugin.HSConfig.reload("silent");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            player.sendMessage(plugin.HSConfig.getColoredMessage("NoPerms"));
        }
    }

}
