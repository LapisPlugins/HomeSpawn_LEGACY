package net.lapismc.HomeSpawn;

import net.lapismc.HomeSpawn.commands.DelHome;
import net.lapismc.HomeSpawn.commands.Home;
import net.lapismc.HomeSpawn.commands.SetHome;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Wool;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.*;

public class HomeSpawnCommand implements CommandExecutor {

    public static YamlConfiguration getMessages;
    public static YamlConfiguration getSpawn;
    private final HomeSpawn plugin;
    public HomeSpawnCommand cmd;
    private DelHome delHome;
    private Home home;
    private SetHome setHome;

    public HomeSpawnCommand(HomeSpawn plugin) {
        this.plugin = plugin;
        this.delHome = new DelHome(plugin);
        this.home = new Home(plugin);
        this.setHome = new SetHome(plugin);
    }

    private void showMenu(Player p) {
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

    @Override
    public boolean onCommand(CommandSender sender, Command cmd,
                             String commandLabel, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (cmd.getName().equalsIgnoreCase("sethome")) {
                setHome.setHome(args, player);
            } else if (cmd.getName().equalsIgnoreCase("home")) {
                home.home(args, player, this);
            } else if (cmd.getName().equalsIgnoreCase("delhome")) {
                delHome.delHome(args, player);
            } else if (cmd.getName().equalsIgnoreCase("setspawn")) {
                HashMap<String, Integer> perms = plugin.Permissions.get(plugin.PlayerPermission.get(player.getUniqueId()));
                if (perms.get("sSpawn") == 1) {
                    if (args.length == 0) {
                        HomeSpawnCommand.getSpawn.set("spawn.SpawnSet", "Yes");
                        HomeSpawnCommand.getSpawn.set("spawn.X", player.getLocation()
                                .getBlockX());
                        HomeSpawnCommand.getSpawn.set("spawn.Y", player.getLocation()
                                .getBlockY());
                        HomeSpawnCommand.getSpawn.set("spawn.Z", player.getLocation()
                                .getBlockZ());
                        HomeSpawnCommand.getSpawn.set("spawn.World", player.getWorld().getName());
                        HomeSpawnCommand.getSpawn.set("spawn.Yaw", player.getLocation().getYaw());
                        HomeSpawnCommand.getSpawn.set("spawn.Pitch", player.getLocation()
                                .getPitch());
                        player.sendMessage(ChatColor.GOLD
                                + HomeSpawnCommand.getMessages.getString("Spawn.SpawnSet"));
                    } else if (args[0].equalsIgnoreCase("new")) {
                        HomeSpawnCommand.getSpawn.set("spawnnew.SpawnSet", "Yes");
                        HomeSpawnCommand.getSpawn.set("spawnnew.X", player.getLocation()
                                .getBlockX());
                        HomeSpawnCommand.getSpawn.set("spawnnew.Y", player.getLocation()
                                .getBlockY());
                        HomeSpawnCommand.getSpawn.set("spawnnew.Z", player.getLocation()
                                .getBlockZ());
                        HomeSpawnCommand.getSpawn.set("spawnnew.World", player.getWorld()
                                .getName());
                        HomeSpawnCommand.getSpawn.set("spawnnew.Yaw", player.getLocation()
                                .getYaw());
                        HomeSpawnCommand.getSpawn.set("spawnnew.Pitch", player.getLocation()
                                .getPitch());
                        player.sendMessage(ChatColor.GOLD
                                + HomeSpawnCommand.getMessages.getString("Spawn.SpawnNewSet"));
                    } else {
                        this.plugin.help(player);
                    }
                    try {
                        HomeSpawnCommand.getSpawn.save(this.plugin.spawnFile);
                        this.plugin.reload("Silent");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else {
                    player.sendMessage(ChatColor.DARK_RED
                            + HomeSpawnCommand.getMessages.getString("NoPerms"));

                }
            } else if (cmd.getName().equals("spawn")) {
                HashMap<String, Integer> perms = plugin.Permissions.get(plugin.PlayerPermission.get(player.getUniqueId()));
                if (perms.get("spawn") == 1) {
                    if (!HomeSpawnCommand.getSpawn.contains("spawn.SpawnSet")) {
                        player.sendMessage(ChatColor.RED
                                + HomeSpawnCommand.getMessages.getString("Spawn.NotSet"));
                        return false;
                    }
                    if (HomeSpawnCommand.getSpawn.getString("spawn.SpawnSet").equalsIgnoreCase(
                            "yes")) {
                        int x = HomeSpawnCommand.getSpawn.getInt("spawn.X");
                        int y = HomeSpawnCommand.getSpawn.getInt("spawn.Y");
                        int z = HomeSpawnCommand.getSpawn.getInt("spawn.Z");
                        float yaw = HomeSpawnCommand.getSpawn.getInt("spawn.Yaw");
                        float pitch = HomeSpawnCommand.getSpawn.getInt("spawn.Pitch");
                        String cworld = HomeSpawnCommand.getSpawn.getString("spawn.World");
                        World world = this.plugin.getServer().getWorld(cworld);
                        Location Spawn = new Location(world, x, y, z, yaw,
                                pitch);
                        Spawn.add(0.5, 0, 0.5);
                        this.TeleportPlayer(player, Spawn, "Spawn");
                    } else {
                        player.sendMessage(ChatColor.RED
                                + HomeSpawnCommand.getMessages.getString("Spawn.NotSet"));
                    }
                } else {
                    player.sendMessage(ChatColor.DARK_RED
                            + HomeSpawnCommand.getMessages.getString("NoPerms"));
                }

            } else if (cmd.getName().equalsIgnoreCase("delspawn")) {
                HashMap<String, Integer> perms = plugin.Permissions.get(plugin.PlayerPermission.get(player.getUniqueId()));
                if (perms.get("sSpawn") == 1) {
                    if (Objects.equals(HomeSpawnCommand.getSpawn.getString("spawn.SpawnSet"), "No")
                            || !HomeSpawnCommand.getSpawn.contains("spawn.SpawnSet")) {
                        player.sendMessage(ChatColor.RED
                                + HomeSpawnCommand.getMessages.getString("Spawn.NotSet"));
                    } else if (HomeSpawnCommand.getSpawn.getString("spawn.SpawnSet")
                            .equalsIgnoreCase("Yes")) {
                        HomeSpawnCommand.getSpawn.set("spawn.SpawnSet", "No");
                        player.sendMessage(ChatColor.GOLD
                                + HomeSpawnCommand.getMessages.getString("Spawn.Removed"));
                        try {
                            HomeSpawnCommand.getSpawn.save(this.plugin.spawnFile);
                            this.plugin.reload("silent");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                } else {
                    player.sendMessage(ChatColor.DARK_RED
                            + HomeSpawnCommand.getMessages.getString("NoPerms"));
                }
            } else if (cmd.getName().equalsIgnoreCase("homeslist")) {
                if (this.plugin.getConfig().getBoolean("InventoryMenu")) {
                    this.showMenu(player);
                    return true;
                } else {
                    UUID uuid = this.plugin.PlayertoUUID.get(player.getName());
                    YamlConfiguration getHomes = this.plugin.HomeConfigs.get(uuid);
                    if (!getHomes.contains(player.getUniqueId()
                            + ".list")) {
                        getHomes.createSection(player.getUniqueId()
                                + ".list");
                    }
                    List<String> list = getHomes.getStringList(player
                            .getUniqueId() + ".list");
                    if (getHomes.getInt(player.getUniqueId()
                            + ".Numb") > 0) {
                        if (!list.isEmpty()) {
                            String list2 = list.toString();
                            String list3 = list2.replace("[", " ");
                            String StringList = list3.replace("]", " ");
                            player.sendMessage(ChatColor.GOLD
                                    + "Your Current Homes Are:");
                            player.sendMessage(ChatColor.RED + StringList);
                        } else {
                            player.sendMessage(ChatColor.DARK_RED
                                    + HomeSpawnCommand.getMessages.getString("Home.NoHomeSet"));
                        }
                    } else {
                        player.sendMessage(ChatColor.DARK_RED
                                + HomeSpawnCommand.getMessages.getString("Home.NoHomeSet"));
                    }
                }
            } else if (cmd.getName().equalsIgnoreCase("homespawn")) {
                HashMap<String, Integer> perms = plugin.Permissions.get(plugin.PlayerPermission.get(player.getUniqueId()));
                if (args.length == 0) {
                    player.sendMessage(ChatColor.GOLD + "---------------"
                            + ChatColor.RED + "Homespawn" + ChatColor.GOLD
                            + "---------------");
                    player.sendMessage(ChatColor.RED + "Author:"
                            + ChatColor.GOLD + " Dart2112");
                    player.sendMessage(ChatColor.RED + "Version: "
                            + ChatColor.GOLD
                            + this.plugin.getDescription().getVersion());
                    player.sendMessage(ChatColor.RED + "Bukkit Dev:"
                            + ChatColor.GOLD + " http://goo.gl/2Selqa");
                    player.sendMessage(ChatColor.RED
                            + "Use /homespawn Help For Commands!");
                    player.sendMessage(ChatColor.GOLD
                            + "-----------------------------------------");
                } else if (args.length == 1) {
                    if (args[0].equalsIgnoreCase("reload")) {
                        if (perms.get("reload") == 1) {
                            this.plugin.reload(player);
                        } else {
                            player.sendMessage(ChatColor.RED
                                    + "You Don't Have Permission To Do That");
                        }
                    } else if (args[0].equalsIgnoreCase("help")) {
                        this.plugin.help(player);
                    }
                } else if (args.length > 0) {
                    if (args[0].equalsIgnoreCase("update")) {
                        if (perms.get("updateNotify") == 1) {
                            LapisUpdater updater = new LapisUpdater(plugin);
                            if (args.length == 1) {
                                String ID = plugin.getConfig().getBoolean("BetaVersions") ? "beta" : "stable";
                                if (updater.downloadUpdate(ID)) {
                                    player.sendMessage(ChatColor.GOLD + "Downloading Update...");
                                    player.sendMessage(ChatColor.GOLD + "The update will be installed" +
                                            " when the server next starts!");
                                } else {
                                    player.sendMessage(ChatColor.GOLD + "Updating failed or there is no update!");
                                }
                            } else if (args.length == 2) {
                                String ID = args[1];
                                if (updater.downloadUpdate(ID)) {
                                    player.sendMessage(ChatColor.GOLD + "Downloading Update...");
                                    player.sendMessage(ChatColor.GOLD + "The update will be installed" +
                                            " when the server next starts!");
                                } else {
                                    player.sendMessage(ChatColor.GOLD + "Updating failed or there is no update!");
                                }
                            } else {
                                player.sendMessage(ChatColor.RED + HomeSpawnCommand.getMessages.getString("Error.Args"));
                            }
                        } else {
                            player.sendMessage(ChatColor.RED + HomeSpawnCommand.getMessages.getString("NoPerms"));
                        }
                    }
                } else {
                    player.sendMessage("That Is Not A Recognised Command, Use /homespawn help For Commands");
                }
            } else if (cmd.getName().equalsIgnoreCase("homepassword")) {
                File file = new File(this.plugin.getDataFolder() + File.separator
                        + "PlayerData" + File.separator
                        + player.getUniqueId() + ".yml");
                FileConfiguration getHomes = YamlConfiguration
                        .loadConfiguration(file);
                File file3 = new File(this.plugin.getDataFolder() + File.separator
                        + "PlayerData" + File.separator + "Passwords.yml");
                FileConfiguration getPasswords = YamlConfiguration
                        .loadConfiguration(file3);
                if (!this.plugin.getServer().getOnlineMode()) {
                    if (args.length == 3) {
                        String string = args[0];
                        if (string.equalsIgnoreCase("set")) {
                            if (args[1].equals(args[2])) {
                                String pass = args[1];
                                String passHash = null;
                                try {
                                    passHash = PasswordHash.createHash(pass);
                                } catch (NoSuchAlgorithmException
                                        | InvalidKeySpecException e1) {
                                    e1.printStackTrace();
                                    player.sendMessage(ChatColor.RED
                                            + "Failed To Save Password!");
                                    return false;
                                }
                                getPasswords.set(player.getName(), passHash);
                                player.sendMessage(ChatColor.GOLD
                                        + "Password Set To:");
                                player.sendMessage(ChatColor.RED + pass);
                                try {
                                    getPasswords.save(file3);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                player.sendMessage(ChatColor.RED
                                        + "Your 2 passwords didn't match!");
                            }
                        }
                    } else if (args.length <= 1) {
                        this.PassHelp(player);
                    } else if (args.length == 3) {
                        String string = args[0];
                        if (string.equalsIgnoreCase("transfer")) {
                            String pass = args[2];
                            String name = args[1];
                            File namefile = new File(this.plugin.getDataFolder()
                                    .getAbsolutePath()
                                    + File.separator
                                    + "PlayerData"
                                    + File.separator
                                    + "PlayerNames"
                                    + File.separator
                                    + name
                                    + ".yml");
                            FileConfiguration getOldName = YamlConfiguration
                                    .loadConfiguration(namefile);
                            boolean Password = false;
                            try {
                                Password = PasswordHash.validatePassword(pass,
                                        getPasswords.getString(name));
                            } catch (NoSuchAlgorithmException
                                    | InvalidKeySpecException e2) {
                                e2.printStackTrace();
                                player.sendMessage(ChatColor.RED
                                        + "An Error Stopped Us From Checking Your Password, Please Try Again Later");
                            }
                            if (namefile.exists() && Password) {
                                String uuid = getOldName.getString("UUID");
                                File OldUUIDFile = new File(
                                        this.plugin.getDataFolder() + File.separator
                                                + "PlayerData" + File.separator
                                                + uuid + ".yml");
                                file.delete();
                                OldUUIDFile.renameTo(file);
                                getHomes.set("Name", player.getUniqueId()
                                        .toString());
                                try {
                                    getHomes.save(file);
                                } catch (IOException e1) {
                                    e1.printStackTrace();
                                }
                                OldUUIDFile.delete();
                                namefile.delete();
                                try {
                                    getHomes.save(file);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                player.sendMessage("Data Transfered!");
                                getPasswords.set(player.getName(),
                                        getPasswords.getString(name));
                                getPasswords.set(name, null);
                            } else {
                                player.sendMessage("That Name Or Password Was Incorrect!");
                            }
                        }
                    }
                } else {
                    player.sendMessage("This Command Isnt Used As This Is An Online Mode Server");
                }

            }
        } else if (cmd.getName().

                equalsIgnoreCase("homespawn")

                )

        {
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("reload")) {
                    Player p = null;
                    this.plugin.reload(p);
                }
            }

        } else

        {
            sender.sendMessage("You Must Be a Player To Do That");
        }

        return false;
    }

    private void PassHelp(Player player) {
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
}