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

package net.lapismc.HomeSpawn;

import net.lapismc.HomeSpawn.commands.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

class HomeSpawnCommand implements TabCompleter {

    private final HomeSpawn plugin;

    HomeSpawnCommand(HomeSpawn plugin) {
        List<String> disabledCommands = plugin.getConfig().getStringList("DisabledCommands");
        this.plugin = plugin;
        if (!disabledCommands.contains("homespawn"))
            new net.lapismc.HomeSpawn.commands.HomeSpawn(plugin);
        if (!disabledCommands.contains("delhome"))
            new HomeSpawnDelHome(plugin);
        if (!disabledCommands.contains("delspawn"))
            new HomeSpawnDelSpawn(plugin);
        if (!disabledCommands.contains("home"))
            new HomeSpawnHome(plugin);
        if (!disabledCommands.contains("homeslist"))
            new HomeSpawnHomesList(plugin);
        if (!disabledCommands.contains("sethome"))
            new HomeSpawnSetHome(plugin);
        if (!disabledCommands.contains("renamehome"))
            new HomeSpawnRenameHome(plugin);
        if (!disabledCommands.contains("setspawn"))
            new HomeSpawnSetSpawn(plugin);
        if (!disabledCommands.contains("spawn"))
            new HomeSpawnSpawn(plugin);
        if (!disabledCommands.contains("homepassword"))
            new HomeSpawnHomePassword(plugin);
        plugin.logger.info("Commands Registered!");
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String alias,
                                      String[] args) {
        //checks if a player is attempting to tab complete a home name
        if (command.getName().equalsIgnoreCase("home") || command.getName().equalsIgnoreCase("delhome")) {
            Player p = (Player) sender;
            YamlConfiguration playerData = plugin.getPlayer(p.getUniqueId()).getConfig(false);
            //Gets the list of the players homes and returns it for the tab complete to deal with
            List<String> l = new ArrayList<>();
            for (String home : playerData.getStringList("Homes.list")) {
                if (args.length > 0) {
                    if (home.toLowerCase().startsWith(args[0].toLowerCase())) {
                        l.add(home);
                    }
                } else {
                    l.add(home);
                }
            }
            plugin.debug("Tab Completed for " + sender.getName());
            return l;
        }
        return null;
    }
}