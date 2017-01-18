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

public class HomeSpawnSetSpawn {

    private HomeSpawn plugin;

    public HomeSpawnSetSpawn(HomeSpawn p) {
        plugin = p;
    }

    public void setSpawn(String[] args, Player player) {
        HashMap<HomeSpawnPermissions.perm, Integer> perms = plugin.HSPermissions.getPlayerPermissions(player.getUniqueId());
        if (perms.get(HomeSpawnPermissions.perm.setSpawn) == 1) {
            if (args.length == 0) {
                plugin.HSConfig.spawn.set("spawn", player.getLocation());
                player.sendMessage(plugin.HSConfig.getColoredMessage("Spawn.SpawnSet"));
            } else if (args[0].equalsIgnoreCase("new")) {
                plugin.HSConfig.spawn.set("spawnnew", player.getLocation());
                player.sendMessage(plugin.HSConfig.getColoredMessage("Spawn.SpawnNewSet"));
            } else {
                plugin.help(player);
            }
            try {
                plugin.HSConfig.spawn.save(plugin.HSConfig.spawnFile);
                plugin.HSConfig.reload("Silent");
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            player.sendMessage(plugin.HSConfig.getColoredMessage("NoPerms"));

        }
    }

}
