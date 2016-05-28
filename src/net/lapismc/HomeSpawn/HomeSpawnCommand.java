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

    public static YamlConfiguration getMessages;
    public static YamlConfiguration getSpawn;
    private final HomeSpawn plugin;
    public HomeSpawnCommand cmd;
    private DelHome delHome;
    private DelSpawn delSpawn;
    private Home home;
    private HomePassword homePassword;
    private HomesList homesList;
    private net.lapismc.HomeSpawn.commands.HomeSpawn homeSpawn;
    private SetHome setHome;
    private SetSpawn setSpawn;
    private Spawn spawn;

    public HomeSpawnCommand(HomeSpawn plugin) {
        this.plugin = plugin;
        this.delHome = new DelHome(plugin);
        this.delSpawn = new DelSpawn(plugin);
        this.home = new Home(plugin, this);
        this.homePassword = new HomePassword(plugin, this);
        this.homesList = new HomesList(plugin, this);
        this.homeSpawn = new net.lapismc.HomeSpawn.commands.HomeSpawn(plugin);
        this.setHome = new SetHome(plugin);
        this.setSpawn = new SetSpawn(plugin);
        this.spawn = new Spawn(plugin, this);
    }

    public void showMenu(Player p) {
        UUID uuid = this.plugin.PlayertoUUID.get(p.getName());
        YamlConfiguration getHomes = this.plugin.HomeConfigs.get(uuid);
        List<String> homes = getHomes.getStringList(p.getUniqueId() + ".list");
        if (homes.isEmpty()) {
            p.sendMessage(ChatColor.DARK_RED
                    + HomeSpawnCommand.getMessages.getString("Home.NoHomeSet"));
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
        if (this.plugin.HomesListInvs.containsKey(p)) {
            if (!(this.plugin.HomesListInvs.get(p).getSize() == slots * 9)) {
                Inventory inv = Bukkit.createInventory(p, 9 * slots, ChatColor.GOLD + p.getName() + "'s HomesList");
                this.plugin.HomesListInvs.put(p, inv);
            }
        } else {
            Inventory inv = Bukkit.createInventory(p, 9 * slots, ChatColor.GOLD + p.getName() + "'s HomesList");
            this.plugin.HomesListInvs.put(p, inv);
        }
        for (String home : homes) {
            ItemStack i = new Wool(dc.get(r.nextInt(5))).toItemStack(1);
            ItemMeta im = i.getItemMeta();
            im.setDisplayName(ChatColor.GOLD + home);
            im.setLore(Arrays.asList(ChatColor.GOLD + "Click To Teleport To",
                    ChatColor.RED + home));
            i.setItemMeta(im);
            this.plugin.HomesListInvs.get(p).addItem(i);
        }
        p.openInventory(this.plugin.HomesListInvs.get(p));
    }

    private YamlConfiguration GetHome(String p) {
        UUID uuid = this.plugin.PlayertoUUID.get(p);
        YamlConfiguration getHomes = this.plugin.HomeConfigs.get(uuid);
        return getHomes;
    }

    public void TeleportPlayer(Player p, Location l, String r) {
        HashMap<String, Integer> perms = plugin.Permissions.get(plugin.PlayerPermission.get(p.getUniqueId()));
        if (perms.get("TPD") == 0) {
            if (!l.getChunk().isLoaded()) {
                l.getChunk().load();
            }
            p.teleport(l);
            if (r.equalsIgnoreCase("Spawn")) {
                p.sendMessage(ChatColor.GOLD
                        + HomeSpawnCommand.getMessages.getString("Spawn.SentToSpawn"));
            } else if (r.equalsIgnoreCase("Home")) {
                p.sendMessage(ChatColor.GOLD
                        + HomeSpawnCommand.getMessages.getString("Home.SentHome"));
            }
        } else {
            String waitraw = ChatColor.GOLD + HomeSpawnCommand.getMessages.getString("Wait");
            String Wait = waitraw.replace("{time}", ChatColor.RED
                    + perms.get("TPD").toString()
                    + ChatColor.GOLD);
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
            if (plugin.Permissions.get(plugin.PlayerPermission.get(player.getUniqueId())) == null) {
                plugin.Permissions();
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
                    this.plugin.reload(p);
                }
            }
        } else {
            sender.sendMessage("You Must Be a Player To Do That");
        }
        return false;
    }

}