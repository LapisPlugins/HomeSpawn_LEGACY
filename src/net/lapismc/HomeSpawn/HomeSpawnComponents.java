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

package net.lapismc.HomeSpawn;


import net.lapismc.HomeSpawn.api.HomeSpawnConfigs;
import net.lapismc.HomeSpawn.api.HomeSpawnPlayerData;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class HomeSpawnComponents {

    private YamlConfiguration comp;


    void init(HomeSpawn plugin) {
        HomeSpawn plugin1 = plugin;
        File f = new File(plugin.getDataFolder().getAbsolutePath()
                + File.separator + "Components.yml");
        if (!f.exists()) {
            try {
                if (!f.createNewFile()) {
                    plugin.logger.info("Failed to generate " + f.getName());
                }
                comp = YamlConfiguration.loadConfiguration(f);
                comp.set("Homes", true);
                comp.set("Spawn", true);
                comp.set("HomeSpawnPassword", false);
                comp.set("Logging", false);
                comp.set("API", false);
                comp.save(f);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        comp = YamlConfiguration.loadConfiguration(f);
        plugin.HSCommand = new HomeSpawnCommand(plugin);
        HomeSpawnConfigs c = new HomeSpawnConfigs(plugin);
        c.init(plugin);
        HomeSpawnPlayerData pd = new HomeSpawnPlayerData(plugin);
        pd.init(plugin);
        if (home()) {
            plugin.getCommand("home").setExecutor(plugin.HSCommand);
            plugin.getCommand("sethome").setExecutor(plugin.HSCommand);
            plugin.getCommand("renamehome").setExecutor(plugin.HSCommand);
            plugin.getCommand("delhome").setExecutor(plugin.HSCommand);
            plugin.getCommand("homeslist").setExecutor(plugin.HSCommand);
        }
        if (spawn()) {
            plugin.getCommand("spawn").setExecutor(plugin.HSCommand);
            plugin.getCommand("setspawn").setExecutor(plugin.HSCommand);
            plugin.getCommand("delspawn").setExecutor(plugin.HSCommand);
        }
        if (password()) {
            plugin.getCommand("homepassword").setExecutor(plugin.HSCommand);
        }
        plugin.getCommand("homespawn").setExecutor(plugin.HSCommand);
        plugin.HSCommand.registerCommands();
        plugin.logger.info("Commands Registered!");
    }

    private boolean home() {
        return comp.getBoolean("Homes");
    }

    private boolean spawn() {
        return comp.getBoolean("Spawn");
    }

    private boolean password() {
        return comp.getBoolean("HomeSpawnPassword");
    }

    public boolean api() {
        return comp.getBoolean("API");
    }
}
