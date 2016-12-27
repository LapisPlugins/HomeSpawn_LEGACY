package net.lapismc.HomeSpawn;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

import java.io.*;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;

public class HomeSpawnConfiguration {

    public final HashMap<UUID, YamlConfiguration> HomeConfigs = new HashMap<>();
    public final HashMap<UUID, File> HomeConfigsFiles = new HashMap<>();
    public YamlConfiguration spawn;
    public File spawnFile;
    public YamlConfiguration messages;
    public File messagesFile;
    public YamlConfiguration passwords;
    public File passwordsFile;
    private HomeSpawn plugin;

    protected HomeSpawnConfiguration(HomeSpawn p) {
        plugin = p;
        Configs();
    }

    private void Configs() {
        File f = new File(Bukkit.getWorldContainer() + File.separator
                + "plugins" + File.separator + "Homespawn");
        if (f.exists()) {
            f.renameTo(new File(f.getParent() + File.separator + "HomeSpawn"));
        }
        plugin.saveDefaultConfig();
        createSpawn();
        createBook();
        createPlayerData();
        createMessages();
        createPasswords();
        loadPlayerData();
        loadName();
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
                loadPlayerData();
                loadName();
            }
        } else if (obj == null) {
            spawn = YamlConfiguration.loadConfiguration(spawnFile);
            messages = YamlConfiguration.loadConfiguration(messagesFile);
            passwords = YamlConfiguration.loadConfiguration(passwordsFile);
            loadPlayerData();
            loadName();
            plugin.HSPermissions.init();
            for (Permission p : plugin.HSPermissions.Permissions.keySet()) {
                if (plugin.HSPermissions.Permissions.get(p).get("reload").equals(1)) {
                    Bukkit.broadcast(ChatColor.RED + "Console" + ChatColor.GOLD + " Has Reloaded Homespawn!", p.getName());
                }
            }
            plugin.logger.info("You Have Reloaded Homespawn!");
        }
        if (player != null) {
            spawn = YamlConfiguration.loadConfiguration(spawnFile);
            messages = YamlConfiguration.loadConfiguration(messagesFile);
            passwords = YamlConfiguration.loadConfiguration(passwordsFile);
            loadPlayerData();
            loadName();
            plugin.HSPermissions.init();
            player.sendMessage(ChatColor.GOLD
                    + "You have reloaded the configs for Homespawn!");
            for (Permission p : plugin.HSPermissions.Permissions.keySet()) {
                if (plugin.HSPermissions.Permissions.get(p).get("reload").equals(1)) {
                    Bukkit.broadcast(ChatColor.GOLD + "Player " + ChatColor.RED + player.getName() + ChatColor.GOLD
                            + " Has Reloaded Homespawn!", p.getName());
                }
            }
            plugin.logger.info("Player " + player.getName()
                    + " Has Reloaded Homespawn!");
        }
    }

    public void savePlayerData(UUID uuid) {
        try {
            HomeConfigs.get(uuid).save(HomeConfigsFiles.get(uuid));
        } catch (IOException e) {
            plugin.logger.log(Level.SEVERE, "An error has occurred while trying to save" +
                    " HomeSpawn player data");
            plugin.logger.log(Level.SEVERE, "The error follows, Please report it to dart2112");
            e.printStackTrace();
        }
    }

    public void loadPlayerData() {
        HomeConfigs.clear();
        HomeConfigsFiles.clear();
        File file = new File(plugin.getDataFolder().getAbsolutePath() + File.separator + "PlayerData");
        File[] playerdataArray = file.listFiles();
        for (File f : playerdataArray) {
            if (f.isFile() && !f.getName().contains("Passwords")) {
                YamlConfiguration Yaml = YamlConfiguration.loadConfiguration(f);
                HomeConfigs.put(UUID.fromString(Yaml.getString("name")), Yaml);
                HomeConfigsFiles.put(UUID.fromString(Yaml.getString("name")), f);
            }
        }
    }

    public void loadName() {
        plugin.PlayertoUUID.clear();
        File file = new File(plugin.getDataFolder().getAbsolutePath() + File.separator
                + "PlayerData" + File.separator + "PlayerNames");
        File[] playerNamesArray = file.listFiles();
        for (File f : playerNamesArray) {
            YamlConfiguration Yaml = YamlConfiguration.loadConfiguration(f);
            plugin.PlayertoUUID.put(Yaml.getString("Name"), UUID.fromString(Yaml.getString("UUID")));
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
            yaml.set("Book.1", " &6How To Use HomeSpawn! \n &4/home:&6 Sends You To Your Home \n &4/sethome:&6 Sets Your Home At Your Current Location \n &4/delhome:&6 Removes Your Home \n &4/spawn:&6 Sends You To Spawn \n &4/homepassword help:\n &6 Displays The Home Transfer Commands \n &2 For More Detailed Help Use /homespawn help");
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
        File theDir1 = new File(plugin.getDataFolder().getAbsolutePath()
                + File.separator + "PlayerData" + File.separator
                + "PlayerNames");
        if (!theDir.exists()) {
            plugin.logger.info("Creating PlayerData Directory!");
            theDir.mkdir();
        }
        if (!theDir1.exists()) {
            theDir1.mkdir();
        }
    }

    private void createSpawn() {
        File file = new File(plugin.getDataFolder().getAbsolutePath()
                + File.separator + "Spawn.yml");
        FileConfiguration getSpawn = YamlConfiguration.loadConfiguration(file);
        if (!file.exists()) {
            try {
                file.createNewFile();
                getSpawn.createSection("spawn.X");
                getSpawn.createSection("spawn.Y");
                getSpawn.createSection("spawn.Z");
                getSpawn.createSection("spawn.World");
                getSpawn.createSection("spawn.Yaw");
                getSpawn.createSection("spawn.Pitch");
                getSpawn.createSection("spawnnew.X");
                getSpawn.createSection("spawnnew.Y");
                getSpawn.createSection("spawnnew.Z");
                getSpawn.createSection("spawnnew.World");
                getSpawn.createSection("spawnnew.Yaw");
                getSpawn.createSection("spawnnew.Pitch");
                getSpawn.save(file);
            } catch (IOException e) {
                plugin.logger.log(Level.SEVERE, "An error has occurred while trying to save HomeSpawn spawn data");
                plugin.logger.log(Level.SEVERE, "The error follows, Please report it to dart2112");
                e.printStackTrace();
            }
        }
        spawn = YamlConfiguration.loadConfiguration(file);
        spawnFile = file;
    }


    private void configVersion() {
        if (plugin.getConfig().getInt("ConfigVersion") != 7) {
            File oldConfig = new File(plugin.getDataFolder() + File.separator + "config.yml");
            File backupConfig = new File(plugin.getDataFolder() + File.separator +
                    "Backup_config.yml");
            oldConfig.renameTo(backupConfig);
            plugin.saveDefaultConfig();
            plugin.logger.info("New config generated!");
            plugin.logger.info("Please transfer values!");
        }
    }


}
