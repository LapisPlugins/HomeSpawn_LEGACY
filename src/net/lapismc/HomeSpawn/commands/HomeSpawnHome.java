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

import net.lapismc.HomeSpawn.HomeSpawn;
import net.lapismc.HomeSpawn.HomeSpawnPermissions;
import net.lapismc.HomeSpawn.api.events.HomeTeleportEvent;
import net.lapismc.HomeSpawn.playerdata.Home;
import net.lapismc.HomeSpawn.playerdata.HomeSpawnPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class HomeSpawnHome extends LapisCommand {

    private final HomeSpawn plugin;

    public HomeSpawnHome(HomeSpawn p) {
        super("home", "Sends the player to the home they set with /sethome", new ArrayList<>());
        this.plugin = p;
    }

    @Override
    public void onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.HSConfig.getMessage("Error.MustBePlayer"));
            return;
        }
        Player p = (Player) sender;
        HomeSpawnPlayer HSPlayer = new HomeSpawnPlayer(plugin, p.getUniqueId());
        List<String> list = HSPlayer.getHomesStringList();
        if (HSPlayer.getPermissionValue(HomeSpawnPermissions.perm.homes) == 0) {
            p.sendMessage(plugin.HSConfig.getColoredMessage("NoPerms"));
            return;
        }
        if (list.size() > HSPlayer.getPermissionValue(HomeSpawnPermissions.perm.homes)) {
            p.sendMessage(plugin.HSConfig.getColoredMessage("Home.ToManyHomes").replace("%ALLOWED%",
                    HSPlayer.getPermissionValue(HomeSpawnPermissions.perm.homes) + "").replace("%AMOUNT%", list.size() + ""));
            return;
        }
        if (args.length == 0) {
            if (list.contains("Home")) {
                Home home = HSPlayer.getHome("Home");
                HomeTeleportEvent HTE = new HomeTeleportEvent(p, home);
                Bukkit.getPluginManager().callEvent(HTE);
                if (HTE.isCancelled()) {
                    p.sendMessage(plugin.PrimaryColor + "Your teleport was cancelled because " + HTE.getCancelReason());
                    return;
                }
                home.teleportPlayer(p);
            } else {
                p.sendMessage(plugin.HSConfig.getColoredMessage("Home.NoHomeSet"));
            }
        } else if (args.length == 1) {
            String homeName = args[0];
            Home home = HSPlayer.getHome(homeName);
            if (!HSPlayer.hasHome(homeName)) {
                if (!list.isEmpty()) {
                    p.sendMessage(plugin.HSConfig.getColoredMessage("Home.NoHomeName"));
                    p.sendMessage(plugin.HSConfig.getColoredMessage("Home.CurrentHomes"));
                    p.sendMessage(plugin.SecondaryColor + HSPlayer.getHomesList());
                } else {
                    p.sendMessage(plugin.HSConfig.getColoredMessage("Home.NoHomeSet"));
                }
            } else {
                HomeTeleportEvent HTE = new HomeTeleportEvent(p, home);
                Bukkit.getPluginManager().callEvent(HTE);
                if (HTE.isCancelled()) {
                    p.sendMessage(plugin.PrimaryColor + "Your teleport was cancelled because " + HTE.getCancelReason());
                    return;
                }
                home.teleportPlayer(p);
            }
        }
    }
}



