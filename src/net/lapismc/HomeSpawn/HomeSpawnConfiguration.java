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

import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class HomeSpawnConfiguration {

    public YamlConfiguration spawn;
    public File spawnFile;
    protected File teleLogFile;
    protected YamlConfiguration teleLog;
    protected File setsAndDelsFile;
    protected YamlConfiguration setsAndDels;
    private HashMap<UUID, YamlConfiguration> HomeConfigs = new HashMap<>();
    private YamlConfiguration messages;
    private File messagesFile;
    private YamlConfiguration passwords;
    private File passwordsFile;
    private HomeSpawn plugin;

    protected HomeSpawnConfiguration(HomeSpawn p) {
        plugin = p;
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                HomeConfigs.clear();
            }
        }, 20 * 60 * 5, 20 * 60 * 5);
        Configs();
        updatePlayerData();
    }

    protected void updatePlayerData() {
        if (plugin.getConfig().getInt("ConfigVersion") >= 8) {
            File f = new File(plugin.getDataFolder().getAbsolutePath() + File.separator + "PlayerData");
            File[] fa = f.listFiles();
            int i = 0;
            for (File f0 : fa) {
                if (f0.isDirectory()) {
                    return;
                } else {
                    YamlConfiguration Homes = YamlConfiguration.loadConfiguration(f0);
                    if (Homes.contains("HasHome")) {
                        i++;
                        UUID uuid = UUID.fromString(Homes.getString("name"));
                        Long logout = Homes.getLong("login");
                        List<String> homesList = Homes.getStringList(uuid.toString() + ".list");
                        HashMap<String, Location> homesLocList = new HashMap<>();
                        for (String homeName : homesList) {
                            try {
                                if (homeName.equals("Home")) {
                                    World world = Bukkit.getWorld(Homes.getString(uuid.toString() + ".world"));
                                    Location loc = new Location(world, Homes.getInt(uuid.toString() + ".x"),
                                            Homes.getInt(uuid.toString() + ".y"), Homes.getInt(uuid.toString() + ".z"),
                                            Float.parseFloat(Homes.getString(uuid.toString() + ".Yaw")),
                                            Float.parseFloat(Homes.getString(uuid.toString() + ".Pitch")));
                                    homesLocList.put(homeName, loc);
                                } else {
                                    World world = Bukkit.getWorld(Homes.getString(homeName + ".world"));
                                    Location loc = new Location(world, Homes.getInt(homeName + ".x"),
                                            Homes.getInt(uuid.toString() + ".y"), Homes.getInt(homeName + ".z"),
                                            Float.parseFloat(Homes.getString(homeName + ".Yaw")),
                                            Float.parseFloat(Homes.getString(homeName + ".Pitch")));
                                    homesLocList.put(homeName, loc);
                                }
                            } catch (IllegalArgumentException e) {
                                e.printStackTrace();
                                plugin.logger.severe("Failed to update to new " +
                                        "PlayerData format for file " + f0.getName());
                            }
                        }
                        YamlConfiguration newHomes = new YamlConfiguration();
                        OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
                        newHomes.set("UUID", uuid.toString());
                        newHomes.set("UserName", op.getName());
                        newHomes.set("Permission", "-");
                        newHomes.set("login", "-");
                        newHomes.set("logout", logout);
                        newHomes.set("Homes.list", homesList);
                        for (String homeName : homesLocList.keySet()) {
                            Location loc = homesLocList.get(homeName);
                            newHomes.set("Homes." + homeName, loc);
                        }
                        savePlayerData(uuid, newHomes);
                    }
                }
            }
            plugin.logger.info(i + " Player Data files updated to the new layout");
        }
        if (spawn.contains("spawn.SpawnSet")) {
            try {
                Location loc = new Location(Bukkit.getWorld(spawn.getString("spawn.World")), spawn.getInt("spawn.X"), spawn.getInt("spawn.Y"), spawn.getInt("spawn.Z"),
                        Float.parseFloat(spawn.getString("spawn.Yaw")), Float.parseFloat(spawn.getString("spawn.Pitch")));
                spawn.set("spawn", loc);
                spawn.save(spawnFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (spawn.contains("spawnnew.SpawnSet")) {
            try {
                Location loc = new Location(Bukkit.getWorld(spawn.getString("spawnnew.World")), spawn.getInt("spawnnew.X"),
                        spawn.getInt("spawnnew.Y"), spawn.getInt("spawnnew.Z"),
                        Float.parseFloat(spawn.getString("spawnnew.Yaw")), Float.parseFloat(spawn.getString("spawnnew.Pitch")));
                spawn.set("spawnnew", loc);
                spawn.save(spawnFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public YamlConfiguration getPlayerData(UUID uuid) {
        YamlConfiguration yaml;
        if (!HomeConfigs.containsKey(uuid) || HomeConfigs.get(uuid) == null) {
            File f = new File(plugin.getDataFolder().getAbsolutePath() + File.separator + "PlayerData" + File.separator + uuid.toString() + ".yml");
            yaml = YamlConfiguration.loadConfiguration(f);
            HomeConfigs.put(uuid, yaml);
        } else {
            yaml = HomeConfigs.get(uuid);
        }
        return yaml;
    }

    public void unloadPlayerData(UUID uuid) {
        YamlConfiguration getHomes = getPlayerData(uuid);
        try {
            getHomes.save(new File(plugin.getDataFolder().getAbsolutePath() + File.separator + "PlayerData" + File.separator + uuid.toString() + ".yml"));
            HomeConfigs.remove(uuid);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getColoredMessage(String path) {
        return ChatColor.translateAlternateColorCodes('&', messages.getString(path));
    }

    public String getMessage(String path) {
        return ChatColor.stripColor(getColoredMessage(path));
    }

    public void log(logType type, Player p, String name) {
        switch (type) {
            case Set:
                List<String> sets;
                if (setsAndDels.contains("Sets")) {
                    sets = setsAndDels.getStringList("Sets");
                } else {
                    sets = new ArrayList<>();
                }
                sets.add("Player " + p.getName() + " has set a home named " + name);
                setsAndDels.set("Sets", sets);
                break;
            case Delete:
                List<String> dels;
                if (setsAndDels.contains("Dels")) {
                    dels = setsAndDels.getStringList("Dels");
                } else {
                    dels = new ArrayList<>();
                }
                dels.add("Player " + p.getName() + " has deleted a home named " + name);
                setsAndDels.set("Dels", dels);
                break;
            case TeleportHome:
                List<String> homeTeleports;
                if (teleLog.contains("HomeTeleports")) {
                    homeTeleports = teleLog.getStringList("HomeTeleports");
                } else {
                    homeTeleports = new ArrayList<>();
                }
                homeTeleports.add("Player " + p.getName() + " has teleported to their home named" + name);
                teleLog.set("HomeTeleports", homeTeleports);
                break;
            case TeleportSpawn:
                List<String> spawnTeleports;
                if (teleLog.contains("SpawnTeleports")) {
                    spawnTeleports = teleLog.getStringList("SpawnTeleports");
                } else {
                    spawnTeleports = new ArrayList<>();
                }
                spawnTeleports.add("Player " + p.getName() + " has teleported to spawn");
                teleLog.set("SpawnTeleports", spawnTeleports);
                break;
        }
    }

    public void saveLogs() {
        if (plugin.HSComponents.logging()) {
            try {
                teleLog.save(teleLogFile);
                setsAndDels.save(setsAndDelsFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void Configs() {
        File f = new File(plugin.getDataFolder().getParent() + File.separator + "Homespawn");
        if (f.exists()) {
            f.renameTo(new File(f.getParent() + File.separator + "HomeSpawn"));
        }
        plugin.saveDefaultConfig();
        createSpawn();
        createBook();
        createPlayerData();
        createMessages();
        createPasswords();
        configVersion();
    }

    public void reload(Object obj) {
        Player player = null;
        if (obj instanceof Player) {
            player = (Player) obj;
        } else if (obj instanceof String) {
            String s = (String) obj;
            if (s.equalsIgnoreCase("Silent")) {
                spawn = YamlConfiguration.loadConfiguration(spawnFile);
                messages = YamlConfiguration.loadConfiguration(messagesFile);
                passwords = YamlConfiguration.loadConfiguration(passwordsFile);
            }
        } else if (obj == null) {
            spawn = YamlConfiguration.loadConfiguration(spawnFile);
            messages = YamlConfiguration.loadConfiguration(messagesFile);
            passwords = YamlConfiguration.loadConfiguration(passwordsFile);
            plugin.logger.info("You Have Reloaded Homespawn!");
        }
        if (player != null) {
            spawn = YamlConfiguration.loadConfiguration(spawnFile);
            messages = YamlConfiguration.loadConfiguration(messagesFile);
            passwords = YamlConfiguration.loadConfiguration(passwordsFile);
            player.sendMessage(ChatColor.GOLD + "You have reloaded the configs for Homespawn!");
            plugin.logger.info("Player " + player.getName() + " Has Reloaded Homespawn!");
        }
    }

    public void savePlayerData(UUID uuid, YamlConfiguration yaml) {
        try {
            File file = new File(plugin.getDataFolder() + File.separator + "PlayerData" + File.separator + uuid.toString() + ".yml");
            yaml.save(file);
        } catch (IOException e) {
            plugin.logger.log(Level.SEVERE, "An error has occurred while trying to save" +
                    " HomeSpawn player data");
            plugin.logger.log(Level.SEVERE, "The error follows, Please report it to dart2112");
            e.printStackTrace();
        }
    }

    private void createPasswords() {
        File file = new File(plugin.getDataFolder().getAbsolutePath()
                + File.separator + "PlayerData" + File.separator
                + "Passwords.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                plugin.logger.log(Level.SEVERE, "An error has occurred while trying to" +
                        " save HomeSpawn player password data");
                plugin.logger.log(Level.SEVERE, "The error follows, Please report it to dart2112");
                e.printStackTrace();
            }
        }
        passwords = YamlConfiguration.loadConfiguration(file);
        passwordsFile = file;
    }

    private void createBook() {
        File f = new File(plugin.getDataFolder() + File.separator + "HomeSpawnBook.yml");
        if (f.exists()) {
            return;
        }
        try {
            f.createNewFile();
            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(f);
            yaml.set("Title", "&6How To HomeSpawn!");
            yaml.set("Book.NumbOfPages", 1);
            yaml.set("Book.1", " &6How To Use HomeSpawn! \n &4/home:&6 Sends You To Your HomeSpawnHome \n &4/sethome:&6 Sets Your HomeSpawnHome At Your Current Location \n &4/delhome:&6 Removes Your HomeSpawnHome \n &4/spawn:&6 Sends You To HomeSpawnSpawn \n &4/homepassword help:\n &6 Displays The HomeSpawnHome Transfer Commands \n &2 For More Detailed Help Use /homespawn help");
            yaml.set("Book.2", "This will only be on page 2 if the page number is 2");
            yaml.save(f);
        } catch (IOException e) {
            e.printStackTrace();
            plugin.logger.severe("Failed to generate the HomeSpawnBook.yml file!");
        }
    }

    private void createMessages() {
        messagesFile = new File(plugin.getDataFolder().getAbsolutePath() + File.separator + "Messages.yml");
        if (!messagesFile.exists()) {
            InputStream is = null;
            OutputStream os = null;
            try {
                messagesFile.createNewFile();
                is = plugin.getResource("Messages.yml");
                int readBytes;
                byte[] buffer = new byte[4096];
                os = new FileOutputStream(messagesFile);
                while ((readBytes = is.read(buffer)) > 0) {
                    os.write(buffer, 0, readBytes);
                }
                is.close();
                os.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        messages = YamlConfiguration.loadConfiguration(messagesFile);
    }

    private void createPlayerData() {
        File theDir = new File(plugin.getDataFolder().getAbsolutePath()
                + File.separator + "PlayerData");
        if (!theDir.exists()) {
            plugin.logger.info("Creating HomeSpawn PlayerData Directory!");
            theDir.mkdir();
        }
    }

    private void createSpawn() {
        File file = new File(plugin.getDataFolder().getAbsolutePath()
                + File.separator + "Spawn.yml");
        FileConfiguration getSpawn = YamlConfiguration.loadConfiguration(file);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                plugin.logger.log(Level.SEVERE, "An error has occurred while trying to load HomeSpawn spawn data");
                plugin.logger.log(Level.SEVERE, "The error follows, Please report it to dart2112");
                e.printStackTrace();
            }
        }
        spawn = YamlConfiguration.loadConfiguration(file);
        spawnFile = file;
    }

    private void configVersion() {
        if (plugin.getConfig().getInt("ConfigVersion") != 9) {
            File oldConfig = new File(plugin.getDataFolder() + File.separator + "config.yml");
            File backupConfig = new File(plugin.getDataFolder() + File.separator +
                    "Backup_config.yml");
            oldConfig.renameTo(backupConfig);
            plugin.saveDefaultConfig();
            plugin.logger.info("New config generated!");
            plugin.logger.info("Please transfer values!");
        }
    }

    public enum logType {
        Set, Delete, TeleportHome, TeleportSpawn;

        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }
    }


}
