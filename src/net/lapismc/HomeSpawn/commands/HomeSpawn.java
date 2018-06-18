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

import net.lapismc.HomeSpawn.HomeSpawnPermissions;
import net.lapismc.HomeSpawn.playerdata.HomeSpawnPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class HomeSpawn extends LapisCommand {

    private final net.lapismc.HomeSpawn.HomeSpawn plugin;
    private final net.lapismc.HomeSpawn.commands.HomeSpawnPlayer homeSpawnPlayer;

    public HomeSpawn(net.lapismc.HomeSpawn.HomeSpawn p) {
        super("homespawn", "Shows plugin information", new ArrayList<>());
        this.plugin = p;
        this.homeSpawnPlayer = new net.lapismc.HomeSpawn.commands.HomeSpawnPlayer(plugin);
    }

    @Override
    public void onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("player")) {
            homeSpawnPlayer.homeSpawnPlayer(args, sender);
            return;
        }
        if (args.length == 0) {
            sender.sendMessage(plugin.SecondaryColor + "---------------"
                    + plugin.PrimaryColor + "Homespawn" + plugin.SecondaryColor
                    + "---------------");
            sender.sendMessage(plugin.PrimaryColor + "Author:"
                    + plugin.SecondaryColor + " Dart2112");
            sender.sendMessage(plugin.PrimaryColor + "Version: "
                    + plugin.SecondaryColor
                    + plugin.getDescription().getVersion());
            sender.sendMessage(plugin.PrimaryColor + "Spigot:"
                    + plugin.SecondaryColor + " https://goo.gl/aWby6W");
            sender.sendMessage(plugin.PrimaryColor
                    + "Use /homespawn Help For Commands!");
            sender.sendMessage(plugin.SecondaryColor
                    + "-----------------------------------------");
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("reload")) {
                boolean isPermitted;
                if (sender instanceof Player) {
                    Player p = (Player) sender;
                    HomeSpawnPlayer HSPlayer = new HomeSpawnPlayer(plugin, p.getUniqueId());
                    isPermitted = HSPlayer.isPermitted(HomeSpawnPermissions.perm.reload);
                } else {
                    isPermitted = true;
                }
                if (isPermitted) {
                    this.plugin.HSConfig.reload(sender);
                } else {
                    sender.sendMessage(plugin.SecondaryColor
                            + "You Don't Have Permission To Do That");
                }
            } else if (args[0].equalsIgnoreCase("help")) {
                this.plugin.help(sender);
            } else if (args[0].equalsIgnoreCase("update")) {
                boolean isPermitted;
                if (sender instanceof Player) {
                    Player p = (Player) sender;
                    HomeSpawnPlayer HSPlayer = new HomeSpawnPlayer(plugin, p.getUniqueId());
                    isPermitted = HSPlayer.isPermitted(HomeSpawnPermissions.perm.updateNotify);
                } else {
                    isPermitted = true;
                }
                if (isPermitted) {
                    if (plugin.lapisUpdater.checkUpdate()) {
                        sender.sendMessage(plugin.PrimaryColor + "Update found, Downloading it now\n it will be installed on next server restart");
                        plugin.lapisUpdater.downloadUpdate();
                    } else {
                        sender.sendMessage(plugin.PrimaryColor + "No updates found");
                    }
                } else {
                    sender.sendMessage(plugin.HSConfig.getColoredMessage("NoPerms"));
                }
            }
        }
    }
}
