package net.lapismc.HomeSpawn;

import java.io.File;
import java.io.IOException;

import net.gravitydevelopment.updater.updater;
import net.gravitydevelopment.updater.updater.UpdateResult;
import net.gravitydevelopment.updater.updater.UpdateType;

import org.mcstats.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.java.JavaPlugin;

public class HomeSpawn extends JavaPlugin implements Listener {

	public HomeSpawn plugin;
	public Permission PlayerPerm = new Permission("homespawn.player");
	public Permission AdminPerm = new Permission("homespawn.Admin");

	@Override
	public void onEnable() {
		Enable();
		Configs();
		Update();
		Metrics();
	}

	private void Metrics() {
		try {
			Metrics metrics = new Metrics(this);
			metrics.start();
		} catch (IOException e) {

		}

	}

	private void Update() {
		updater updater = new updater(this, 86785, this.getFile(),
				UpdateType.DEFAULT, true);
		if (updater.getResult() == UpdateResult.SUCCESS) {
			this.getLogger()
					.info("[HomeSpawn] Updated, Reload or restart to install the update!");
		} else if (updater.getResult() == UpdateResult.NO_UPDATE) {
			this.getLogger().info("[HomeSpawn] No Update Avilable");
		} else {
			this.getLogger().severe(
					"ChatColor.RED + [HomeSpawn] Something Went Wrong Updating!");
		}
	}

	@Override
	public void onDisable() {
		Disable();
	}

	@EventHandler(priority = EventPriority.HIGH)
	void PlayerJoinEvent(final PlayerJoinEvent event) {
		Player player = event.getPlayer();
		File file = new File(this.getDataFolder().getAbsolutePath()
				+ File.separator + "PlayerData" + File.separator
				+ player.getUniqueId() + ".yml");
		FileConfiguration getHomes = YamlConfiguration.loadConfiguration(file);
		if (!(file.exists())) {
			try {
				file.createNewFile();
				getHomes.createSection("HasHome");
				getHomes.save(file);
				getHomes.set("HasHome", "No");
				getHomes.save(file);
			} catch (IOException e) {
				e.printStackTrace();
				Bukkit.getConsoleSender().sendMessage(
						"[HomeSpawn] Player Data File Creation Failed!");
				return;
			}

			getHomes.createSection("name");
			getHomes.set("name", player.getName());
			Bukkit.getConsoleSender().sendMessage(
					ChatColor.GOLD
							+ "[HomeSpawn] Blank Config Has Been Created For "
							+ player.getName());
			try {
				getHomes.save(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void Enable() {
		Bukkit.getConsoleSender().sendMessage(
				"[HomeSpawn] V." + getDescription().getVersion()
						+ " Has Been Enabled!");
		getServer().getPluginManager().registerEvents(this, this);
	}

	public void Disable() {
		Bukkit.getConsoleSender().sendMessage(
				"[HomeSpawn] Plugin Has Been Disabled!");
		HandlerList.unregisterAll();
	}

	public void Configs() {
		saveDefaultConfig();
		saveConfig();
		createSpawn();
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
				getMessages.createSection("Spawn");
				getMessages.createSection("Spawn.NotSet");
				getMessages.createSection("Spawn.SpawnSet");
				getMessages.createSection("Spawn.SentToSpawn");
				getMessages.createSection("Spawn.Removed");
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
			getMessages.set("Spawn.NotSet",
					"You First Need To Set a Spawn With /setspawn");
			getMessages.set("Spawn.SpawnSet",
					"Spawn Set, You Can Now Use /spawn");
			getMessages.set("Spawn.SentToSpawn", "Welcome To Spawn");
			getMessages.set("Spawn.Removed", "Spawn Removed!");
			getMessages.set("Error.Args+","Too Much Infomation!");
			getMessages.set("Error.Args-","Not Enough Infomation");
			getMessages.set("Error.Args","Too Little or Too Much Infomation");
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
		if (!theDir.exists()) {
			Bukkit.getConsoleSender().sendMessage(
					"[HomeSpawn] Creating PlayerData Directory!");
			theDir.mkdir();
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
				try {
					getSpawn.save(file);
				} catch (IOException e) {
					e.printStackTrace();
				}
			} catch (IOException e) {
				Bukkit.getConsoleSender().sendMessage(
						"[HomeSpawn] Couldn't create spawn file!");
				e.printStackTrace();
			}
		}

	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd,
			String commandLabel, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;

			if (commandLabel.equalsIgnoreCase("sethome")) {
				if (player.hasPermission("homespawn.player")) {
					File file = new File(getDataFolder().getAbsolutePath()
							+ File.separator + "PlayerData" + File.separator
							+ player.getUniqueId() + ".yml");
					FileConfiguration getHomes = YamlConfiguration
							.loadConfiguration(file);
					File file2 = new File(this.getDataFolder()
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
					if (getHomes.getString("name").equalsIgnoreCase(
							player.getName())) {
						if (args.length == 0) {

							getHomes.createSection(player.getDisplayName());
							getHomes.createSection(player.getDisplayName()
									+ ".x");
							getHomes.createSection(player.getDisplayName()
									+ ".y");
							getHomes.createSection(player.getDisplayName()
									+ ".z");
							getHomes.createSection(player.getDisplayName()
									+ ".world");
							getHomes.createSection(player.getDisplayName()
									+ ".Yaw");
							getHomes.createSection(player.getDisplayName()
									+ ".Pitch");
							getHomes.createSection("HasHome");
							getHomes.set(player.getDisplayName() + ".x", player
									.getLocation().getBlockX());
							getHomes.set(player.getDisplayName() + ".y", player
									.getLocation().getBlockY());
							getHomes.set(player.getDisplayName() + ".z", player
									.getLocation().getBlockZ());
							getHomes.set(player.getDisplayName() + ".world",
									player.getWorld().getName());
							getHomes.set(player.getName() + ".Yaw", player
									.getLocation().getYaw());
							getHomes.set(player.getName() + ".Pitch", player
									.getLocation().getPitch());
							getHomes.set("HasHome", "Yes");
							player.sendMessage(ChatColor.GOLD
									+ getMessages.getString("Home.HomeSet"));
						} else if (args.length == 1) {
							String home = args[0];
							getHomes.createSection(home);
							getHomes.createSection(home + ".x");
							getHomes.createSection(home + ".y");
							getHomes.createSection(home + ".z");
							getHomes.createSection(home + ".world");
							getHomes.createSection(home + ".Yaw");
							getHomes.createSection(home + ".Pitch");
							getHomes.createSection(home + ".HasHome");
							getHomes.set(home + ".x", player.getLocation()
									.getBlockX());
							getHomes.set(home + ".y", player.getLocation()
									.getBlockY());
							getHomes.set(home + ".z", player.getLocation()
									.getBlockZ());
							getHomes.set(home + ".world", player.getWorld()
									.getName());
							getHomes.set(home + ".Yaw", player.getLocation()
									.getYaw());
							getHomes.set(home + ".Pitch", player.getLocation()
									.getPitch());
							getHomes.set(home + ".HasHome", "Yes");
							player.sendMessage(ChatColor.GOLD
									+ getMessages.getString("Home.HomeSet"));
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
						player.sendMessage(ChatColor.RED
								+ "For some reason your name doesnt match"
								+ " the name in your file, please contact an admin!");
					}
				} else {
					player.sendMessage(ChatColor.DARK_RED
							+ "You don't have permission to do that!");
				}

			} else if (commandLabel.equalsIgnoreCase("home")) {
				if (player.hasPermission("homespawn.player")) {
					File file = new File(this.getDataFolder() + File.separator
							+ "PlayerData" + File.separator
							+ player.getUniqueId() + ".yml");
					FileConfiguration getHomes = YamlConfiguration
							.loadConfiguration(file);
					File file2 = new File(this.getDataFolder()
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
					if (args.length == 0) {
						if (getHomes.getString("HasHome").equalsIgnoreCase(
								"yes")) {
							int x = getHomes.getInt(player.getDisplayName()
									+ ".x");
							int y = getHomes.getInt(player.getDisplayName()
									+ ".y");
							int z = getHomes.getInt(player.getDisplayName()
									+ ".z");
							float yaw = getHomes.getInt(player.getName()
									+ ".Yaw");
							float pitch = getHomes.getInt(player.getName()
									+ ".Pitch");
							String cworld = getHomes.getString(player
									.getDisplayName() + ".world");
							World world = getServer().getWorld(cworld);
							Location home = new Location(world, x, y, z, yaw,
									pitch);
							home.add(0.5, 0, 0.5);
							player.sendMessage(ChatColor.GOLD
									+ getMessages.getString("Home.SentHome"));
							player.teleport(home);

						} else {
							player.sendMessage(ChatColor.RED
									+ getMessages.getString("Home.NoHomeSet"));
						}
					} else if (args.length == 1) {
						String home = args[0];
						if (getHomes.contains(home + ".HasHome")) {
							if (!getHomes.getString(home + ".HasHome")
									.equalsIgnoreCase("yes")) {
								player.sendMessage(ChatColor.RED + "A home with this name does not exist!");
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
								World world = getServer().getWorld(cworld);
								Location home2 = new Location(world, x, y, z,
										yaw, pitch);
								home2.add(0.5, 0, 0.5);
								player.sendMessage(ChatColor.GOLD
										+ getMessages
												.getString("Home.SentHome"));
								player.teleport(home2);
							}
						} else {
							player.sendMessage(ChatColor.RED + "A home with this name does not exist!");
							return false;
						}
					}
				} else {
					player.sendMessage(ChatColor.DARK_RED
							+ "You don't have permission to do that!");
				}

			} else if (commandLabel.equalsIgnoreCase("delhome")) {
				if (player.hasPermission("homespawn.player")) {
					File file = new File(getDataFolder() + File.separator
							+ "PlayerData" + File.separator
							+ player.getUniqueId() + ".yml");
					FileConfiguration getHomes = YamlConfiguration
							.loadConfiguration(file);
					File file2 = new File(this.getDataFolder()
							.getAbsolutePath()
							+ File.separator
							+ "Messages.yml");
					FileConfiguration getMessages = YamlConfiguration
							.loadConfiguration(file2);
					try {
						getHomes.load(file);
					} catch (IOException | InvalidConfigurationException e) {
						e.printStackTrace();
						return true;
					}
					if (args.length == 0) {
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
							try {
								getHomes.save(file);
							} catch (IOException e) {
								e.printStackTrace();
							}
						} else {
							player.sendMessage(ChatColor.RED
									+ getMessages.getString("Home.NoHomeSet"));
						}
					} else if (args.length == 1) {
						String home = args[0];
						if (getHomes.getString(home + ".HasHome")
								.equalsIgnoreCase("no")
								|| !getHomes.contains(home + ".HasHome")) {
							player.sendMessage(ChatColor.RED
									+ getMessages.getString("Home.NoHomeSet"));
						} else if (getHomes.getString(home + ".HasHome")
								.equalsIgnoreCase("yes")) {
							player.sendMessage(ChatColor.GOLD
									+ getMessages.getString("Home.HomeRemoved"));
							getHomes.set(home + ".HasHome", "No");
							try {
								getHomes.save(file);
							} catch (IOException e) {
								e.printStackTrace();
							}
						} else {
							player.sendMessage(ChatColor.RED
									+ getMessages.getString("Home.NoHomeSet"));
						}
					} else {
						player.sendMessage(ChatColor.RED
								+ getMessages.getString("Error.Args+"));
					}
				} else {
					player.sendMessage(ChatColor.DARK_RED
							+ "You don't have permission to do that!");
				}
			} else if (commandLabel.equalsIgnoreCase("setspawn")) {
				if (player.hasPermission("homespawn.admin")) {
					File file = new File(this.getDataFolder().getAbsolutePath()
							+ File.separator + "Spawn.yml");
					FileConfiguration getSpawn = YamlConfiguration
							.loadConfiguration(file);
					File file2 = new File(this.getDataFolder()
							.getAbsolutePath()
							+ File.separator
							+ "Messages.yml");
					FileConfiguration getMessages = YamlConfiguration
							.loadConfiguration(file2);
					try {
						getSpawn.load(file);
					} catch (IOException | InvalidConfigurationException e) {
						e.printStackTrace();
					}
					getSpawn.set("spawn.SpawnSet", "Yes");
					getSpawn.set("spawn.X", player.getLocation().getBlockX());
					getSpawn.set("spawn.Y", player.getLocation().getBlockY());
					getSpawn.set("spawn.Z", player.getLocation().getBlockZ());
					getSpawn.set("spawn.World", player.getWorld().getName());
					getSpawn.set("spawn.Yaw", player.getLocation().getYaw());
					getSpawn.set("spawn.Pitch", player.getLocation().getPitch());
					player.sendMessage(ChatColor.GOLD
							+ getMessages.getString("Spawn.SpawnSet"));
					try {
						getSpawn.save(file);
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					player.sendMessage(ChatColor.DARK_RED
							+ "You don't have permission to do that!");
				}

			} else if (commandLabel.equals("spawn")) {
				if (player.hasPermission("homespawn.player")) {
					File file = new File(this.getDataFolder().getAbsolutePath()
							+ File.separator + "Spawn.yml");
					FileConfiguration getSpawn = YamlConfiguration
							.loadConfiguration(file);
					File file2 = new File(this.getDataFolder()
							.getAbsolutePath()
							+ File.separator
							+ "Messages.yml");
					FileConfiguration getMessages = YamlConfiguration
							.loadConfiguration(file2);
					try {
						getSpawn.load(file);
					} catch (IOException | InvalidConfigurationException e) {
						e.printStackTrace();
					}
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
						World world = getServer().getWorld(cworld);
						Location Spawn = new Location(world, x, y, z, yaw,
								pitch);
						Spawn.add(0.5, 0, 0.5);
						player.sendMessage(ChatColor.GOLD
								+ getMessages.getString("Spawn.SentToSpawn"));
						player.teleport(Spawn);
					} else {
						player.sendMessage(ChatColor.RED
								+ getMessages.getString("Spawn.NotSet"));
					}
				} else {
					player.sendMessage(ChatColor.DARK_RED
							+ "You don't have permission to do that!");
				}

			} else if (commandLabel.equalsIgnoreCase("delspawn")) {
				if (player.hasPermission("homespawn.admin")) {
					File file = new File(this.getDataFolder().getAbsolutePath()
							+ File.separator + "Spawn.yml");
					FileConfiguration getSpawn = YamlConfiguration
							.loadConfiguration(file);
					File file2 = new File(this.getDataFolder()
							.getAbsolutePath()
							+ File.separator
							+ "Messages.yml");
					FileConfiguration getMessages = YamlConfiguration
							.loadConfiguration(file2);
					try {
						getSpawn.load(file);
					} catch (IOException | InvalidConfigurationException e) {
						e.printStackTrace();
					}
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
							getSpawn.save(file);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}

				} else {
					player.sendMessage(ChatColor.DARK_RED
							+ "You don't have permission to do that!");
				}
			}

		} else {
			sender.sendMessage("You Must Be a Player To Do That");
		}
		return false;
	}
}
