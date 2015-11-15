package net.lapismc.HomeSpawn;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class HomeSpawnCommand implements CommandExecutor {

    public static YamlConfiguration getMessages = null;
    public static YamlConfiguration getSpawn = null;
    public HomeSpawnCommand cmd;
    private HomeSpawn plugin;

    public HomeSpawnCommand(HomeSpawn plugin) {
        this.plugin = plugin;
    }

    public void showMenu(Player p) {
        File file2 = new File(plugin.getDataFolder().getAbsolutePath()
                + File.separator + "PlayerData" + File.separator
                + "PlayerNames" + File.separator + p.getName() + ".yml");
        FileConfiguration getName = YamlConfiguration.loadConfiguration(file2);
        File Homes = new File(plugin.getDataFolder().getAbsolutePath()
                + File.separator + "PlayerData" + File.separator
                + getName.getString("UUID") + ".yml");
        YamlConfiguration getHomes = YamlConfiguration.loadConfiguration(Homes);
        List<String> homes = getHomes.getStringList(p.getUniqueId().toString()
                + ".list");
        ArrayList<DyeColor> dc = new ArrayList<DyeColor>();
        dc.add(DyeColor.BLACK);
        dc.add(DyeColor.BLUE);
        dc.add(DyeColor.GRAY);
        dc.add(DyeColor.GREEN);
        dc.add(DyeColor.MAGENTA);
        dc.add(DyeColor.ORANGE);
        Random r = new Random(3);
        if (!plugin.HomesListInvs.containsKey(p)) {
            int notrounded = plugin.getConfig().getInt("AdminHomesLimit") / 9;
            Double rounded = Math.ceil(notrounded);
            int slots = rounded.intValue();
            Inventory inv = Bukkit.createInventory(p, 9 * slots, "HomeSpawn Homes");
            plugin.HomesListInvs.put(p, inv);
        }
        for (String home : homes) {
            ItemStack i = new Wool(dc.get(r.nextInt(5))).toItemStack(1);
            ItemMeta im = i.getItemMeta();
            im.setDisplayName(ChatColor.GOLD + home);
            im.setLore(Arrays.asList(ChatColor.GOLD + "Click To Teleport To",
                    ChatColor.RED + home));
            i.setItemMeta(im);
            plugin.HomesListInvs.get(p).addItem(i);
        }
        p.openInventory(plugin.HomesListInvs.get(p));
    }

    private YamlConfiguration GetHome(String player) {
        File file2 = new File(plugin.getDataFolder().getAbsolutePath()
                + File.separator + "PlayerData" + File.separator
                + "PlayerNames" + File.separator + player + ".yml");
        FileConfiguration getName = YamlConfiguration.loadConfiguration(file2);
        File Homes = new File(plugin.getDataFolder().getAbsolutePath()
                + File.separator + "PlayerData" + File.separator
                + getName.getString("UUID") + ".yml");
        YamlConfiguration Gethome = YamlConfiguration.loadConfiguration(Homes);
        return Gethome;
    }

    public void TeleportPlayer(Player p, Location l, String r) {
        if (plugin.getConfig().getInt("TeleportTime") == 0
                || p.hasPermission("homespawn.bypassdelay")) {
            p.teleport(l);
            if (r.equalsIgnoreCase("Spawn")) {
                p.sendMessage(ChatColor.GOLD
                        + getMessages.getString("Spawn.SentToSpawn"));
            } else if (r.equalsIgnoreCase("Home")) {
                p.sendMessage(ChatColor.GOLD
                        + getMessages.getString("Home.SentHome"));
            }
        } else {
            String waitraw = ChatColor.GOLD + getMessages.getString("Wait");
            String Wait = waitraw.replace("{time}", ChatColor.RED
                    + plugin.getConfig().getString("TeleportTime")
                    + ChatColor.GOLD);
            p.sendMessage(Wait);
            plugin.HomeSpawnLocations.put(p, l);
            plugin.HomeSpawnTimeLeft.put(p,
                    plugin.getConfig().getInt("TeleportTime"));
        }

    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd,
                             String commandLabel, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (cmd.getName().equalsIgnoreCase("sethome")) {
                if (player.hasPermission("homespawn.player")) {
                    File file = new File(plugin.getDataFolder()
                            + File.separator + "PlayerData" + File.separator
                            + player.getUniqueId().toString()
                            + ".yml");
                    FileConfiguration getHomes = YamlConfiguration
                            .loadConfiguration(file);
                    try {
                        getHomes.load(file);
                    } catch (IOException | InvalidConfigurationException e) {
                        e.printStackTrace();
                    }
                    if (!getHomes.contains(player.getUniqueId().toString()
                            + ".list")) {
                        getHomes.createSection(player.getUniqueId().toString()
                                + ".list");
                        try {
                            getHomes.save(file);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    List<String> list = getHomes.getStringList(player
                            .getUniqueId().toString() + ".list");
                    if (player.hasPermission("homespawn.vip")
                            && !player.hasPermission("homespawn.admin")) {
                        if (getHomes.getInt(player.getUniqueId().toString()
                                + ".Numb") >= plugin.getConfig().getInt(
                                "VIPHomesLimit")) {
                            player.sendMessage(ChatColor.RED
                                    + getMessages
                                    .getString("Home.LimitReached"));
                            return false;
                        }
                    } else if (player.hasPermission("homespawn.admin")) {
                        if (getHomes.getInt(player.getUniqueId().toString()
                                + ".Numb") >= plugin.getConfig().getInt(
                                "AdminHomesLimit")) {
                            player.sendMessage(ChatColor.RED
                                    + getMessages
                                    .getString("Home.LimitReached"));
                            return false;
                        }
                        if (args.length == 0) {
                            getHomes.createSection(player.getUniqueId()
                                    .toString());
                            if (!getHomes.contains(player.getUniqueId()
                                    .toString() + ".Numb")) {
                                getHomes.createSection(player.getUniqueId()
                                        .toString() + ".Numb");
                                getHomes.set(player.getUniqueId().toString()
                                        + ".Numb", "0");
                            }
                            getHomes.createSection(player.getUniqueId()
                                    .toString() + ".x");
                            getHomes.createSection(player.getUniqueId()
                                    .toString() + ".y");
                            getHomes.createSection(player.getUniqueId()
                                    .toString() + ".z");
                            getHomes.createSection(player.getUniqueId()
                                    .toString() + ".world");
                            getHomes.createSection(player.getUniqueId()
                                    .toString() + ".Yaw");
                            getHomes.createSection(player.getUniqueId()
                                    .toString() + ".Pitch");
                            if (!getHomes.contains("HasHome")) {
                                getHomes.createSection("HasHome");
                            }
                            int HomesNumb = getHomes.getInt(player
                                    .getUniqueId().toString() + ".Numb");
                            if (!getHomes.contains(player.getUniqueId()
                                    .toString() + ".HasHome")
                                    || !getHomes.getString(
                                    player.getUniqueId().toString()
                                            + ".HasHome").equals("Yes")) {
                                getHomes.set(player.getUniqueId().toString()
                                        + ".Numb", HomesNumb + 1);
                            }
                            if (!list.contains("Home")) {
                                list.add("Home");
                                getHomes.set(player.getUniqueId().toString()
                                        + ".list", list);
                            }
                            getHomes.set(
                                    player.getUniqueId().toString() + ".x",
                                    player.getLocation().getBlockX());
                            getHomes.set(
                                    player.getUniqueId().toString() + ".y",
                                    player.getLocation().getBlockY());
                            getHomes.set(
                                    player.getUniqueId().toString() + ".z",
                                    player.getLocation().getBlockZ());
                            getHomes.set(player.getUniqueId().toString()
                                    + ".world", player.getWorld().getName());
                            getHomes.set(player.getUniqueId().toString()
                                    + ".Yaw", player.getLocation().getYaw());
                            getHomes.set(player.getUniqueId().toString()
                                    + ".Pitch", player.getLocation().getPitch());
                            getHomes.set("HasHome", "Yes");
                            player.sendMessage(ChatColor.GOLD
                                    + getMessages.getString("Home.HomeSet"));
                        } else if (args.length == 1) {
                            if (player.hasPermission("homespawn.vip")
                                    || player.hasPermission("homespawn.admin")) {
                                String home = args[0];
                                if (home.equals("Home")) {
                                    player.sendMessage(ChatColor.RED
                                            + "You Cannot Use The Home Name Home, Please Choose Another!");
                                    return true;
                                }
                                getHomes.createSection(home);
                                getHomes.createSection(home + ".x");
                                getHomes.createSection(home + ".y");
                                getHomes.createSection(home + ".z");
                                getHomes.createSection(home + ".world");
                                getHomes.createSection(home + ".Yaw");
                                getHomes.createSection(home + ".Pitch");
                                if (!getHomes.contains(home + ".HasHome")) {
                                    getHomes.createSection(home + ".HasHome");
                                }
                                if (!getHomes.contains(player.getUniqueId()
                                        .toString() + ".Numb")) {
                                    getHomes.createSection(player.getUniqueId()
                                            .toString() + ".Numb");
                                    getHomes.set(player.getUniqueId()
                                            .toString() + ".Numb", "0");
                                }
                                int HomesNumb = getHomes.getInt(player
                                        .getUniqueId().toString() + ".Numb");
                                if (!getHomes.contains(home + ".HasHome")
                                        || !getHomes.get(home + ".HasHome")
                                        .equals("Yes")) {
                                    getHomes.set(player.getUniqueId()
                                                    .toString() + ".Numb",
                                            HomesNumb + 1);
                                }
                                if (!list.contains(home)) {
                                    list.add(home);
                                    getHomes.set(player.getUniqueId()
                                            .toString() + ".list", list);
                                }
                                getHomes.set(home + ".x", player.getLocation()
                                        .getBlockX());
                                getHomes.set(home + ".y", player.getLocation()
                                        .getBlockY());
                                getHomes.set(home + ".z", player.getLocation()
                                        .getBlockZ());
                                getHomes.set(home + ".world", player.getWorld()
                                        .getName());
                                getHomes.set(home + ".Yaw", player
                                        .getLocation().getYaw());
                                getHomes.set(home + ".Pitch", player
                                        .getLocation().getPitch());
                                getHomes.set(home + ".HasHome", "Yes");
                                player.sendMessage(ChatColor.GOLD
                                        + getMessages.getString("Home.HomeSet"));
                            }
                        } else {
                            player.sendMessage(ChatColor.RED
                                    + getMessages.getString("Error.Args+"));
                        }
                        try {
                            getHomes.save(file);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        player.sendMessage(ChatColor.DARK_RED + getMessages.getString("NoPerms"));
                    }
                } else {
                    player.sendMessage(ChatColor.DARK_RED
                            + getMessages.getString("NoPerms"));
                }

            } else if (cmd.getName().equalsIgnoreCase("home")) {
                if (player.hasPermission("homespawn.player")) {
                    File file = new File(plugin.getDataFolder()
                            + File.separator + "PlayerData" + File.separator
                            + player.getUniqueId().toString()
                            + ".yml");
                    FileConfiguration getHomes = YamlConfiguration
                            .loadConfiguration(file);
                    File file2 = new File(plugin.getDataFolder()
                            .getAbsolutePath()
                            + File.separator
                            + "Messages.yml");
                    FileConfiguration getMessages = YamlConfiguration
                            .loadConfiguration(file2);
                    if (!getHomes.contains(player.getUniqueId().toString()
                            + ".list")) {
                        getHomes.createSection(player.getUniqueId().toString()
                                + ".list");
                        try {
                            getHomes.save(file);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    List<String> list = getHomes.getStringList(player
                            .getUniqueId().toString() + ".list");
                    try {
                        getHomes.load(file);
                    } catch (IOException | InvalidConfigurationException e) {
                        e.printStackTrace();
                    }
                    if (args.length == 0) {
                        if (file != null
                                && getHomes.getString("HasHome")
                                .equalsIgnoreCase("yes")) {
                            int x = getHomes.getInt(player.getUniqueId()
                                    .toString() + ".x");
                            int y = getHomes.getInt(player.getUniqueId()
                                    .toString() + ".y");
                            int z = getHomes.getInt(player.getUniqueId()
                                    .toString() + ".z");
                            float yaw = getHomes.getInt(player.getUniqueId()
                                    .toString() + ".Yaw");
                            float pitch = getHomes.getInt(player.getUniqueId()
                                    .toString() + ".Pitch");
                            String cworld = getHomes.getString(player
                                    .getUniqueId().toString() + ".world");
                            World world = plugin.getServer().getWorld(cworld);
                            Location home = new Location(world, x, y, z, yaw,
                                    pitch);
                            home.add(0.5, 0, 0.5);
                            TeleportPlayer(player, home, "Home");
                        } else {
                            player.sendMessage(ChatColor.RED
                                    + getMessages.getString("Home.NoHomeSet"));
                        }
                    } else if (args.length == 1) {
                        String home = args[0];
                        if (getHomes.contains(home + ".HasHome")) {
                            if (!getHomes.getString(home + ".HasHome")
                                    .equalsIgnoreCase("yes")) {
                                player.sendMessage(ChatColor.RED
                                        + "A home with this name does not exist!");
                                if (getHomes.getInt(player.getUniqueId()
                                        .toString() + ".Numb") > 0) {
                                    if (!list.isEmpty()) {
                                        String list2 = list.toString();
                                        String list3 = list2.replace("[", " ");
                                        String StringList = list3.replace("]",
                                                " ");
                                        player.sendMessage(ChatColor.GOLD
                                                + "Your Current Homes Are:");
                                        player.sendMessage(ChatColor.RED
                                                + StringList);
                                    } else {
                                        player.sendMessage(ChatColor.DARK_RED
                                                + getMessages
                                                .getString("Home.NoHomeSet"));
                                    }
                                } else {
                                    player.sendMessage(ChatColor.DARK_RED
                                            + getMessages
                                            .getString("Home.NoHomeSet"));
                                }
                                return false;
                            }

                            if (getHomes.getString(home + ".HasHome")
                                    .equalsIgnoreCase("yes")) {
                                int x = getHomes.getInt(home + ".x");
                                int y = getHomes.getInt(home + ".y");
                                int z = getHomes.getInt(home + ".z");
                                float yaw = getHomes.getInt(home + ".Yaw");
                                float pitch = getHomes.getInt(home + ".Pitch");
                                String cworld = getHomes.getString(home
                                        + ".world");
                                World world = plugin.getServer().getWorld(
                                        cworld);
                                Location home2 = new Location(world, x, y, z,
                                        yaw, pitch);
                                home2.add(0.5, 0, 0.5);
                                TeleportPlayer(player, home2, "Home");
                            }
                        } else {
                            player.sendMessage(ChatColor.RED
                                    + "A home with this name does not exist!");
                            if (getHomes.getInt(player.getUniqueId().toString()
                                    + ".Numb") > 0) {
                                if (!list.isEmpty()) {
                                    String list2 = list.toString();
                                    String list3 = list2.replace("[", " ");
                                    String StringList = list3.replace("]", " ");
                                    player.sendMessage(ChatColor.GOLD
                                            + "Your Current Homes Are:");
                                    player.sendMessage(ChatColor.RED
                                            + StringList);
                                } else {
                                    player.sendMessage(ChatColor.DARK_RED
                                            + getMessages
                                            .getString("Home.NoHomeSet"));
                                }
                            } else {
                                player.sendMessage(ChatColor.DARK_RED
                                        + getMessages
                                        .getString("Home.NoHomeSet"));
                            }
                            return false;
                        }
                    }
                } else {
                    player.sendMessage(ChatColor.DARK_RED
                            + getMessages.getString("NoPerms"));
                }

            } else if (cmd.getName().equalsIgnoreCase("delhome")) {
                if (player.hasPermission("homespawn.player")) {
                    File file = new File(plugin.getDataFolder()
                            + File.separator + "PlayerData" + File.separator
                            + player.getUniqueId().toString()
                            + ".yml");
                    FileConfiguration getHomes = YamlConfiguration
                            .loadConfiguration(file);
                    File file2 = new File(plugin.getDataFolder()
                            .getAbsolutePath()
                            + File.separator
                            + "Messages.yml");
                    FileConfiguration getMessages = YamlConfiguration
                            .loadConfiguration(file2);
                    if (!getHomes.contains(player.getUniqueId().toString()
                            + ".list")) {
                        getHomes.createSection(player.getUniqueId().toString()
                                + ".list");
                        try {
                            getHomes.save(file);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    List<String> list = getHomes.getStringList(player
                            .getUniqueId().toString() + ".list");
                    try {
                        getHomes.load(file);
                    } catch (IOException | InvalidConfigurationException e) {
                        e.printStackTrace();
                        return true;
                    }
                    if (args.length == 0) {
                        int HomeNumb = getHomes.getInt(player.getUniqueId()
                                .toString() + ".Numb");
                        if (getHomes.getString("HasHome")
                                .equalsIgnoreCase("no")
                                || !getHomes.contains("HasHome")) {
                            player.sendMessage(ChatColor.RED
                                    + getMessages.getString("Home.NoHomeSet"));
                        } else if (getHomes.getString("HasHome")
                                .equalsIgnoreCase("yes")) {
                            player.sendMessage(ChatColor.GOLD
                                    + getMessages.getString("Home.HomeRemoved"));
                            getHomes.set("HasHome", "No");
                            getHomes.set(player.getUniqueId().toString()
                                    + ".Numb", HomeNumb - 1);
                            if (list.contains("Home")) {
                                list.remove("Home");
                                getHomes.set(player.getUniqueId().toString()
                                        + ".list", list);
                            }
                            try {
                                getHomes.save(file);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            player.sendMessage(ChatColor.RED
                                    + getMessages.getString("Home.NoHomeSet"));
                            if (getHomes.getInt(player.getUniqueId().toString()
                                    + ".Numb") > 0) {
                                if (!list.isEmpty()) {
                                    String list2 = list.toString();
                                    String list3 = list2.replace("[", " ");
                                    String StringList = list3.replace("]", " ");
                                    player.sendMessage(ChatColor.GOLD
                                            + "Your Current Homes Are:");
                                    player.sendMessage(ChatColor.RED
                                            + StringList);
                                } else {
                                    player.sendMessage(ChatColor.DARK_RED
                                            + getMessages
                                            .getString("Home.NoHomeSet"));
                                }
                            } else {
                                player.sendMessage(ChatColor.DARK_RED
                                        + getMessages
                                        .getString("Home.NoHomeSet"));
                            }
                        }
                    } else if (args.length == 1) {
                        String home = args[0];
                        int HomeNumb = getHomes.getInt(player.getUniqueId()
                                .toString() + ".Numb");
                        if (!getHomes.contains(home + ".HasHome")
                                || getHomes.getString(home + ".HasHome")
                                .equalsIgnoreCase("no")) {
                            player.sendMessage(ChatColor.RED
                                    + getMessages.getString("Home.NoHomeSet"));
                            if (getHomes.getInt(player.getUniqueId().toString()
                                    + ".Numb") > 0) {
                                if (!list.isEmpty()) {
                                    String list2 = list.toString();
                                    String list3 = list2.replace("[", " ");
                                    String StringList = list3.replace("]", " ");
                                    player.sendMessage(ChatColor.GOLD
                                            + "Your Current Homes Are:");
                                    player.sendMessage(ChatColor.RED
                                            + StringList);
                                } else {
                                    player.sendMessage(ChatColor.DARK_RED
                                            + getMessages
                                            .getString("Home.NoHomeSet"));
                                }
                            } else {
                                player.sendMessage(ChatColor.DARK_RED
                                        + getMessages
                                        .getString("Home.NoHomeSet"));
                            }
                        } else if (getHomes.getString(home + ".HasHome")
                                .equalsIgnoreCase("yes")) {
                            player.sendMessage(ChatColor.GOLD
                                    + getMessages.getString("Home.HomeRemoved"));
                            getHomes.set(home + ".HasHome", "No");
                            getHomes.set(player.getUniqueId().toString()
                                    + ".Numb", HomeNumb - 1);
                            if (list.contains(home)) {
                                list.remove(home);
                                getHomes.set(player.getUniqueId().toString()
                                        + ".list", list);
                            }
                            try {
                                getHomes.save(file);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            player.sendMessage(ChatColor.RED
                                    + getMessages.getString("Home.NoHomeSet"));
                            if (getHomes.getInt(player.getUniqueId().toString()
                                    + ".Numb") > 0) {
                                if (!list.isEmpty()) {
                                    String list2 = list.toString();
                                    String list3 = list2.replace("[", " ");
                                    String StringList = list3.replace("]", " ");
                                    player.sendMessage(ChatColor.GOLD
                                            + "Your Current Homes Are:");
                                    player.sendMessage(ChatColor.RED
                                            + StringList);
                                } else {
                                    player.sendMessage(ChatColor.DARK_RED
                                            + getMessages
                                            .getString("Home.NoHomeSet"));
                                }
                            } else {
                                player.sendMessage(ChatColor.DARK_RED
                                        + getMessages
                                        .getString("Home.NoHomeSet"));
                            }
                        }
                    } else {
                        player.sendMessage(ChatColor.RED
                                + getMessages.getString("Error.Args+"));
                    }
                } else {
                    player.sendMessage(ChatColor.DARK_RED
                            + getMessages.getString("NoPerms"));
                }
            } else if (cmd.getName().equalsIgnoreCase("setspawn")) {
                if (player.hasPermission("homespawn.admin")) {
                    if (args.length == 0) {
                        getSpawn.set("spawn.SpawnSet", "Yes");
                        getSpawn.set("spawn.X", player.getLocation()
                                .getBlockX());
                        getSpawn.set("spawn.Y", player.getLocation()
                                .getBlockY());
                        getSpawn.set("spawn.Z", player.getLocation()
                                .getBlockZ());
                        getSpawn.set("spawn.World", player.getWorld().getName());
                        getSpawn.set("spawn.Yaw", player.getLocation().getYaw());
                        getSpawn.set("spawn.Pitch", player.getLocation()
                                .getPitch());
                        player.sendMessage(ChatColor.GOLD
                                + getMessages.getString("Spawn.SpawnSet"));
                    } else if (args[0].equalsIgnoreCase("new")) {
                        getSpawn.set("spawnnew.SpawnSet", "Yes");
                        getSpawn.set("spawnnew.X", player.getLocation()
                                .getBlockX());
                        getSpawn.set("spawnnew.Y", player.getLocation()
                                .getBlockY());
                        getSpawn.set("spawnnew.Z", player.getLocation()
                                .getBlockZ());
                        getSpawn.set("spawnnew.World", player.getWorld()
                                .getName());
                        getSpawn.set("spawnnew.Yaw", player.getLocation()
                                .getYaw());
                        getSpawn.set("spawnnew.Pitch", player.getLocation()
                                .getPitch());
                        player.sendMessage(ChatColor.GOLD
                                + getMessages.getString("Spawn.SpawnNewSet"));
                    } else {
                        plugin.help(player);
                    }
                    try {
                        getSpawn.save(plugin.spawnFile);
                        plugin.reload("Silent");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else {
                    player.sendMessage(ChatColor.DARK_RED
                            + getMessages.getString("NoPerms"));

                }
            } else if (cmd.getName().equals("spawn")) {
                if (player.hasPermission("homespawn.player")) {
                    if (!getSpawn.contains("spawn.SpawnSet")) {
                        player.sendMessage(ChatColor.RED
                                + getMessages.getString("Spawn.NotSet"));
                        return false;
                    }
                    if (getSpawn.getString("spawn.SpawnSet").equalsIgnoreCase(
                            "yes")) {
                        int x = getSpawn.getInt("spawn.X");
                        int y = getSpawn.getInt("spawn.Y");
                        int z = getSpawn.getInt("spawn.Z");
                        float yaw = getSpawn.getInt("spawn.Yaw");
                        float pitch = getSpawn.getInt("spawn.Pitch");
                        String cworld = getSpawn.getString("spawn.World");
                        World world = plugin.getServer().getWorld(cworld);
                        Location Spawn = new Location(world, x, y, z, yaw,
                                pitch);
                        Spawn.add(0.5, 0, 0.5);
                        TeleportPlayer(player, Spawn, "Spawn");
                    } else {
                        player.sendMessage(ChatColor.RED
                                + getMessages.getString("Spawn.NotSet"));
                    }
                } else {
                    player.sendMessage(ChatColor.DARK_RED
                            + getMessages.getString("NoPerms"));
                }

            } else if (cmd.getName().equalsIgnoreCase("delspawn")) {
                if (player.hasPermission("homespawn.admin")) {
                    if (getSpawn.getString("spawn.SpawnSet") == "No"
                            || !getSpawn.contains("spawn.SpawnSet")) {
                        player.sendMessage(ChatColor.RED
                                + getMessages.getString("Spawn.NotSet"));
                    } else if (getSpawn.getString("spawn.SpawnSet")
                            .equalsIgnoreCase("Yes")) {
                        getSpawn.set("spawn.SpawnSet", "No");
                        player.sendMessage(ChatColor.GOLD
                                + getMessages.getString("Spawn.Removed"));
                        try {
                            getSpawn.save(plugin.spawnFile);
                            plugin.reload("silent");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                } else {
                    player.sendMessage(ChatColor.DARK_RED
                            + getMessages.getString("NoPerms"));
                }
            } else if (cmd.getName().equalsIgnoreCase("homeslist")) {
                if (plugin.getConfig().getBoolean("InventoryMenu")) {
                    showMenu(player);
                    return true;
                } else {
                    File file = new File(plugin.getDataFolder()
                            + File.separator + "PlayerData" + File.separator
                            + player.getUniqueId().toString()
                            + ".yml");
                    FileConfiguration getHomes = YamlConfiguration
                            .loadConfiguration(file);
                    File file2 = new File(plugin.getDataFolder()
                            .getAbsolutePath()
                            + File.separator
                            + "Messages.yml");
                    FileConfiguration getMessages = YamlConfiguration
                            .loadConfiguration(file2);
                    try {
                        getHomes.load(file);
                    } catch (IOException | InvalidConfigurationException e) {
                        e.printStackTrace();
                    }
                    if (!getHomes.contains(player.getUniqueId().toString()
                            + ".list")) {
                        getHomes.createSection(player.getUniqueId().toString()
                                + ".list");
                    }
                    List<String> list = getHomes.getStringList(player
                            .getUniqueId().toString() + ".list");
                    if (getHomes.getInt(player.getUniqueId().toString()
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
                                    + getMessages.getString("Home.NoHomeSet"));
                        }
                    } else {
                        player.sendMessage(ChatColor.DARK_RED
                                + getMessages.getString("Home.NoHomeSet"));
                    }
                }
            } else if (cmd.getName().equalsIgnoreCase("homespawn")) {
                if (args.length == 0) {
                    player.sendMessage(ChatColor.GOLD + "---------------"
                            + ChatColor.RED + "Homespawn" + ChatColor.GOLD
                            + "---------------");
                    player.sendMessage(ChatColor.RED + "Author:"
                            + ChatColor.GOLD + " Dart2112");
                    player.sendMessage(ChatColor.RED + "Version: "
                            + ChatColor.GOLD
                            + plugin.getDescription().getVersion());
                    player.sendMessage(ChatColor.RED + "Bukkit Dev:"
                            + ChatColor.GOLD + " http://goo.gl/2Selqa");
                    player.sendMessage(ChatColor.RED
                            + "Use /homespawn Help For Commands!");
                    player.sendMessage(ChatColor.GOLD
                            + "-----------------------------------------");
                } else if (args.length == 1) {
                    if (args[0].equalsIgnoreCase("reload")) {
                        if (player.hasPermission("homespawn.admin")) {
                            try {
                                plugin.reload(player);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            player.sendMessage(ChatColor.RED
                                    + "You Dont Have Permission To Do That");
                        }
                    } else if (args[0].equalsIgnoreCase("help")) {
                        plugin.help(player);
                    }
                } else {
                    player.sendMessage("That Is Not A Recognised Command, Use /homespawn help For Commands");
                }
            } else if (cmd.getName().equalsIgnoreCase("homepassword")) {
                File file = new File(plugin.getDataFolder() + File.separator
                        + "PlayerData" + File.separator
                        + player.getUniqueId().toString() + ".yml");
                FileConfiguration getHomes = YamlConfiguration
                        .loadConfiguration(file);
                File file3 = new File(plugin.getDataFolder() + File.separator
                        + "PlayerData" + File.separator + "Passwords.yml");
                FileConfiguration getPasswords = YamlConfiguration
                        .loadConfiguration(file3);
                if (!plugin.getServer().getOnlineMode()) {
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
                        PassHelp(player);
                    } else if (args.length == 3) {
                        String string = args[0];
                        if (string.equalsIgnoreCase("transfer")) {
                            String pass = args[2];
                            String name = args[1];
                            File namefile = new File(plugin.getDataFolder()
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
                                player.sendMessage(ChatColor.RED
                                        + "An Error Stopped Us From Checking Your Password, Please Try Again Later");
                            }
                            if (namefile.exists() && Password) {
                                String uuid = getOldName.getString("UUID");
                                File OldUUIDFile = new File(
                                        plugin.getDataFolder() + File.separator
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

            } else if (cmd.getName().equalsIgnoreCase("setglobalhome")) {
                if (player.hasPermission("homespawn.admin")) {
                    if (args.length == 0) {
                        player.sendMessage(ChatColor.RED
                                + "You need to specify a name");
                    } else if (args.length == 1) {
                        File file = new File(plugin.getDataFolder()
                                .getAbsolutePath()
                                + File.separator
                                + "GlobalHomes.yml");
                        FileConfiguration getGlobalHomes = YamlConfiguration
                                .loadConfiguration(file);
                        String home = args[1];
                        getGlobalHomes.createSection(home);
                        getGlobalHomes.createSection(home + ".x");
                        getGlobalHomes.createSection(home + ".y");
                        getGlobalHomes.createSection(home + ".z");
                        getGlobalHomes.createSection(home + ".world");
                        getGlobalHomes.createSection(home + ".Yaw");
                        getGlobalHomes.createSection(home + ".Pitch");
                        try {
                            getGlobalHomes.save(file);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        getGlobalHomes.set(home + ".x", player.getLocation()
                                .getBlockX());
                        getGlobalHomes.set(home + ".y", player.getLocation()
                                .getBlockY());
                        getGlobalHomes.set(home + ".z", player.getLocation()
                                .getBlockZ());
                        getGlobalHomes.set(home + ".world", player.getWorld()
                                .getName());
                        getGlobalHomes.set(home + ".Yaw", player.getLocation()
                                .getYaw());
                        getGlobalHomes.set(home + ".Pitch", player
                                .getLocation().getPitch());
                        if (!getGlobalHomes.contains("list")) {
                            List<String> list = new ArrayList<String>();
                            list.add("...");
                            getGlobalHomes.set("list", list);
                            list.remove("...");
                            getGlobalHomes.set("list", list);
                        }
                        List<String> list = getGlobalHomes
                                .getStringList("list");
                        list.add(home);
                        getGlobalHomes.set("list", list);
                        try {
                            getGlobalHomes.save(file);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + "To much infomation");
                        player.sendMessage(ChatColor.RED
                                + "Usage: /setglobalhome (home name)");
                    }
                } else {
                    player.sendMessage(ChatColor.DARK_RED
                            + getMessages.getString("NoPerms"));
                }
            }
            } else if (cmd.getName().equalsIgnoreCase("homespawn")) {
                if (args.length == 1) {
                    if (args[0].equalsIgnoreCase("reload")) {
                        Player p = null;
                        try {
                            plugin.reload(p);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

        } else {
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
