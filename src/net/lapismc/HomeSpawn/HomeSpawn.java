package net.lapismc.HomeSpawn;

import net.lapismc.HomeSpawn.Metrics.Graph;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Inventory;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HomeSpawn extends JavaPlugin {

    public final Logger logger = getLogger();
    public final HashMap<UUID, YamlConfiguration> HomeConfigs = new HashMap<>();
    public final HashMap<String, UUID> PlayertoUUID = new HashMap<>();
    public final HashMap<Permission, HashMap<String, Integer>> Permissions = new HashMap<>();
    public final HashMap<UUID, Permission> PlayerPermission = new HashMap<>();
    final HashMap<Player, Location> HomeSpawnLocations = new HashMap<>();
    final HashMap<Player, Integer> HomeSpawnTimeLeft = new HashMap<>();
    final HashMap<Player, Inventory> HomesListInvs = new HashMap<>();
    private final HashMap<UUID, File> HomeConfigsFiles = new HashMap<>();
    public HomeSpawn plugin;
    public LapisUpdater updater;
    public YamlConfiguration spawn;
    public File spawnFile;
    public YamlConfiguration messages;
    public File messagesFile;
    public YamlConfiguration passwords;
    public File passwordsFile;
    private HomeSpawnListener pl;

    @Override
    public void onEnable() {
        Enable();
        Configs();
        Update(this);
        Permissions();
        Commands();
        CommandDelay();
        Metrics();
    }

    protected void Permissions() {
        HomeSpawnPermissions hsp = new HomeSpawnPermissions(this);
        hsp.init();
    }

    private void Metrics() {
        try {
            Metrics metrics = new Metrics(this);
            Graph averageHomesGraph = metrics.createGraph("Average Number Of Homes");
            int homes = 0;
            int files = HomeConfigs.size();
            for (YamlConfiguration yaml : HomeConfigs.values()) {
                homes = homes + yaml.getInt(yaml.getString("name") + ".Numb");
            }
            int average;
            if (files != 0) {
                average = homes % files == 0 ? homes / files : homes / files + 1;
            } else {
                average = 0;
            }
            averageHomesGraph.addPlotter(new Metrics.Plotter(average + "") {
                @Override
                public int getValue() {
                    return 1;
                }
            });
            metrics.start();
            debug("Send stats to metrics");
        } catch (IOException e) {
            this.logger.log(Level.SEVERE, "An error has occurred while trying to" +
                    " start HomeSpawn metrics");
            this.logger.log(Level.SEVERE, "The error follows, Please report it to dart2112");
            e.printStackTrace();
        }
    }

    private void Update(final HomeSpawn p) {
        Bukkit.getScheduler().runTaskAsynchronously(this, new Runnable() {
            @Override
            public void run() {
                updater = new LapisUpdater(p);
                String ID = getConfig().getBoolean("BetaVersions") == true ? "beta" : "stable";
                if (updater.checkUpdate(ID)) {
                    if (getConfig().getBoolean("UpdateNotification") && !getConfig()
                            .getBoolean("DownloadUpdates")) {
                        logger.info("An update for HomeSpawn is available and can be" +
                                " downloaded and installed by running /homespawn update");
                    } else if (getConfig().getBoolean("DownloadUpdates")) {
                        updater.downloadUpdate(ID);
                        logger.info("Downloading Homespawn update, it will be installed " +
                                "on next restart!");
                    }
                } else {
                    if (getConfig().getBoolean("UpdateNotification")) {
                        logger.info("No Update Available");
                    }
                }
            }
        });
    }

    @Override
    public void onDisable() {
        Disable();
    }

    private void Enable() {
        logger.info("V." + getDescription().getVersion()
                + " Has Been Enabled!");
        PluginManager pm = getServer().getPluginManager();
        pl = new HomeSpawnListener(this);
        pm.registerEvents(pl, this);
    }

    private void configVersion() {
        if (getConfig().getInt("ConfigVersion") != 6) {
            File oldConfig = new File(this.getDataFolder() + File.separator + "config.yml");
            File backupConfig = new File(this.getDataFolder() + File.separator +
                    "Backup_config.yml");
            oldConfig.renameTo(backupConfig);
            saveDefaultConfig();
            logger.info("New config generated!");
            logger.info("Please transfer values!");
        }
    }

    private void Disable() {
        logger.info("Plugin Has Been Disabled!");
        HandlerList.unregisterAll();
    }

    private void Configs() {
        File f = new File(Bukkit.getWorldContainer() + File.separator
                + "plugins" + File.separator + "Homespawn");
        if (f.exists()) {
            f.renameTo(new File(f.getParent() + File.separator + "HomeSpawn"));
        }
        saveDefaultConfig();
        createSpawn();
        createBook();
        createPlayerData();
        createMessages();
        createPasswords();
        loadPlayerData();
        loadName();
        configVersion();
    }

    public void savePlayerData(UUID uuid) {
        try {
            HomeConfigs.get(uuid).save(HomeConfigsFiles.get(uuid));
        } catch (IOException e) {
            this.logger.log(Level.SEVERE, "An error has occurred while trying to save" +
                    " HomeSpawn player data");
            this.logger.log(Level.SEVERE, "The error follows, Please report it to dart2112");
            e.printStackTrace();
        }
    }

    public void loadPlayerData() {
        HomeConfigs.clear();
        HomeConfigsFiles.clear();
        File file = new File(getDataFolder().getAbsolutePath() + File.separator + "PlayerData");
        File[] playerdataArray = file.listFiles();
        for (File f : playerdataArray) {
            if (f.isFile()) {
                if (!f.getName().contains("Passwords")) {
                    YamlConfiguration Yaml = YamlConfiguration.loadConfiguration(f);
                    HomeConfigs.put(UUID.fromString(Yaml.getString("name")), Yaml);
                    HomeConfigsFiles.put(UUID.fromString(Yaml.getString("name")), f);
                }
            }
        }
    }

    public void loadName() {
        PlayertoUUID.clear();
        File file = new File(getDataFolder().getAbsolutePath() + File.separator
                + "PlayerData" + File.separator + "PlayerNames");
        File[] playerNamesArray = file.listFiles();
        for (File f : playerNamesArray) {
            YamlConfiguration Yaml = YamlConfiguration.loadConfiguration(f);
            PlayertoUUID.put(Yaml.getString("Name"), UUID.fromString(Yaml.getString("UUID")));
        }
    }

    private void createPasswords() {
        File file = new File(getDataFolder().getAbsolutePath()
                + File.separator + "PlayerData" + File.separator
                + "Passwords.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                this.logger.log(Level.SEVERE, "An error has occurred while trying to" +
                        " save HomeSpawn player password data");
                this.logger.log(Level.SEVERE, "The error follows, Please report it to dart2112");
                e.printStackTrace();
            }
        }
        passwords = YamlConfiguration.loadConfiguration(file);
        passwordsFile = file;
    }

    private void createBook() {
        File f = new File(getDataFolder() + File.separator + "HomeSpawnBook.yml");
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
            logger.severe("Failed to generate the HomeSpawnBook.yml file!");
        }
    }

    private void createMessages() {
        messagesFile = new File(getDataFolder().getAbsolutePath() + File.separator + "Messages.yml");
        if (!messagesFile.exists()) {
            InputStream is = null;
            OutputStream os = null;
            try {
                messagesFile.createNewFile();
                is = getResource("Messages.yml");
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
        File theDir = new File(getDataFolder().getAbsolutePath()
                + File.separator + "PlayerData");
        File theDir1 = new File(getDataFolder().getAbsolutePath()
                + File.separator + "PlayerData" + File.separator
                + "PlayerNames");
        if (!theDir.exists()) {
            logger.info("Creating PlayerData Directory!");
            theDir.mkdir();
        }
        if (!theDir1.exists()) {
            theDir1.mkdir();
        }
    }

    private void createSpawn() {
        File file = new File(getDataFolder().getAbsolutePath()
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
                this.logger.log(Level.SEVERE, "An error has occurred while trying to save HomeSpawn spawn data");
                this.logger.log(Level.SEVERE, "The error follows, Please report it to dart2112");
                e.printStackTrace();
            }
        }
        spawn = YamlConfiguration.loadConfiguration(file);
        spawnFile = file;
    }

    public void spawnNew(Player player) {
        if (spawn.get("spawnnew.SpawnSet") != null
                && spawn.getString("spawnnew.SpawnSet").equalsIgnoreCase(
                "yes")) {
            int x = spawn.getInt("spawnnew.X");
            int y = spawn.getInt("spawnnew.Y");
            int z = spawn.getInt("spawnnew.Z");
            float yaw = spawn.getInt("spawnnew.Yaw");
            float pitch = spawn.getInt("spawnnew.Pitch");
            String cworld = spawn.getString("spawnnew.World");
            World world = getServer().getWorld(cworld);
            Location spawnnew = new Location(world, x, y, z, yaw, pitch);
            spawnnew.add(0.5, 0, 0.5);
            player.teleport(spawnnew);
            logger.info("Player " + player.getName()
                    + " Was Sent To New Spawn");
        } else {
            logger.info("There Is No New Spawn Set And Therefore The Player Wasn't Sent To The New Spawn");
        }
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
            Permissions();
            for (Permission p : Permissions.keySet()) {
                if (Permissions.get(p).get("reload").equals(1)) {
                    Bukkit.broadcast(ChatColor.RED + "Console" + ChatColor.GOLD + " Has Reloaded Homespawn!", p.getName());
                }
            }
            logger.info("You Have Reloaded Homespawn!");
        }
        if (player != null) {
            spawn = YamlConfiguration.loadConfiguration(spawnFile);
            messages = YamlConfiguration.loadConfiguration(messagesFile);
            passwords = YamlConfiguration.loadConfiguration(passwordsFile);
            loadPlayerData();
            loadName();
            Permissions();
            player.sendMessage(ChatColor.GOLD
                    + "You have reloaded the configs for Homespawn!");
            for (Permission p : Permissions.keySet()) {
                if (Permissions.get(p).get("reload").equals(1)) {
                    Bukkit.broadcast(ChatColor.GOLD + "Player " + ChatColor.RED + player.getName() + ChatColor.GOLD
                            + " Has Reloaded Homespawn!", p.getName());
                }
            }
            logger.info("Player " + player.getName()
                    + " Has Reloaded Homespawn!");
        }
    }

    public void help(Player player) {
        if (player != null) {
            player.sendMessage(ChatColor.GOLD + "---------------"
                    + ChatColor.RED + "Homespawn" + ChatColor.GOLD
                    + "---------------");
            if (Permissions.get(PlayerPermission.get(player.getUniqueId())).get("cHomes") == 1 &&
                    Permissions.get(PlayerPermission.get(player.getUniqueId())).get("homes") > 0) {
                player.sendMessage(ChatColor.RED + "/home [name]:" + ChatColor.GOLD
                        + " Sends You To The Home Specified");
                player.sendMessage(ChatColor.RED + "/sethome [name]:"
                        + ChatColor.GOLD
                        + " Sets Your Home At Your Current Location");
                player.sendMessage(ChatColor.RED + "/delhome [name]:"
                        + ChatColor.GOLD + " Removes The Specified Home");
            } else if (Permissions.get(PlayerPermission.get(player.getUniqueId())).get("homes") > 0) {
                player.sendMessage(ChatColor.RED + "/home:" + ChatColor.GOLD
                        + " Sends You To Your Home");
                player.sendMessage(ChatColor.RED + "/sethome:"
                        + ChatColor.GOLD
                        + " Sets Your Home At Your Current Location");
                player.sendMessage(ChatColor.RED + "/delhome:"
                        + ChatColor.GOLD + " Removes Your Home");
            }
            if (Permissions.get(PlayerPermission.get(player.getUniqueId())).get("spawn") == 1) {
                player.sendMessage(ChatColor.RED + "/spawn:" + ChatColor.GOLD
                        + " Sends You To Spawn");
            }
            if (!getServer().getOnlineMode()) {
                player.sendMessage(ChatColor.RED + "/homepassword help:"
                        + ChatColor.GOLD
                        + " Displays The Home Transfer Commands");
            }
            if (Permissions.get(PlayerPermission.get(player.getUniqueId())).get("sSpawn").equals(1)) {
                player.sendMessage(ChatColor.RED + "/setspawn:"
                        + ChatColor.GOLD + " Sets The Server Spawn");
                player.sendMessage(ChatColor.RED + "/setspawn new:"
                        + ChatColor.GOLD
                        + " All New Players Will Be Sent To This Spawn");
                player.sendMessage(ChatColor.RED + "/delspawn:"
                        + ChatColor.GOLD + " Removes The Server Spawn");
            }
            player.sendMessage(ChatColor.RED + "/homespawn:"
                    + ChatColor.GOLD + " Displays Plugin Information");
            if (Permissions.get(PlayerPermission.get(player.getUniqueId())).get("reload").equals(1)) {
                player.sendMessage(ChatColor.RED + "/homespawn reload:"
                        + ChatColor.GOLD + " Reloads The Plugin Configs");
            }
            if (Permissions.get(PlayerPermission.get(player.getUniqueId())).get("updateNotify").equals(1)) {
                player.sendMessage(ChatColor.RED + "/homespawn update (beta/stable):"
                        + ChatColor.GOLD + " Installs updates if available, you can also choose beta or stable.");
            }
            player.sendMessage(ChatColor.GOLD
                    + "-----------------------------------------");
        } else {
            return;
        }
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String alias,
                                      String[] args) {
        if (command.getName().equalsIgnoreCase("home") || command.getName().equalsIgnoreCase("delhome")) {
            List<String> l = new ArrayList<>();
            if (args.length == 1) {
                Player p = (Player) sender;
                YamlConfiguration pd = HomeConfigs.get(p.getUniqueId());
                l.addAll(pd.getStringList(p.getUniqueId() + ".list"));
            }
            debug("Tab Completed for " + sender.getName());
            return l;
        }
        return null;
    }

    private void CommandDelay() {
        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                if (!HomeSpawnTimeLeft.isEmpty()) {
                    for (Player p : HomeSpawnTimeLeft.keySet()) {
                        if (HomeSpawnLocations.get(p) == null) {
                            HomeSpawnTimeLeft.remove(p);
                            HomeSpawnLocations.remove(p);
                        }
                        if (HomeSpawnTimeLeft.isEmpty()) {
                            return;
                        }
                        Collection<Integer> values = HomeSpawnTimeLeft.values();
                        for (int Time : values) {
                            int NewTime = Time - 1;
                            if (NewTime > 0) {
                                HomeSpawnTimeLeft.put(p, NewTime);
                            } else if (NewTime <= 0) {
                                Location Tele = HomeSpawnLocations.get(p);
                                if (!(Tele == null)) {
                                    if (!Tele.getChunk().isLoaded()) {
                                        Tele.getChunk().load();
                                    }
                                    p.teleport(Tele);
                                    debug("Teleported " + p.getName());
                                    p.sendMessage(ChatColor.GOLD
                                            + "Teleporting...");
                                    HomeSpawnTimeLeft.remove(p);
                                    HomeSpawnLocations.remove(p);
                                } else {
                                    HomeSpawnTimeLeft.remove(p);
                                    HomeSpawnLocations.remove(p);
                                }
                            }
                        }
                    }
                }
            }
        }, 0, 20);
    }

    public void debug(String s) {
        if (getConfig().getBoolean("Debug")) {
            logger.info("Homespawn Debug: " + s);
        }
    }

    private void Commands() {
        HomeSpawnComponents hsc = new HomeSpawnComponents();
        hsc.init(this);
    }
}