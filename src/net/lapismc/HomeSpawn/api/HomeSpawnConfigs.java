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

package net.lapismc.HomeSpawn.api;

import net.lapismc.HomeSpawn.HomeSpawn;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.IOException;

/**
 * api Class to get Config Files
 *
 * @author Dart2112
 */
public class HomeSpawnConfigs {

    private static HomeSpawn plugin;

    public void init(HomeSpawn p) {
        plugin = p;
    }

    /**
     * Reloads all configs from disk
     */
    public void reloadConfigs(Plugin p) {
        plugin.HSConfig.reload("Silent");
    }

    /**
     * Returns the currently loaded HomeSpawnSpawn.yml
     */
    public YamlConfiguration getSpawnConfig(Plugin p) {
        return plugin.HSConfig.spawn;
    }

    /**
     * Saves the given YamlConfiguration as the HomeSpawnSpawn.yml file
     *
     * @throws IOException Caused by saving the config
     */
    public void saveSpawnConfig(Plugin p, YamlConfiguration SpawnConfig) throws IOException {
        plugin.HSConfig.spawn = SpawnConfig;
        plugin.HSConfig.spawn.save(plugin.HSConfig.spawnFile);
        plugin.HSConfig.reload("Silent");
    }


}
