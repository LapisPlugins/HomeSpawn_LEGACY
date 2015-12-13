package net.lapismc.HomeSpawn;

import net.gravitydevelopment.updater.Updater;
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
import org.mcstats.Metrics.Graph;
import org.mcstats.Metrics.Plotter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

public class HomeSpawn extends JavaPlugin {

    public final Logger logger = this.getLogger();
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
    public YamlConfiguration stackTraces;
    public File stackTracesFile;
    public YamlConfiguration messages;
    public File messagesFile;
    public YamlConfiguration passwords;
    public File passwordsFile;
    private HomeSpawnListener pl;

    @Override
    public void onEnable() {
        this.Enable();
        this.Configs();
        this.Metrics();
        this.Commands();
        this.CommandDelay();
    }

    private void Metrics() {
        if (this.getConfig().getBoolean("Metrics")) {
            try {
                Metrics metrics = new Metrics(this);
                Graph averageHomesGraph = metrics.createGraph();
                int homes = 0;
                int files = this.HomeConfigs.size();
                for (YamlConfiguration yaml : this.HomeConfigs.values()) {
                    homes = homes + yaml.getInt(yaml.getString("name") + ".Numb");
                }
                int average = homes % files == 0 ? homes / files : homes / files + 1;
                averageHomesGraph.addPlotter(new Plotter(average + "") {
                    @Override
                    public int getValue() {
                        return 1;
                    }
                });
                metrics.start();
            } catch (IOException e) {
                e.printStackTrace();
                this.logger.severe("[HomeSpawn] Metrics Failed To Start!");
            }
        } else {
            this.getLogger()
                    .info("Metrics wasn't started because it is disabled in the config!");
        }
    }

    private void Update() {
        if (this.getConfig().getBoolean("AutoUpdate")) {
            Updater updater = new Updater(this, this.getFile(),
                    Updater.UpdateType.DEFAULT, true);
            this.updateCheck(updater);
        } else {
            Updater updater = new Updater(this, this.getFile(),
                    Updater.UpdateType.NO_DOWNLOAD, true);
            this.updateCheck(updater);
        }
    }

    private void updateCheck(Updater updater) {
        File file = new File(this.getDataFolder().getAbsolutePath()
                + File.separator + "Update.yml");
        FileConfiguration getUpdate = YamlConfiguration.loadConfiguration(file);
        if (updater.getResult() == Updater.UpdateResult.SUCCESS) {
            this.getLogger().info(
                    "Updated, Reload or restart to install the update!");
        } else if (updater.getResult() == Updater.UpdateResult.NO_UPDATE) {
            this.getLogger().info("No Update Available");
            if (file.exists()) {
                if (getUpdate.contains("Avail")) {
                    getUpdate.set("Avail", "false");
                } else {
                    getUpdate.createSection("Avail");
                    try {
                        getUpdate.save(file);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    getUpdate.set("Avail", "false");
                }
            } else {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                getUpdate.createSection("Avail");
                try {
                    getUpdate.save(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                getUpdate.set("Avail", "false");
                try {
                    getUpdate.save(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else if (updater.getResult() == Updater.UpdateResult.UPDATE_AVAILABLE
                && updater.getResult() != Updater.UpdateResult.SUCCESS) {
            this.getLogger().info(
                    "An update is Available for HomeSpawn, It can be downloaded from,"
                            + " dev.bukkit.org/bukkit-plugins/homespawn");
            if (file.exists()) {
                if (!this.getConfig().getBoolean("AutoUpdate")) {
                    if (getUpdate.contains("Avail")) {
                        getUpdate.set("Avail", "true");
                    } else {
                        getUpdate.createSection("Avail");
                        getUpdate.set("Avail", "true");
                        try {
                            getUpdate.save(file);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                getUpdate.createSection("Avail");
                getUpdate.set("Avail", "true");
                try {
                    getUpdate.save(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            this.getLogger().severe(
                    ChatColor.RED + "Something Went Wrong Updating!");
            getUpdate.set("Avail", "false");
        }
        try {
            getUpdate.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        this.Disable();
    }

    private void Enable() {
        this.logger.info(" V." + this.getDescription().getVersion()
                + " Has Been Enabled!");
        PluginManager pm = this.getServer().getPluginManager();
        this.pl = new HomeSpawnListener(this);
        pm.registerEvents(this.pl, this);
    }

    private void configVersion() {
        if (this.getConfig().getInt("ConfigVersion") != 2) {
            this.getConfig().set("ConfigVersion", 2);
            if (!this.getConfig().contains("AutoUpdate")) {
                this.getConfig().set("AutoUpdate", true);
            }
            if (!this.getConfig().contains("UpdateNotification")) {
                this.getConfig().set("UpdateNotification", true);
            }
            if (!this.getConfig().contains("Metrics")) {
                this.getConfig().set("Metrics", true);
            }
            if (!this.getConfig().contains("VIPHomesLimit")) {
                this.getConfig().set("VIPHomesLimit", 3);
            }
            if (!this.getConfig().contains("AdminHomesLimit")) {
                this.getConfig().set("AdminHomesLimit", 5);
            }
            if (!this.getConfig().contains("TeleportTime")) {
                this.getConfig().set("TeleportTime", 10);
            }
            if (!this.getConfig().contains("CommandBook")) {
                this.getConfig().set("CommandBook", true);
            }
            if (!this.getConfig().contains("InventoryMenu")) {
                this.getConfig().set("InventoryMenu", true);
            }
            this.saveConfig();
        }
    }

    private void Disable() {
        this.logger.info("[HomeSpawn] Plugin Has Been Disabled!");
        HandlerList.unregisterAll();
    }

    private void Configs() {
        this.saveDefaultConfig();
        this.getConfig().options().copyDefaults(true);
        this.saveConfig();
        this.createSpawn();
        this.createPlayerData();
        this.createMessages();
        this.createPasswords();
        this.pl.setMessages();
        this.loadPlayerData();
        this.loadName();
        this.configVersion();
    }

    public void savePlayerData(String uuid) throws IOException {
        this.HomeConfigs.get(uuid).save(this.HomeConfigsFiles.get(uuid));
    }

    public void loadPlayerData() {
        this.HomeConfigs.clear();
        this.HomeConfigsFiles.clear();
        File file = new File(this.getDataFolder().getAbsolutePath() + File.separator + "PlayerData");
        File[] playerdataArray = file.listFiles();
        for (File f : playerdataArray) {
            if (f.isFile()) {
                if (!f.getName().contains("Passwords")) {
                    YamlConfiguration Yaml = YamlConfiguration.loadConfiguration(f);
                    this.HomeConfigs.put(Yaml.getString("name"), Yaml);
                    this.HomeConfigsFiles.put(Yaml.getString("name"), f);
                }
            }
        }
    }

    public void loadName() {
        this.PlayertoUUID.clear();
        File file = new File(this.getDataFolder().getAbsolutePath() + File.separator
                + "PlayerData" + File.separator + "PlayerNames");
        File[] playerNamesArray = file.listFiles();
        for (File f : playerNamesArray) {
            YamlConfiguration Yaml = YamlConfiguration.loadConfiguration(f);
            this.PlayertoUUID.put(Yaml.getString("Name"), Yaml.getString("UUID"));
        }
    }


    private void createPasswords() {
        File file = new File(this.getDataFolder().getAbsolutePath()
                + File.separator + "PlayerData" + File.separator
                + "Passwords.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.passwords = YamlConfiguration.loadConfiguration(file);
        this.passwordsFile = file;
    }

    private void createMessages() {
        File file2 = new File(this.getDataFolder().getAbsolutePath()
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
                this.setDefaultMessages();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.messages = YamlConfiguration.loadConfiguration(file2);
        this.messagesFile = file2;
    }

    private void setDefaultMessages() {
        File file2 = new File(this.getDataFolder().getAbsolutePath()
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
                e.printStackTrace();
            }
        } else {
            this.createMessages();
        }

    }

    private void createPlayerData() {
        File theDir = new File(this.getDataFolder().getAbsolutePath()
                + File.separator + "PlayerData");
        File theDir1 = new File(this.getDataFolder().getAbsolutePath()
                + File.separator + "PlayerData" + File.separator
                + "PlayerNames");
        if (!theDir.exists()) {
            this.logger.info(" Creating PlayerData Directory!");
            theDir.mkdir();
        }
        if (!theDir1.exists()) {
            theDir1.mkdir();
        }
    }

    private void createSpawn() {
        File file = new File(this.getDataFolder().getAbsolutePath()
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
                this.logger.severe("[HomeSpawn] Couldn't create spawn file!");
                e.printStackTrace();
            }
        }
        this.spawn = YamlConfiguration.loadConfiguration(file);
        this.spawnFile = file;
    }

    public void spawnnew(Player player) {
        if (this.spawn.get("spawnnew.SpawnSet") != null
                && this.spawn.getString("spawnnew.SpawnSet").equalsIgnoreCase(
                "yes")) {
            int x = this.spawn.getInt("spawnnew.X");
            int y = this.spawn.getInt("spawnnew.Y");
            int z = this.spawn.getInt("spawnnew.Z");
            float yaw = this.spawn.getInt("spawnnew.Yaw");
            float pitch = this.spawn.getInt("spawnnew.Pitch");
            String cworld = this.spawn.getString("spawnnew.World");
            World world = this.getServer().getWorld(cworld);
            Location spawnnew = new Location(world, x, y, z, yaw, pitch);
            spawnnew.add(0.5, 0, 0.5);
            player.teleport(spawnnew);
            this.logger.info("[HomeSpawn] Player " + player.getName()
                    + " Was Sent To New Spawn");
        } else {
            this.logger.info("[HomeSpawn] There Is No New Spawn Set And Therefore The Player Wasn't Sent To The New Spawn");
        }
    }

    public void reload(Object obj) {
        Player player = null;
        if (obj instanceof Player) {
            player = (Player) obj;
        } else if (obj instanceof String) {
            String s = (String) obj;
            if (s.equalsIgnoreCase("Silent")) {
                this.spawn = YamlConfiguration.loadConfiguration(this.spawnFile);
                this.messages = YamlConfiguration.loadConfiguration(this.messagesFile);
                this.passwords = YamlConfiguration.loadConfiguration(this.passwordsFile);
                this.loadPlayerData();
                this.loadName();
            }
        } else if (obj == null) {
            this.spawn = YamlConfiguration.loadConfiguration(this.spawnFile);
            this.messages = YamlConfiguration.loadConfiguration(this.messagesFile);
            this.passwords = YamlConfiguration.loadConfiguration(this.passwordsFile);
            this.loadPlayerData();
            this.loadName();
            Bukkit.broadcast(ChatColor.RED
                            + "Console" + ChatColor.GOLD + " Has Reloaded Homespawn!",
                    "homespawn.admin");
            this.logger
                    .info("You Have Reloaded Homespawn!");
        }
        if (player != null) {
            this.spawn = YamlConfiguration.loadConfiguration(this.spawnFile);
            this.messages = YamlConfiguration.loadConfiguration(this.messagesFile);
            this.passwords = YamlConfiguration.loadConfiguration(this.passwordsFile);
            this.loadPlayerData();
            this.loadName();
            player.sendMessage(ChatColor.GOLD
                    + "You have reloaded the configs for Homespawn!");
            Bukkit.broadcast(ChatColor.GOLD + "Player " + ChatColor.RED
                    + player.getName() + ChatColor.GOLD
                    + " Has Reloaded Homespawn!", "homespawn.admin");
            this.logger.info("Player " + player.getName()
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
            if (!this.getServer().getOnlineMode()) {
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
                YamlConfiguration pd = this.HomeConfigs.get(p.getUniqueId().toString());
                l.addAll(pd.getStringList(p.getUniqueId() + ".list"));
            }
            return l;
        }
        return null;
    }

    private void CommandDelay() {
        if (!this.getConfig().contains("TeleportTime")) {
            this.getConfig().createSection("TeleportTime");
            this.saveConfig();
            this.getConfig().set("TeleportTime", 0);
        }
        if (!(this.getConfig().getInt("TeleportTime") <= 0)) {
            BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
            scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
                @Override
                public void run() {
                    if (!HomeSpawn.this.HomeSpawnTimeLeft.isEmpty()) {
                        for (Player p : HomeSpawn.this.HomeSpawnTimeLeft.keySet()) {
                            if (HomeSpawn.this.HomeSpawnLocations.get(p) == null) {
                                HomeSpawn.this.HomeSpawnTimeLeft.remove(p);
                                HomeSpawn.this.HomeSpawnLocations.remove(p);
                            }
                            if (HomeSpawn.this.HomeSpawnTimeLeft.isEmpty()) {
                                return;
                            }
                            for (int Time : HomeSpawn.this.HomeSpawnTimeLeft.values()) {
                                int NewTime = Time - 1;
                                if (NewTime > 0) {
                                    HomeSpawn.this.HomeSpawnTimeLeft.put(p, NewTime);
                                } else if (NewTime <= 0) {
                                    Location Tele = HomeSpawn.this.HomeSpawnLocations.get(p);
                                    if (!(Tele == null)) {
                                        if (!Tele.getChunk().isLoaded()) {
                                            Tele.getChunk().load();
                                        }
                                        p.teleport(Tele);
                                        p.sendMessage(ChatColor.GOLD
                                                + "Teleporting...");
                                        HomeSpawn.this.HomeSpawnTimeLeft.remove(p);
                                        HomeSpawn.this.HomeSpawnLocations.remove(p);
                                    } else {
                                        HomeSpawn.this.HomeSpawnTimeLeft.remove(p);
                                        HomeSpawn.this.HomeSpawnLocations.remove(p);
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
        this.getCommand("home").setExecutor(new HomeSpawnCommand(this));
        this.getCommand("sethome").setExecutor(new HomeSpawnCommand(this));
        this.getCommand("delhome").setExecutor(new HomeSpawnCommand(this));
        this.getCommand("spawn").setExecutor(new HomeSpawnCommand(this));
        this.getCommand("setspawn").setExecutor(new HomeSpawnCommand(this));
        this.getCommand("delspawn").setExecutor(new HomeSpawnCommand(this));
        this.getCommand("homespawn").setExecutor(new HomeSpawnCommand(this));
        this.getCommand("homepassword").setExecutor(new HomeSpawnCommand(this));
        this.getCommand("homeslist").setExecutor(new HomeSpawnCommand(this));
        this.logger.info("Commands Registered!");
    }
}