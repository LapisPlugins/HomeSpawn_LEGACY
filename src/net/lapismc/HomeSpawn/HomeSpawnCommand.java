package net.lapismc.HomeSpawn;

import net.lapismc.HomeSpawn.commands.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Wool;

import java.util.*;

public class HomeSpawnCommand implements CommandExecutor {

    private final HomeSpawn plugin;
    public HomeSpawnCommand cmd;
    public HomeSpawnDelHome delHome;
    public HomeSpawnDelSpawn delSpawn;
    public HomeSpawnHome home;
    public HomeSpawnHomePassword homePassword;
    public HomeSpawnHomesList homesList;
    public net.lapismc.HomeSpawn.commands.HomeSpawn homeSpawn;
    public HomeSpawnSetHome setHome;
    public HomeSpawnSetSpawn setSpawn;
    public HomeSpawnSpawn spawn;

    public HomeSpawnCommand(HomeSpawn plugin) {
        this.plugin = plugin;
    }

    protected void registerCommands() {
        this.delHome = new HomeSpawnDelHome(plugin);
        this.delSpawn = new HomeSpawnDelSpawn(plugin);
        this.home = new HomeSpawnHome(plugin, this);
        this.homesList = new HomeSpawnHomesList(plugin, this);
        this.setHome = new HomeSpawnSetHome(plugin);
        this.setSpawn = new HomeSpawnSetSpawn(plugin);
        this.spawn = new HomeSpawnSpawn(plugin, this);
        this.homePassword = new HomeSpawnHomePassword(plugin, this);
        this.homeSpawn = new net.lapismc.HomeSpawn.commands.HomeSpawn(plugin);
    }

    public void showMenu(Player p) {
        UUID uuid = this.plugin.HSConfig.PlayertoUUID.get(p.getName());
        YamlConfiguration getHomes = this.plugin.HSConfig.HomeConfigs.get(uuid);
        if (getHomes == null) {
            p.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.HSConfig.messages.getString("Error.Config")));
            plugin.HSConfig.reload("Silent");
            return;
        }
        List<String> homes = getHomes.getStringList(p.getUniqueId() + ".list");
        if (homes.isEmpty()) {
            p.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.HSConfig.messages.getString("HomeSpawnHome.NoHomeSet")));
            return;
        }
        ArrayList<DyeColor> dc = new ArrayList<>();
        dc.add(DyeColor.BLACK);
        dc.add(DyeColor.BLUE);
        dc.add(DyeColor.GRAY);
        dc.add(DyeColor.GREEN);
        dc.add(DyeColor.MAGENTA);
        dc.add(DyeColor.ORANGE);
        Random r = new Random(25);
        int slots = homes.size() % 9 == 0 ? homes.size() / 9 : homes.size() / 9 + 1;
        if (this.homesList.HomesListInvs.containsKey(p)) {
            if (!(this.homesList.HomesListInvs.get(p).getSize() == slots * 9)) {
                Inventory inv = Bukkit.createInventory(p, 9 * slots, ChatColor.GOLD + p.getName() + "'s HomeSpawnHomesList");
                this.homesList.HomesListInvs.put(p, inv);
            }
        } else {
            Inventory inv = Bukkit.createInventory(p, 9 * slots, ChatColor.GOLD + p.getName() + "'s HomeSpawnHomesList");
            this.homesList.HomesListInvs.put(p, inv);
        }
        for (String home : homes) {
            ItemStack i = new Wool(dc.get(r.nextInt(5))).toItemStack(1);
            ItemMeta im = i.getItemMeta();
            im.setDisplayName(ChatColor.GOLD + home);
            im.setLore(Arrays.asList(ChatColor.GOLD + "Click To Teleport To",
                    ChatColor.RED + home));
            i.setItemMeta(im);
            this.homesList.HomesListInvs.get(p).addItem(i);
        }
        p.openInventory(this.homesList.HomesListInvs.get(p));
    }

    private YamlConfiguration GetHome(String p) {
        UUID uuid = plugin.HSConfig.PlayertoUUID.get(p);
        YamlConfiguration getHomes = this.plugin.HSConfig.HomeConfigs.get(uuid);
        return getHomes;
    }

    public void TeleportPlayer(Player p, Location l, String r, String name) {
        HashMap<String, Integer> perms = plugin.HSPermissions.Permissions.get(plugin.HSPermissions.PlayerPermission.get(p.getUniqueId()));
        if (perms.get("TPD") == 0) {
            if (!l.getChunk().isLoaded()) {
                l.getChunk().load();
            }
            p.teleport(l);
            if (r.equalsIgnoreCase("Spawn")) {
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.HSConfig.messages.getString("HomeSpawnSpawn.SentToSpawn")));
            } else if (r.equalsIgnoreCase("Home")) {
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.HSConfig.messages.getString("HomeSpawnHome.SentHome")));
            }
        } else {
            String waitraw = ChatColor.translateAlternateColorCodes('&', plugin.HSConfig.messages.getString("Wait"));
            String Wait = waitraw.replace("{time}", perms.get("TPD").toString());
            p.sendMessage(Wait);
            this.plugin.HomeSpawnLocations.put(p, l);
            this.plugin.HomeSpawnTimeLeft.put(p, perms.get("TPD"));
        }
    }

    public void PassHelp(Player player) {
        player.sendMessage(ChatColor.GOLD + "---------------------"
                + ChatColor.RED + "Homespawn" + ChatColor.GOLD
                + "---------------------");
        player.sendMessage(ChatColor.RED + "/homepassword help:"
                + ChatColor.GOLD + " Shows This Text");
        player.sendMessage(ChatColor.RED
                + "/homepassword set [password] [password]:" + ChatColor.GOLD
                + " Sets Your Transfer Password");
        player.sendMessage(ChatColor.RED
                + "/homepassword transfer [old username] [password]:"
                + ChatColor.GOLD
                + " Transfers Playerdata From Old Username To Current Username");
        player.sendMessage(ChatColor.GOLD
                + "-----------------------------------------------------");
        return;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd,
                             String commandLabel, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (plugin.HSPermissions.Permissions.get(plugin.HSPermissions.PlayerPermission.get(player.getUniqueId())) == null) {
                plugin.HSPermissions.init();
            }
            if (cmd.getName().equalsIgnoreCase("sethome")) {
                setHome.setHome(args, player);
            } else if (cmd.getName().equalsIgnoreCase("home")) {
                home.home(args, player);
            } else if (cmd.getName().equalsIgnoreCase("delhome")) {
                delHome.delHome(args, player);
            } else if (cmd.getName().equalsIgnoreCase("setspawn")) {
                setSpawn.setSpawn(args, player);
            } else if (cmd.getName().equals("spawn")) {
                spawn.spawn(args, player);
            } else if (cmd.getName().equalsIgnoreCase("delspawn")) {
                delSpawn.delSpawn(args, player);
            } else if (cmd.getName().equalsIgnoreCase("homeslist")) {
                homesList.homesList(args, player);
            } else if (cmd.getName().equalsIgnoreCase("homespawn")) {
                homeSpawn.homeSpawn(args, player);
            } else if (cmd.getName().equalsIgnoreCase("homepassword")) {
                homePassword.homePassword(args, player);
            }
        } else if (cmd.getName().equalsIgnoreCase("homespawn")) {
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("reload")) {
                    Player p = null;
                    this.plugin.HSConfig.reload(p);
                } else if (args[0].equalsIgnoreCase("update")) {
                    String ID = plugin.getConfig().getBoolean("BetaVersions") ? "beta" : "stable";
                    if (plugin.lapisUpdater.downloadUpdate(ID)) {
                        sender.sendMessage(ChatColor.GOLD + "Downloading Update...");
                        sender.sendMessage(ChatColor.GOLD + "The update will be installed" +
                                " when the server next starts!");
                    } else {
                        sender.sendMessage(ChatColor.GOLD + "Updating failed or there is no update!");
                    }
                } else {
                    sender.sendMessage(ChatColor.GOLD + "---------------"
                            + ChatColor.RED + "Homespawn" + ChatColor.GOLD
                            + "---------------");
                    sender.sendMessage(ChatColor.RED + "Author:"
                            + ChatColor.GOLD + " Dart2112");
                    sender.sendMessage(ChatColor.RED + "Version: "
                            + ChatColor.GOLD
                            + this.plugin.getDescription().getVersion());
                    String version = System.getProperty("java.version");
                    int pos = version.indexOf('.');
                    pos = version.indexOf('.', pos + 1);
                    Double versionDouble = Double.parseDouble(version.substring(0, pos));
                    sender.sendMessage(ChatColor.RED + "Java Version: " + ChatColor.GOLD
                            + versionDouble);
                    sender.sendMessage(ChatColor.RED + "Spigot:"
                            + ChatColor.GOLD + " https://goo.gl/aWby6W");
                    sender.sendMessage(ChatColor.GOLD
                            + "-----------------------------------------");
                    sender.sendMessage("HomeSpawn Console Commands!");
                    sender.sendMessage("/homespawn reload: Reloads all configs");
                    sender.sendMessage("/homespawn update: Will download and " +
                            "install update if available");
                }
            } else {
                sender.sendMessage(ChatColor.GOLD + "---------------"
                        + ChatColor.RED + "Homespawn" + ChatColor.GOLD
                        + "---------------");
                sender.sendMessage(ChatColor.RED + "Author:"
                        + ChatColor.GOLD + " Dart2112");
                sender.sendMessage(ChatColor.RED + "Version: "
                        + ChatColor.GOLD
                        + this.plugin.getDescription().getVersion());
                String version = System.getProperty("java.version");
                int pos = version.indexOf('.');
                pos = version.indexOf('.', pos + 1);
                Double versionDouble = Double.parseDouble(version.substring(0, pos));
                sender.sendMessage(ChatColor.RED + "Java Version: " + ChatColor.GOLD
                        + versionDouble);
                sender.sendMessage(ChatColor.RED + "Spigot:"
                        + ChatColor.GOLD + " https://goo.gl/aWby6W");
                sender.sendMessage(ChatColor.GOLD
                        + "-----------------------------------------");
                sender.sendMessage("HomeSpawn Console Commands!");
                sender.sendMessage("/homespawn reload: Reloads all configs");
                sender.sendMessage("/homespawn update: Will download and " +
                        "install update if available");
            }
        } else {
            sender.sendMessage("You Must Be a Player To Do That");
        }
        return false;
    }

}