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
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class HomeSpawnSpawn {

    private final HomeSpawn plugin;
    private final HomeSpawnCommand hsc;

    public HomeSpawnSpawn(HomeSpawn p, HomeSpawnCommand hsc) {
        plugin = p;
        this.hsc = hsc;
    }

    public void spawn(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.HSConfig.getMessage("Error.MustBePlayer"));
            return;
        }
        Player p = (Player) sender;
        HashMap<HomeSpawnPermissions.perm, Integer> perms = plugin.HSPermissions.getPlayerPermissions(p.getUniqueId());
        if (perms.get(HomeSpawnPermissions.perm.spawn) == 1) {
            if (plugin.HSConfig.spawn.get("spawn") != null) {
                Location Spawn = (Location) plugin.HSConfig.spawn.get("spawn");
                SpawnTeleportEvent STE = new SpawnTeleportEvent(plugin, p, Spawn);
                Bukkit.getPluginManager().callEvent(STE);
                if (STE.isCancelled()) {
                    p.sendMessage(plugin.PrimaryColor + "Your teleport was cancelled because " + STE.getCancelReason());
                    return;
                }
                hsc.TeleportPlayer(p, Spawn, "Spawn");
            } else {
                p.sendMessage(plugin.HSConfig.getColoredMessage("Spawn.NotSet"));
            }
        } else {
            p.sendMessage(plugin.HSConfig.getColoredMessage("NoPerms"));
        }
    }

}
