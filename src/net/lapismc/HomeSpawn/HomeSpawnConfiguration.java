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
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class HomeSpawnConfiguration {

    public YamlConfiguration spawn;
    public File spawnFile;
    private HashMap<UUID, YamlConfiguration> HomeConfigs = new HashMap<>();
    private YamlConfiguration messages;
    private File messagesFile;
    private File passwordsFile;
    private HomeSpawn plugin;

    HomeSpawnConfiguration(HomeSpawn p) {
        plugin = p;
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> HomeConfigs.clear(), 20 * 60 * 5, 20 * 60 * 5);
        Configs();
        updatePlayerData();
    }

    private void updatePlayerData() {
        if (plugin.getConfig().getInt("ConfigVersion") >= 8) {
            File f = new File(plugin.getDataFolder().getAbsolutePath() + File.separator + "PlayerData");
            File[] fa = f.listFiles();
            int i = 0;
            assert fa != null;
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

    void unloadPlayerData(UUID uuid) {
        YamlConfiguration getHomes = getPlayerData(uuid);
        try {
            getHomes.save(new File(plugin.getDataFolder().getAbsolutePath() + File.separator + "PlayerData" + File.separator + uuid.toString() + ".yml"));
            HomeConfigs.remove(uuid);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void generateNewPlayerData(File f, Player player) {
        try {
            if (!f.createNewFile()) {
                plugin.logger.severe("Failed to generate player data file for " + player.getName());
                return;
            }
            YamlConfiguration getHomes = YamlConfiguration.loadConfiguration(f);
            getHomes.set("UUID", player.getUniqueId().toString());
            getHomes.set("UserName", player.getName());
            getHomes.set("Permission", "-");
            getHomes.set("login", System.currentTimeMillis());
            getHomes.set("logout", "-");
            getHomes.set("Homes.list", new ArrayList<String>());
            getHomes.save(f);
            plugin.spawnNew(player);
            if (plugin.getConfig().getBoolean("CommandBook")) {
                PlayerInventory pi = player.getInventory();
                HomeSpawnBook book = new HomeSpawnBook(plugin);
                ItemStack commandBook = book.getBook();
                pi.addItem(commandBook);
            }
        } catch (IOException e) {
            e.printStackTrace();
            plugin.logger
                    .severe("[HomeSpawn] Player Data File Creation Failed!");
        }
    }

    public String getColoredMessage(String path) {
        return ChatColor.translateAlternateColorCodes('&', messages.getString(path).replace("&p", plugin.PrimaryColor).replace("&s", plugin.SecondaryColor));
    }

    @SuppressWarnings("SameParameterValue")
    String getMessage(String path) {
        return ChatColor.stripColor(getColoredMessage(path));
    }

    private void Configs() {
        File f = new File(plugin.getDataFolder().getParent() + File.separator + "Homespawn");
        if (f.exists()) {
            if (!f.renameTo(new File(f.getParent() + File.separator + "HomeSpawn"))) {
                plugin.logger.info("Failed to generate new config.yml");
            }
        }
        plugin.saveDefaultConfig();
        createSpawn();
        createBook();
        createPlayerData();
        createMessages();
        createPasswords();
        configVersion();
        plugin.PrimaryColor = ChatColor.translateAlternateColorCodes('&', messages.getString("PrimaryColor", ChatColor.GOLD.toString()));
        plugin.SecondaryColor = ChatColor.translateAlternateColorCodes('&', messages.getString("SecondaryColor", ChatColor.RED.toString()));
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
                YamlConfiguration.loadConfiguration(passwordsFile);
            }
        } else if (obj == null) {
            spawn = YamlConfiguration.loadConfiguration(spawnFile);
            messages = YamlConfiguration.loadConfiguration(messagesFile);
            YamlConfiguration.loadConfiguration(passwordsFile);
            plugin.logger.info("You Have Reloaded Homespawn!");
        }
        if (player != null) {
            spawn = YamlConfiguration.loadConfiguration(spawnFile);
            messages = YamlConfiguration.loadConfiguration(messagesFile);
            YamlConfiguration.loadConfiguration(passwordsFile);
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
                if (!file.createNewFile()) {
                    plugin.logger.info("Faile to generate " + file.getName());
                }
            } catch (IOException e) {
                plugin.logger.log(Level.SEVERE, "An error has occurred while trying to" +
                        " save HomeSpawn player password data");
                plugin.logger.log(Level.SEVERE, "The error follows, Please report it to dart2112");
                e.printStackTrace();
            }
        }
        YamlConfiguration.loadConfiguration(file);
        passwordsFile = file;
    }

    private void createBook() {
        File f = new File(plugin.getDataFolder() + File.separator + "HomeSpawnBook.yml");
        if (f.exists()) {
            return;
        }
        try {
            if (!f.createNewFile()) {
                plugin.logger.info("Failed to generate " + f.getName());
            }
            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(f);
            yaml.set("Title", "&6How To HomeSpawn!");
            yaml.set("Author", "&bDart2112");
            yaml.set("Book.NumbOfPages", 1);
            yaml.set("Book.1", " &6How To Use HomeSpawn! \n &4/home:&6 Sends You To Your HomeSpawnHome \n &4/sethome:&6 Sets Your HomeSpawnHome At Your Current Location \n &4/delhome:&6 Removes Your HomeSpawnHome \n &4/spawn:&6 Sends You To HomeSpawnSpawn \n &4/homepassword help:\n &6 Displays The HomeSpawnHome Transfer Commands \n &2 For More Detailed Help Use /homespawn help");
            yaml.set("Book.2", "This will only be on page 2 if the page number is 2");
            yaml.save(f);
        } catch (IOException e) {
            e.printStackTrace();
            plugin.logger.severe("Failed to generate the HomeSpawnBook.yml file!");
        }
    }

    void createMessages() {
        messagesFile = new File(plugin.getDataFolder().getAbsolutePath() + File.separator + "Messages.yml");
        if (!messagesFile.exists()) {
            InputStream is;
            OutputStream os;
            try {
                if (!messagesFile.createNewFile()) {
                    plugin.logger.info("Failed to generate " + messagesFile.getName());
                }
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

    void reloadMessages() {
        messages = YamlConfiguration.loadConfiguration(messagesFile);
    }

    private void createPlayerData() {
        File theDir = new File(plugin.getDataFolder().getAbsolutePath()
                + File.separator + "PlayerData");
        if (!theDir.exists()) {
            plugin.logger.info("Creating HomeSpawn PlayerData Directory!");
            if (!theDir.mkdir()) {
                plugin.logger.info("Failed to generate " + theDir.getName());
            }
        }
    }

    void createSpawn() {
        File file = new File(plugin.getDataFolder().getAbsolutePath()
                + File.separator + "Spawn.yml");
        if (!file.exists()) {
            try {
                if (!file.createNewFile()) {
                    plugin.logger.info("Failed to generate " + file.getName());
                }
            } catch (IOException e) {
                plugin.logger.log(Level.SEVERE, "An error has occurred while trying to load HomeSpawn spawn data");
                plugin.logger.log(Level.SEVERE, "The error follows, Please report it to dart2112");
                e.printStackTrace();
            }
        }
        spawn = YamlConfiguration.loadConfiguration(file);
        spawnFile = file;
    }


    void reloadSpawn() {
        spawn = YamlConfiguration.loadConfiguration(spawnFile);
    }

    private void configVersion() {
        if (plugin.getConfig().getInt("ConfigVersion") != 9) {
            File oldConfig = new File(plugin.getDataFolder() + File.separator + "config.yml");
            File backupConfig = new File(plugin.getDataFolder() + File.separator +
                    "Backup_config.yml");
            if (!oldConfig.renameTo(backupConfig)) {
                plugin.logger.info("Failed to generate new config.yml");
            }
            plugin.saveDefaultConfig();
            plugin.logger.info("New config generated!");
            plugin.logger.info("Please transfer values!");
        }
    }


}
