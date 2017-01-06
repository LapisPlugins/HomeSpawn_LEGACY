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
import java.util.Objects;

public class HomeSpawnDelSpawn {

    private HomeSpawn plugin;

    public HomeSpawnDelSpawn(HomeSpawn p) {
        this.plugin = p;
    }

    public void delSpawn(String[] args, Player player) {
        HashMap<HomeSpawnPermissions.perm, Integer> perms = plugin.HSPermissions.getPlayerPermissions(player.getUniqueId());
        if (perms.get(HomeSpawnPermissions.perm.setSpawn) == 1) {
            if (Objects.equals(plugin.HSConfig.spawn.getString("Spawn.SpawnSet"), "No")
                    || !plugin.HSConfig.spawn.contains("Spawn.SpawnSet")) {
                player.sendMessage(plugin.HSConfig.getColoredMessage("Spawn.NotSet"));
            } else if (plugin.HSConfig.spawn.getString("Spawn.SpawnSet")
                    .equalsIgnoreCase("Yes")) {
                plugin.HSConfig.spawn.set("Spawn.SpawnSet", "No");
                player.sendMessage(plugin.HSConfig.getColoredMessage("Spawn.Removed"));
                try {
                    plugin.HSConfig.spawn.save(this.plugin.HSConfig.spawnFile);
                    this.plugin.HSConfig.reload("silent");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } else {
            player.sendMessage(plugin.HSConfig.getColoredMessage("NoPerms"));
        }
    }

}
