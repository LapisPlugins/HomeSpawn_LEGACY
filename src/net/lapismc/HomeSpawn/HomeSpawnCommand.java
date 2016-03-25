package net.lapismc.HomeSpawn;

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

    public HomeSpawnCommand(HomeSpawn plugin) {
        this.plugin = plugin;
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

    private void TeleportPlayer(Player p, Location l, String r) {
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
                HashMap<String, Integer> perms = plugin.Permissions.get(plugin.PlayerPermission.get(player.getUniqueId()));
                if (perms == null) {
                    player.sendMessage("Perms is null");
                }
                if (plugin == null) {
                    player.sendMessage("Plugin is null");
                }
                if (plugin.PlayerPermission.get(player.getUniqueId()) == null) {
                    player.sendMessage("PlayerPermission is null");
                }
                if (plugin.Permissions.get(plugin.PlayerPermission.get(player.getUniqueId())) == null) {
                    player.sendMessage("Permissions in general is null");
                }
                if (plugin.Permissions.get(plugin.PlayerPermission.get(player.getUniqueId())).get("Homes") == null) {
                    player.sendMessage("Homes is null");
                }
                UUID uuid = this.plugin.PlayertoUUID.get(player.getName());
                YamlConfiguration getHomes = this.plugin.HomeConfigs.get(uuid);
                if (!getHomes.contains(player.getUniqueId()
                        + ".list")) {
                    getHomes.createSection(player.getUniqueId()
                            + ".list");
                    this.plugin.savePlayerData(uuid);
                }
                List<String> list = getHomes.getStringList(player
                        .getUniqueId() + ".list");
                if (getHomes.getInt(player.getUniqueId()
                        + ".Numb") >= perms.get("homes")) {
                    player.sendMessage(ChatColor.RED
                            + HomeSpawnCommand.getMessages
                            .getString("Home.LimitReached"));
                    return false;
                }

                if (args.length == 0) {
                    getHomes.createSection(player.getUniqueId()
                            .toString());
                    if (!getHomes.contains(player.getUniqueId() + ".Numb")) {
                        getHomes.createSection(player.getUniqueId() + ".Numb");
                        getHomes.set(player.getUniqueId()
                                + ".Numb", "0");
                    }
                    getHomes.createSection(player.getUniqueId() + ".x");
                    getHomes.createSection(player.getUniqueId() + ".y");
                    getHomes.createSection(player.getUniqueId() + ".z");
                    getHomes.createSection(player.getUniqueId() + ".world");
                    getHomes.createSection(player.getUniqueId() + ".Yaw");
                    getHomes.createSection(player.getUniqueId() + ".Pitch");
                    if (!getHomes.contains("HasHome")) {
                        getHomes.createSection("HasHome");
                    }
                    int HomesNumb = getHomes.getInt(player
                            .getUniqueId() + ".Numb");
                    if (!getHomes.contains(player.getUniqueId() + ".HasHome")
                            || !getHomes.getString(
                            player.getUniqueId()
                                    + ".HasHome").equals("Yes")) {
                        getHomes.set(player.getUniqueId()
                                + ".Numb", HomesNumb + 1);
                    }
                    if (!list.contains("Home")) {
                        list.add("Home");
                        getHomes.set(player.getUniqueId()
                                + ".list", list);
                    }
                    getHomes.set(
                            player.getUniqueId() + ".x",
                            player.getLocation().getBlockX());
                    getHomes.set(
                            player.getUniqueId() + ".y",
                            player.getLocation().getBlockY());
                    getHomes.set(
                            player.getUniqueId() + ".z",
                            player.getLocation().getBlockZ());
                    getHomes.set(player.getUniqueId()
                            + ".world", player.getWorld().getName());
                    getHomes.set(player.getUniqueId()
                            + ".Yaw", player.getLocation().getYaw());
                    getHomes.set(player.getUniqueId()
                            + ".Pitch", player.getLocation().getPitch());
                    getHomes.set("HasHome", "Yes");
                    player.sendMessage(ChatColor.GOLD
                            + HomeSpawnCommand.getMessages.getString("Home.HomeSet"));
                } else if (args.length == 1) {
                    if (perms.get("set custom homes") == 1) {
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
                        if (!getHomes.contains(player.getUniqueId() + ".Numb")) {
                            getHomes.createSection(player.getUniqueId() + ".Numb");
                            getHomes.set(player.getUniqueId() + ".Numb", "0");
                        }
                        int HomesNumb = getHomes.getInt(player
                                .getUniqueId() + ".Numb");
                        if (!getHomes.contains(home + ".HasHome")
                                || !getHomes.get(home + ".HasHome")
                                .equals("Yes")) {
                            getHomes.set(player.getUniqueId() + ".Numb",
                                    HomesNumb + 1);
                        }
                        if (!list.contains(home)) {
                            list.add(home);
                            getHomes.set(player.getUniqueId() + ".list", list);
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
                                + HomeSpawnCommand.getMessages.getString("Home.HomeSet"));
                    } else {
                        player.sendMessage(ChatColor.DARK_RED + HomeSpawnCommand.getMessages.getString("NoPerms"));
                    }
                } else {
                    player.sendMessage(ChatColor.RED
                            + HomeSpawnCommand.getMessages.getString("Error.Args+"));
                }
                this.plugin.savePlayerData(uuid);

            } else if (cmd.getName().equalsIgnoreCase("home")) {
                HashMap<String, Integer> perms = plugin.Permissions.get(plugin.PlayerPermission.get(player.getUniqueId()));
                UUID uuid = this.plugin.PlayertoUUID.get(player.getName());
                YamlConfiguration getHomes = this.plugin.HomeConfigs.get(uuid);
                if (!getHomes.contains(player.getUniqueId()
                        + ".list")) {
                    getHomes.createSection(player.getUniqueId()
                            + ".list");
                    this.plugin.savePlayerData(uuid);
                }
                List<String> list = getHomes.getStringList(player
                        .getUniqueId() + ".list");
                if (args.length == 0) {
                    if (getHomes.getString("HasHome")
                            .equalsIgnoreCase("yes")) {
                        int x = getHomes.getInt(player.getUniqueId() + ".x");
                        int y = getHomes.getInt(player.getUniqueId() + ".y");
                        int z = getHomes.getInt(player.getUniqueId() + ".z");
                        float yaw = getHomes.getInt(player.getUniqueId() + ".Yaw");
                        float pitch = getHomes.getInt(player.getUniqueId() + ".Pitch");
                        String cworld = getHomes.getString(player
                                .getUniqueId() + ".world");
                        World world = this.plugin.getServer().getWorld(cworld);
                        Location home = new Location(world, x, y, z, yaw,
                                pitch);
                        home.add(0.5, 0, 0.5);
                        this.TeleportPlayer(player, home, "Home");
                    } else {
                        player.sendMessage(ChatColor.RED
                                + HomeSpawnCommand.getMessages.getString("Home.NoHomeSet"));
                    }
                } else if (args.length == 1) {
                    String home = args[0];
                    if (getHomes.contains(home + ".HasHome")) {
                        if (!getHomes.getString(home + ".HasHome")
                                .equalsIgnoreCase("yes")) {
                            player.sendMessage(ChatColor.RED
                                    + "A home with this name does not exist!");
                            if (getHomes.getInt(player.getUniqueId() + ".Numb") > 0) {
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
                                            + HomeSpawnCommand.getMessages
                                            .getString("Home.NoHomeSet"));
                                }
                            } else {
                                player.sendMessage(ChatColor.DARK_RED
                                        + HomeSpawnCommand.getMessages
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
                            World world = this.plugin.getServer().getWorld(
                                    cworld);
                            Location home2 = new Location(world, x, y, z,
                                    yaw, pitch);
                            home2.add(0.5, 0, 0.5);
                            this.TeleportPlayer(player, home2, "Home");
                        }
                    } else {
                        player.sendMessage(ChatColor.RED
                                + "A home with this name does not exist!");
                        if (getHomes.getInt(player.getUniqueId()
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
                                        + HomeSpawnCommand.getMessages
                                        .getString("Home.NoHomeSet"));
                            }
                        } else {
                            player.sendMessage(ChatColor.DARK_RED
                                    + HomeSpawnCommand.getMessages
                                    .getString("Home.NoHomeSet"));
                        }
                        return false;
                    }
                }

            } else if (cmd.getName().equalsIgnoreCase("delhome")) {
                UUID uuid = this.plugin.PlayertoUUID.get(player.getName());
                YamlConfiguration getHomes = this.plugin.HomeConfigs.get(uuid);
                if (!getHomes.contains(player.getUniqueId()
                        + ".list")) {
                    getHomes.createSection(player.getUniqueId()
                            + ".list");
                    this.plugin.savePlayerData(uuid);
                }
                List<String> list = getHomes.getStringList(player
                        .getUniqueId() + ".list");
                if (args.length == 0) {
                    int HomeNumb = getHomes.getInt(player.getUniqueId() + ".Numb");
                    if (getHomes.getString("HasHome")
                            .equalsIgnoreCase("no")
                            || !getHomes.contains("HasHome")) {
                        player.sendMessage(ChatColor.RED
                                + HomeSpawnCommand.getMessages.getString("Home.NoHomeSet"));
                    } else if (getHomes.getString("HasHome")
                            .equalsIgnoreCase("yes")) {
                        player.sendMessage(ChatColor.GOLD
                                + HomeSpawnCommand.getMessages.getString("Home.HomeRemoved"));
                        getHomes.set("HasHome", "No");
                        getHomes.set(player.getUniqueId()
                                + ".Numb", HomeNumb - 1);
                        if (list.contains("Home")) {
                            list.remove("Home");
                            getHomes.set(player.getUniqueId()
                                    + ".list", list);
                        }
                        this.plugin.savePlayerData(uuid);
                    } else {
                        player.sendMessage(ChatColor.RED
                                + HomeSpawnCommand.getMessages.getString("Home.NoHomeSet"));
                        if (getHomes.getInt(player.getUniqueId()
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
                                        + HomeSpawnCommand.getMessages
                                        .getString("Home.NoHomeSet"));
                            }
                        } else {
                            player.sendMessage(ChatColor.DARK_RED
                                    + HomeSpawnCommand.getMessages
                                    .getString("Home.NoHomeSet"));
                        }
                    }
                } else if (args.length == 1) {
                    String home = args[0];
                    int HomeNumb = getHomes.getInt(player.getUniqueId() + ".Numb");
                    if (!getHomes.contains(home + ".HasHome")
                            || getHomes.getString(home + ".HasHome")
                            .equalsIgnoreCase("no")) {
                        player.sendMessage(ChatColor.RED
                                + HomeSpawnCommand.getMessages.getString("Home.NoHomeSet"));
                        if (getHomes.getInt(player.getUniqueId()
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
                                        + HomeSpawnCommand.getMessages
                                        .getString("Home.NoHomeSet"));
                            }
                        } else {
                            player.sendMessage(ChatColor.DARK_RED
                                    + HomeSpawnCommand.getMessages
                                    .getString("Home.NoHomeSet"));
                        }
                    } else if (getHomes.getString(home + ".HasHome")
                            .equalsIgnoreCase("yes")) {
                        player.sendMessage(ChatColor.GOLD
                                + HomeSpawnCommand.getMessages.getString("Home.HomeRemoved"));
                        getHomes.set(home + ".HasHome", "No");
                        getHomes.set(player.getUniqueId()
                                + ".Numb", HomeNumb - 1);
                        if (list.contains(home)) {
                            list.remove(home);
                            getHomes.set(player.getUniqueId()
                                    + ".list", list);
                        }
                        this.plugin.savePlayerData(uuid);
                    } else {
                        player.sendMessage(ChatColor.RED
                                + HomeSpawnCommand.getMessages.getString("Home.NoHomeSet"));
                        if (getHomes.getInt(player.getUniqueId()
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
                                        + HomeSpawnCommand.getMessages
                                        .getString("Home.NoHomeSet"));
                            }
                        } else {
                            player.sendMessage(ChatColor.DARK_RED
                                    + HomeSpawnCommand.getMessages
                                    .getString("Home.NoHomeSet"));
                        }
                    }
                } else {
                    player.sendMessage(ChatColor.RED
                            + HomeSpawnCommand.getMessages.getString("Error.Args+"));
                }
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
                    if (HomeSpawnCommand.getSpawn.getString("spawn.SpawnSet") == "No"
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
                                    + "You Dont Have Permission To Do That");
                        }
                    } else if (args[0].equalsIgnoreCase("help")) {
                        this.plugin.help(player);
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