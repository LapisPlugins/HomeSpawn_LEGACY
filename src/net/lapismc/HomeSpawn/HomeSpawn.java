package net.lapismc.HomeSpawn;

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
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.mcstats.Metrics;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HomeSpawn extends JavaPlugin {

    public final Logger logger = getLogger();
    public final HashMap<String, YamlConfiguration> HomeConfigs = new HashMap<>();
    public final HashMap<String, String> PlayertoUUID = new HashMap<>();
    final HashMap<Player, Location> HomeSpawnLocations = new HashMap<>();
    final HashMap<Player, Integer> HomeSpawnTimeLeft = new HashMap<>();
    final HashMap<Player, Inventory> HomesListInvs = new HashMap<>();
    private final HashMap<String, File> HomeConfigsFiles = new HashMap<>();
    public HomeSpawn plugin;
    public LapisUpdater updater;
    public YamlConfiguration spawn;
    public File spawnFile;
    public YamlConfiguration playerData;
    public File playerDataFile;
    public YamlConfiguration messages;
    public File messagesFile;
    public YamlConfiguration passwords;
    public File passwordsFile;
    private HomeSpawnListener pl;

    @Override
    public void onEnable() {
        Enable();
        Configs();
        Metrics();
        Commands();
        CommandDelay();
    }

    private void Metrics() {
        if (getConfig().getBoolean("Metrics")) {
            try {
                Metrics metrics = new Metrics(this);
                Metrics.Graph averageHomesGraph = metrics.createGraph();
                int homes = 0;
                int files = HomeConfigs.size();
                for (YamlConfiguration yaml : HomeConfigs.values()) {
                    homes = homes + yaml.getInt(yaml.getString("name") + ".Numb");
                }
                int average = homes % files == 0 ? homes / files : homes / files + 1;
                averageHomesGraph.addPlotter(new Metrics.Plotter(average + "") {
                    @Override
                    public int getValue() {
                        return 1;
                    }
                });
                metrics.start();
            } catch (IOException e) {
                this.logger.log(Level.SEVERE, "An error has occurred while trying to start HomeSpawn metrics");
                this.logger.log(Level.SEVERE, "The error follows, Please report it to dart2112");
                e.printStackTrace();
            }
        } else {
            getLogger()
                    .info("Metrics wasn't started because it is disabled in the config!");
        }
    }

    private void Update() {
        if (updater.checkUpdate(this, "HomeSpawn")) {
            if (getConfig().getBoolean("UpdateNotification")) {
                logger.info("An update for HomeSpawn is available and can be" +
                        " downloaded from https://www.spigotmc.org/resources/homespawn.14108");

            }
        }
    }

    @Override
    public void onDisable() {
        Disable();
    }

    private void Enable() {
        logger.info(" V." + getDescription().getVersion()
                + " Has Been Enabled!");
        PluginManager pm = getServer().getPluginManager();
        pl = new HomeSpawnListener(this);
        pm.registerEvents(pl, this);
    }

    private void configVersion() {
        if (getConfig().getInt("ConfigVersion") != 2) {
            getConfig().set("ConfigVersion", 2);
            if (!getConfig().contains("AutoUpdate")) {
                getConfig().set("AutoUpdate", true);
            }
            if (!getConfig().contains("UpdateNotification")) {
                getConfig().set("UpdateNotification", true);
            }
            if (!getConfig().contains("Metrics")) {
                getConfig().set("Metrics", true);
            }
            if (!getConfig().contains("VIPHomesLimit")) {
                getConfig().set("VIPHomesLimit", 3);
            }
            if (!getConfig().contains("AdminHomesLimit")) {
                getConfig().set("AdminHomesLimit", 5);
            }
            if (!getConfig().contains("TeleportTime")) {
                getConfig().set("TeleportTime", 10);
            }
            if (!getConfig().contains("CommandBook")) {
                getConfig().set("CommandBook", true);
            }
            if (!getConfig().contains("InventoryMenu")) {
                getConfig().set("InventoryMenu", true);
            }
            saveConfig();
        }
    }

    private void Disable() {
        logger.info("[HomeSpawn] Plugin Has Been Disabled!");
        HandlerList.unregisterAll();
    }

    private void Configs() {
        saveDefaultConfig();
        getConfig().options().copyDefaults(true);
        saveConfig();
        createSpawn();
        createPlayerData();
        createMessages();
        createPasswords();
        pl.setMessages();
        loadPlayerData();
        loadName();
        configVersion();
    }

    public void savePlayerData(String uuid) {
        try {
            HomeConfigs.get(uuid).save(HomeConfigsFiles.get(uuid));
        } catch (IOException e) {
            this.logger.log(Level.SEVERE, "An error has occurred while trying to save HomeSpawn player data");
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
                    HomeConfigs.put(Yaml.getString("name"), Yaml);
                    HomeConfigsFiles.put(Yaml.getString("name"), f);
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
            PlayertoUUID.put(Yaml.getString("Name"), Yaml.getString("UUID"));
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
                this.logger.log(Level.SEVERE, "An error has occurred while trying to save HomeSpawn player password data");
                this.logger.log(Level.SEVERE, "The error follows, Please report it to dart2112");
                e.printStackTrace();
            }
        }
        passwords = YamlConfiguration.loadConfiguration(file);
        passwordsFile = file;
    }

    private void createMessages() {
        File file2 = new File(getDataFolder().getAbsolutePath()
                + File.separator + "Messages.yml");
        FileConfiguration getMessages = YamlConfiguration
                .loadConfiguration(file2);
        if (!file2.exists()) {
            try {
                file2.createNewFile();
                getMessages.createSection("Home");
                getMessages.createSection("Home.HomeSet");
                getMessages.createSection("Home.SentHome");
                getMessages.createSection("Home.NoHomeSet");
                getMessages.createSection("Home.HomeRemoved");
                getMessages.createSection("Home.LimitReached");
                getMessages.createSection("Spawn");
                getMessages.createSection("Spawn.NotSet");
                getMessages.createSection("Spawn.SpawnSet");
                getMessages.createSection("Spawn.SpawnNewSet");
                getMessages.createSection("Spawn.SentToSpawn");
                getMessages.createSection("Spawn.Removed");
                getMessages.createSection("Wait");
                getMessages.createSection("NoPerms");
                getMessages.createSection("Error.Args");
                getMessages.createSection("Error.Args+");
                getMessages.createSection("Error.Args-");
                getMessages.save(file2);
                setDefaultMessages();
            } catch (IOException e) {
                this.logger.log(Level.SEVERE, "An error has occurred while trying to save HomeSpawn messages data");
                this.logger.log(Level.SEVERE, "The error follows, Please report it to dart2112");
                e.printStackTrace();
            }
        }
        messages = YamlConfiguration.loadConfiguration(file2);
        messagesFile = file2;
    }

    private void setDefaultMessages() {
        File file2 = new File(getDataFolder().getAbsolutePath()
                + File.separator + "Messages.yml");
        FileConfiguration getMessages = YamlConfiguration
                .loadConfiguration(file2);
        if (file2.exists()) {
            getMessages.set("Home.HomeSet", "Home Set, You Can Now Use /home");
            getMessages.set("Home.SentHome", "Welcome Home");
            getMessages.set("Home.NoHomeSet",
                    "You First Need To Set a Home With /sethome");
            getMessages.set("Home.HomeRemoved", "Home Removed");
            getMessages.set("Home.LimitReached",
                    "Sorry But You have Reached The Max Limit Of Homes, "
                            + "Please Use /delhome To Remove A Home");
            getMessages.set("Spawn.NotSet",
                    "You First Need To Set a Spawn With /setspawn");
            getMessages.set("Spawn.SpawnSet",
                    "Spawn Set, You Can Now Use /spawn");
            getMessages
                    .set("Spawn.SpawnNewSet",
                            "Spawn New Set, All New Players Will Be Sent To This Location");
            getMessages.set("Spawn.SentToSpawn", "Welcome To Spawn");
            getMessages.set("Spawn.Removed", "Spawn Removed!");
            getMessages
                    .set("Wait",
                            "You Must Wait {time} Seconds Before You Can Be Teleported,"
                                    + " If You Move Or Get Hit By Another Player Your Teleport Will Be Canceled");
            getMessages.set("NoPerms", "You Don't Have Permission To Do That!");
            getMessages.set("Error.Args+", "Too Much Infomation!");
            getMessages.set("Error.Args-", "Not Enough Infomation");
            getMessages.set("Error.Args", "Too Little or Too Much Infomation");
            try {
                getMessages.save(file2);
            } catch (IOException e) {
                this.logger.log(Level.SEVERE, "An error has occurred while trying to save HomeSpawn messages data");
                this.logger.log(Level.SEVERE, "The error follows, Please report it to dart2112");
                e.printStackTrace();
            }
        } else {
            createMessages();
        }

    }

    private void createPlayerData() {
        File theDir = new File(getDataFolder().getAbsolutePath()
                + File.separator + "PlayerData");
        File theDir1 = new File(getDataFolder().getAbsolutePath()
                + File.separator + "PlayerData" + File.separator
                + "PlayerNames");
        if (!theDir.exists()) {
            logger.info(" Creating PlayerData Directory!");
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
            logger.info("[HomeSpawn] Player " + player.getName()
                    + " Was Sent To New Spawn");
        } else {
            logger.info("[HomeSpawn] There Is No New Spawn Set And Therefore The Player Wasn't Sent To The New Spawn");
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
            Bukkit.broadcast(ChatColor.RED
                            + "Console" + ChatColor.GOLD + " Has Reloaded Homespawn!",
                    "homespawn.admin");
            logger
                    .info("You Have Reloaded Homespawn!");
        }
        if (player != null) {
            spawn = YamlConfiguration.loadConfiguration(spawnFile);
            messages = YamlConfiguration.loadConfiguration(messagesFile);
            passwords = YamlConfiguration.loadConfiguration(passwordsFile);
            loadPlayerData();
            loadName();
            player.sendMessage(ChatColor.GOLD
                    + "You have reloaded the configs for Homespawn!");
            Bukkit.broadcast(ChatColor.GOLD + "Player " + ChatColor.RED
                    + player.getName() + ChatColor.GOLD
                    + " Has Reloaded Homespawn!", "homespawn.admin");
            logger.info("Player " + player.getName()
                    + " Has Reloaded Homespawn!");
        }
    }

    public void help(Player player) {
        if (player != null) {
            player.sendMessage(ChatColor.GOLD + "---------------"
                    + ChatColor.RED + "Homespawn" + ChatColor.GOLD
                    + "---------------");
            player.sendMessage(ChatColor.RED + "[name] = VIP Only");
            player.sendMessage(ChatColor.RED + "/home [name]:" + ChatColor.GOLD
                    + " Sends You To The Home Specified");
            player.sendMessage(ChatColor.RED + "/sethome [name]:"
                    + ChatColor.GOLD
                    + " Sets Your Home At Your Current Location");
            player.sendMessage(ChatColor.RED + "/delhome [name]:"
                    + ChatColor.GOLD + " Removes The Specified Home");
            player.sendMessage(ChatColor.RED + "/spawn:" + ChatColor.GOLD
                    + " Sends You To Spawn");
            if (!getServer().getOnlineMode()) {
                player.sendMessage(ChatColor.RED + "/homepassword help:"
                        + ChatColor.GOLD
                        + " Displays The Home Transfer Commands");
            }
            if (player.hasPermission("homespawn.admin")) {
                player.sendMessage(ChatColor.RED + "/setspawn:"
                        + ChatColor.GOLD + " Sets The Server Spawn");
                player.sendMessage(ChatColor.RED + "/setspawn new:"
                        + ChatColor.GOLD
                        + " All New Players Will Be Sent To This Spawn");
                player.sendMessage(ChatColor.RED + "/delspawn:"
                        + ChatColor.GOLD + " Removes The Server Spawn");
                player.sendMessage(ChatColor.RED + "/homespawn:"
                        + ChatColor.GOLD + " Displays Plugin Infomation");
                player.sendMessage(ChatColor.RED + "/homespawn reload:"
                        + ChatColor.GOLD + " Reloads The Plugin Configs");
                player.sendMessage(ChatColor.GOLD
                        + "-----------------------------------------");
                return;
            } else {
                player.sendMessage(ChatColor.GOLD
                        + "-----------------------------------------");
            }

        } else {
            return;
        }
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (command.getName().equalsIgnoreCase("home") || command.getName().equalsIgnoreCase("delhome")) {
            List<String> l = new ArrayList<String>();
            if (args.length == 1) {
                Player p = (Player) sender;
                YamlConfiguration pd = HomeConfigs.get(p.getUniqueId().toString());
                l.addAll(pd.getStringList(p.getUniqueId() + ".list"));
            }
            return l;
        }
        return null;
    }

    private void CommandDelay() {
        if (!getConfig().contains("TeleportTime")) {
            getConfig().createSection("TeleportTime");
            saveConfig();
            getConfig().set("TeleportTime", 0);
        }
        if (!(getConfig().getInt("TeleportTime") <= 0)) {
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
                            for (int Time : HomeSpawnTimeLeft.values()) {
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
        } else {
            return;
        }
    }

    private void Commands() {
        getCommand("home").setExecutor(new HomeSpawnCommand(this));
        getCommand("sethome").setExecutor(new HomeSpawnCommand(this));
        getCommand("delhome").setExecutor(new HomeSpawnCommand(this));
        getCommand("spawn").setExecutor(new HomeSpawnCommand(this));
        getCommand("setspawn").setExecutor(new HomeSpawnCommand(this));
        getCommand("delspawn").setExecutor(new HomeSpawnCommand(this));
        getCommand("homespawn").setExecutor(new HomeSpawnCommand(this));
        getCommand("homepassword").setExecutor(new HomeSpawnCommand(this));
        getCommand("homeslist").setExecutor(new HomeSpawnCommand(this));
        logger.info("Commands Registered!");
    }
}