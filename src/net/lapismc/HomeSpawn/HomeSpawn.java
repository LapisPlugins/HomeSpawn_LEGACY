package net.lapismc.HomeSpawn;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Logger;

import net.gravitydevelopment.updater.Updater;
import net.gravitydevelopment.updater.Updater.UpdateResult;
import net.gravitydevelopment.updater.Updater.UpdateType;

import org.mcstats.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public class HomeSpawn extends JavaPlugin implements Listener {

	public HomeSpawn plugin;
	public Permission PlayerPerm = new Permission("homespawn.player");
	public Permission AdminPerm = new Permission("homespawn.admin");
	public Permission VIPPerm = new Permission("homespawn.vip");
	HashMap<Player, Location> Locations = new HashMap<Player, Location>();
	HashMap<Player, Integer> TimeLeft = new HashMap<Player, Integer>();
	public HomeSpawnListener pl;
	public final Logger logger = this.getLogger();
	public final ConsoleCommandSender console = Bukkit.getConsoleSender();

	@Override
	public void onEnable() {
		Enable();
		try {
			Configs();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Update();
		Metrics();
		Commands();
		CommandDelay();
	}

	private void Metrics() {
		if (getConfig().getBoolean("Metrics")) {
			try {
				Metrics metrics = new Metrics(this);
				metrics.start();
			} catch (IOException e) {
				e.printStackTrace();
				console.sendMessage("[HomeSpawn] Metrics Failed To Start!");
			}
		} else {
			getLogger()
					.info("Metrics wasnt started because it is disabled in the config!");
		}
	}

	private void Update() {
		if (getConfig().getBoolean("AutoUpdate")) {
			Updater updater = new Updater(this, 86785, this.getFile(),
					UpdateType.DEFAULT, true);
			updatecheck(updater);
		} else {
			Updater updater = new Updater(this, 86785, this.getFile(),
					UpdateType.NO_DOWNLOAD, true);
			updatecheck(updater);
		}
	}

	private void updatecheck(Updater updater) {
		File file = new File(this.getDataFolder().getAbsolutePath()
				+ File.separator + "Update.yml");
		FileConfiguration getUpdate = YamlConfiguration.loadConfiguration(file);
		if (updater.getResult() == UpdateResult.SUCCESS) {
			this.getLogger().info(
					"Updated, Reload or restart to install the update!");
		} else if (updater.getResult() == UpdateResult.NO_UPDATE) {
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
		} else if (updater.getResult() == UpdateResult.UPDATE_AVAILABLE
				&& updater.getResult() != UpdateResult.SUCCESS) {
			this.getLogger().info(
					"An update is Available for HomeSpawn, It can be downloaded from,"
							+ " dev.bukkit.org/bukkit-plugins/homespawn");
			if (file.exists()) {
				if (!getConfig().getBoolean("AutoUpdate")) {
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
		Disable();
	}

	public void Enable() {
		logger.info(" V." + getDescription().getVersion()
				+ " Has Been Enabled!");
		PluginManager pm = getServer().getPluginManager();
		pl = new HomeSpawnListener(this);
		pm.registerEvents(this.pl, this);
	}

	public void Disable() {
		console.sendMessage("[HomeSpawn] Plugin Has Been Disabled!");
		HandlerList.unregisterAll();
	}

	public void Configs() throws IOException {
		saveDefaultConfig();
		saveConfig();
		createSpawn();
		createGlobalHomes();
		createPlayerData();
		createMessages();
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
				getMessages.createSection("Error.Args");
				getMessages.createSection("Error.Args+");
				getMessages.createSection("Error.Args-");
				getMessages.save(file2);
				setDefaultMessages();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void createGlobalHomes() throws IOException {
		File file = new File(getDataFolder().getAbsolutePath() + File.separator
				+ "GlobalHomes.yml");
		if (!file.exists()) {
			file.createNewFile();
			FileConfiguration getGlobalHomes = YamlConfiguration
					.loadConfiguration(file);
			getGlobalHomes.set("list", null);
			getGlobalHomes.save(file);
		}
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
			getMessages.set("Error.Args+", "Too Much Infomation!");
			getMessages.set("Error.Args-", "Not Enough Infomation");
			getMessages.set("Error.Args", "Too Little or Too Much Infomation");
			try {
				getMessages.save(file2);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			createMessages();
		}

	}

	private void createPlayerData() {
		File theDir = new File(this.getDataFolder().getAbsolutePath()
				+ File.separator + "PlayerData");
		File theDir1 = new File(this.getDataFolder().getAbsolutePath()
				+ File.separator + "PlayerData" + File.separator
				+ "PlayerNames");
		if (!theDir.exists()) {
			console.sendMessage("[HomeSpawn] Creating PlayerData Directory!");
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
				try {
					getSpawn.save(file);
				} catch (IOException e) {
					e.printStackTrace();
				}
			} catch (IOException e) {
				console.sendMessage("[HomeSpawn] Couldn't create spawn file!");
				e.printStackTrace();
			}
		}

	}

	public void spawnnew(Player player) {
		File file = new File(this.getDataFolder().getAbsolutePath()
				+ File.separator + "Spawn.yml");
		FileConfiguration getSpawn = YamlConfiguration.loadConfiguration(file);
		try {
			getSpawn.load(file);
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
		if (getSpawn.get("spawnnew.SpawnSet") != null
				&& getSpawn.getString("spawnnew.SpawnSet").equalsIgnoreCase(
						"yes")) {
			int x = getSpawn.getInt("spawnnew.X");
			int y = getSpawn.getInt("spawnnew.Y");
			int z = getSpawn.getInt("spawnnew.Z");
			float yaw = getSpawn.getInt("spawnnew.Yaw");
			float pitch = getSpawn.getInt("spawnnew.Pitch");
			String cworld = getSpawn.getString("spawnnew.World");
			World world = getServer().getWorld(cworld);
			Location spawnnew = new Location(world, x, y, z, yaw, pitch);
			spawnnew.add(0.5, 0, 0.5);
			player.teleport(spawnnew);
			console.sendMessage("[HomeSpawn] Player " + player.getName()
					+ " Was Sent To New Spawn");
		} else {
			console.sendMessage("[HomeSpawn] There Is No New Spawn Set And Therefore The Player Wasnt Sent To The New Spawn");
		}
	}

	public void reload(Player player) throws IOException {
		if (player != null) {
			Configs();
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
			player.sendMessage(ChatColor.GOLD + "-----------------------"
					+ ChatColor.RED + "Homespawn" + ChatColor.GOLD
					+ "-----------------------");
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
						+ "---------------------------------------------------------");
				return;
			} else {
				player.sendMessage(ChatColor.GOLD
						+ "---------------------------------------------------------");
			}

		} else {
			return;
		}
	}

	public void CommandDelay() {
		if (!getConfig().contains("TeleportTime")) {
			getConfig().createSection("TeleportTime");
			saveConfig();
			getConfig().set("TeleportTime", 0);
		}
		if (!(getConfig().getInt("TeleportTime") == 0)) {
			BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
			scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
				@Override
				public void run() {
					if (!TimeLeft.isEmpty()) {
						for (Player p : TimeLeft.keySet()) {
							if (Locations.get(p) == null) {
								TimeLeft.remove(p);
								Locations.remove(p);
							}
							for (int Time : TimeLeft.values()) {
								int NewTime = Time - 1;
								if (NewTime > 0) {
									TimeLeft.put(p, NewTime);
								} else if (NewTime <= 0) {
									Location Tele = Locations.get(p);
									if (!Tele.equals(null)) {
										p.teleport(Tele);
										p.sendMessage(ChatColor.GOLD
												+ "Teleporting...");
										TimeLeft.remove(p);
										Locations.remove(p);
									} else {
										TimeLeft.remove(p);
										Locations.remove(p);
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

	public void Commands() {
		this.getCommand("home").setExecutor(new HomeSpawnCommand(this));
		this.getCommand("sethome").setExecutor(new HomeSpawnCommand(this));
		this.getCommand("delhome").setExecutor(new HomeSpawnCommand(this));
		// this.getCommand("globalhome").setExecutor(new
		// HomeSpawnCommand(this));
		// this.getCommand("setglobalhome")
		// .setExecutor(new HomeSpawnCommand(this));
		// this.getCommand("delglobalhome")
		// .setExecutor(new HomeSpawnCommand(this));
		this.getCommand("spawn").setExecutor(new HomeSpawnCommand(this));
		this.getCommand("setspawn").setExecutor(new HomeSpawnCommand(this));
		this.getCommand("delspawn").setExecutor(new HomeSpawnCommand(this));
		this.getCommand("homespawn").setExecutor(new HomeSpawnCommand(this));
		this.getCommand("homeslist").setExecutor(new HomeSpawnCommand(this));
	}
}