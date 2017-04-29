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
import net.lapismc.HomeSpawn.HomeSpawnPermissions;
import net.lapismc.HomeSpawn.api.events.SpawnTeleportEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class HomeSpawnSpawn {

    private HomeSpawn plugin;
    private HomeSpawnCommand hsc;

    public HomeSpawnSpawn(HomeSpawn p, HomeSpawnCommand hsc) {
        plugin = p;
        this.hsc = hsc;
    }

    public void spawn(String[] args, Player player) {
        HashMap<HomeSpawnPermissions.perm, Integer> perms = plugin.HSPermissions.getPlayerPermissions(player.getUniqueId());
        if (perms.get(HomeSpawnPermissions.perm.spawn) == 1) {
            if (plugin.HSConfig.spawn.get("spawn") != null) {
                Location Spawn = (Location) plugin.HSConfig.spawn.get("spawn");
                SpawnTeleportEvent STE = new SpawnTeleportEvent(plugin, player, Spawn);
                Bukkit.getPluginManager().callEvent(STE);
                if (STE.isCancelled()) {
                    player.sendMessage(ChatColor.GOLD + "Your teleport was cancelled because " + STE.getCancelReason());
                    return;
                }
                hsc.TeleportPlayer(player, Spawn, "Spawn");
            } else {
                player.sendMessage(plugin.HSConfig.getColoredMessage("Spawn.NotSet"));
            }
        } else {
            player.sendMessage(plugin.HSConfig.getColoredMessage("NoPerms"));
        }
    }

}
