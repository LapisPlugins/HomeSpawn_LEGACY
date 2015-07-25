package net.lapismc.HomeSpawn;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import net.lapismc.HomeSpawn.PasswordHash;

public class HomeSpawnCommand implements CommandExecutor {
	private final HomeSpawn plugin;

	public HomeSpawnCommand(HomeSpawn plugin) {
		this.plugin = plugin;
	}

	@SuppressWarnings("unused")
	private FileConfiguration GetHome(String player) {
		File file2 = new File(plugin.getDataFolder().getAbsolutePath()
				+ File.separator + "PlayerData" + File.separator
				+ "PlayerNames" + File.separator + player + ".yml");
		FileConfiguration getName = YamlConfiguration.loadConfiguration(file2);
		File Homes = new File(plugin.getDataFolder().getAbsolutePath()
				+ File.separator + "PlayerData" + File.separator
				+ getName.getString("UUID") + ".yml");
		FileConfiguration Gethome = YamlConfiguration.loadConfiguration(Homes);
		return Gethome;
	}

	public void TeleportPlayer(Player p, Location l, String r) {
		File file2 = new File(plugin.getDataFolder().getAbsolutePath()
				+ File.separator + "Messages.yml");
		FileConfiguration getMessages = YamlConfiguration
				.loadConfiguration(file2);
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
			plugin.HomeSpawnTimeLeft.put(p, plugin.getConfig().getInt("TeleportTime"));
		}

	}

	public boolean onCommand(CommandSender sender, Command cmd,
			String commandLabel, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (cmd.getName().equalsIgnoreCase("sethome")) {
				if (player.hasPermission("homespawn.player")) {
					File file = new File(plugin.getDataFolder()
							+ File.separator + "PlayerData" + File.separator
							+ player.getUniqueId().toString() + ".yml");
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
					if (!getHomes.contains(player.getName() + ".list")) {
						getHomes.createSection(player.getName() + ".list");
						try {
							getHomes.save(file);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					List<String> list = getHomes.getStringList(player.getName()
							+ ".list");
					if (getHomes.getString("name").equalsIgnoreCase(
							player.getName())) {
						if (player.hasPermission("homespawn.vip")
								&& !player.hasPermission("homespawn.admin")) {
							if (getHomes.getInt(player.getName() + ".Numb") >= plugin
									.getConfig().getInt("VIPHomesLimit")) {
								player.sendMessage(ChatColor.RED
										+ getMessages
												.getString("Home.LimitReached"));
								return false;
							}
						} else if (player.hasPermission("homespawn.admin")) {
							if (getHomes.getInt(player.getName() + ".Numb") >= plugin
									.getConfig().getInt("AdminHomesLimit")) {
								player.sendMessage(ChatColor.RED
										+ getMessages
												.getString("Home.LimitReached"));
								return false;
							}
						}
						if (args.length == 0) {

							getHomes.createSection(player.getDisplayName());
							if (!getHomes.contains(player.getName() + ".Numb")) {
								getHomes.createSection(player.getName()
										+ ".Numb");
								getHomes.set(player.getName() + ".Numb", "0");
							}
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
							if (!getHomes.contains("HasHome")) {
								getHomes.createSection("HasHome");
							}
							int HomesNumb = getHomes.getInt(player
									.getDisplayName() + ".Numb");
							if (!getHomes.contains(player.getName()
									+ ".HasHome")
									|| !getHomes.getString(
											player.getName() + ".HasHome")
											.equals("Yes")) {
								getHomes.set(player.getDisplayName() + ".Numb",
										HomesNumb + 1);
							}
							if (!list.contains("Home")) {
								list.add("Home");
								getHomes.set(player.getName() + ".list", list);
							}
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
							if (player.hasPermission("homespawn.vip")
									|| player.hasPermission("homespawn.admin")) {
								String home = args[0];
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
								if (!getHomes.contains(player.getName()
										+ ".Numb")) {
									getHomes.createSection(player.getName()
											+ ".Numb");
									getHomes.set(player.getName() + ".Numb",
											"0");
								}
								int HomesNumb = getHomes.getInt(player
										.getDisplayName() + ".Numb");
								if (!getHomes.contains(home + ".HasHome")
										|| !getHomes.get(home + ".HasHome")
												.equals("Yes")) {
									getHomes.set(player.getDisplayName()
											+ ".Numb", HomesNumb + 1);
								}
								if (!list.contains(home)) {
									list.add(home);
									getHomes.set(player.getName() + ".list",
											list);
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
						player.sendMessage(ChatColor.RED
								+ "For some reason your name doesnt match"
								+ " the name in your file, please contact an admin!");
					}
				} else {
					player.sendMessage(ChatColor.DARK_RED
							+ "You don't have permission to do that!");
				}

			} else if (cmd.getName().equalsIgnoreCase("home")) {
				if (player.hasPermission("homespawn.player")) {
					File file = new File(plugin.getDataFolder()
							+ File.separator + "PlayerData" + File.separator
							+ player.getUniqueId().toString() + ".yml");
					FileConfiguration getHomes = YamlConfiguration
							.loadConfiguration(file);
					File file2 = new File(plugin.getDataFolder()
							.getAbsolutePath()
							+ File.separator
							+ "Messages.yml");
					FileConfiguration getMessages = YamlConfiguration
							.loadConfiguration(file2);
					if (!getHomes.contains(player.getName() + ".list")) {
						getHomes.createSection(player.getName() + ".list");
						try {
							getHomes.save(file);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					List<String> list = getHomes.getStringList(player.getName()
							+ ".list");
					try {
						getHomes.load(file);
					} catch (IOException | InvalidConfigurationException e) {
						e.printStackTrace();
					}
					if (args.length == 0) {
						if (file != null
								&& getHomes.getString("HasHome")
										.equalsIgnoreCase("yes")) {
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
								if (getHomes.getInt(player.getName() + ".Numb") > 0) {
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
							if (getHomes.getInt(player.getName() + ".Numb") > 0) {
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
							+ "You don't have permission to do that!");
				}

			} else if (cmd.getName().equalsIgnoreCase("delhome")) {
				if (player.hasPermission("homespawn.player")) {
					File file = new File(plugin.getDataFolder()
							+ File.separator + "PlayerData" + File.separator
							+ player.getUniqueId().toString() + ".yml");
					FileConfiguration getHomes = YamlConfiguration
							.loadConfiguration(file);
					File file2 = new File(plugin.getDataFolder()
							.getAbsolutePath()
							+ File.separator
							+ "Messages.yml");
					FileConfiguration getMessages = YamlConfiguration
							.loadConfiguration(file2);
					if (!getHomes.contains(player.getName() + ".list")) {
						getHomes.createSection(player.getName() + ".list");
						try {
							getHomes.save(file);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					List<String> list = getHomes.getStringList(player.getName()
							+ ".list");
					try {
						getHomes.load(file);
					} catch (IOException | InvalidConfigurationException e) {
						e.printStackTrace();
						return true;
					}
					if (args.length == 0) {
						int HomeNumb = getHomes.getInt(player.getName()
								+ ".Numb");
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
							getHomes.set(player.getName() + ".Numb",
									HomeNumb - 1);
							if (list.contains("Home")) {
								list.remove("Home");
								getHomes.set(player.getName() + ".list", list);
							}
							try {
								getHomes.save(file);
							} catch (IOException e) {
								e.printStackTrace();
							}
						} else {
							player.sendMessage(ChatColor.RED
									+ getMessages.getString("Home.NoHomeSet"));
							if (getHomes.getInt(player.getName() + ".Numb") > 0) {
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
						int HomeNumb = getHomes.getInt(player.getName()
								+ ".Numb");
						if (!getHomes.contains(home + ".HasHome")
								|| getHomes.getString(home + ".HasHome")
										.equalsIgnoreCase("no")) {
							player.sendMessage(ChatColor.RED
									+ getMessages.getString("Home.NoHomeSet"));
							if (getHomes.getInt(player.getName() + ".Numb") > 0) {
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
							getHomes.set(player.getName() + ".Numb",
									HomeNumb - 1);
							if (list.contains(home)) {
								list.remove(home);
								getHomes.set(player.getName() + ".list", list);
							}
							try {
								getHomes.save(file);
							} catch (IOException e) {
								e.printStackTrace();
							}
						} else {
							player.sendMessage(ChatColor.RED
									+ getMessages.getString("Home.NoHomeSet"));
							if (getHomes.getInt(player.getName() + ".Numb") > 0) {
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
							+ "You don't have permission to do that!");
				}
			} else if (cmd.getName().equalsIgnoreCase("setspawn")) {
				if (player.hasPermission("homespawn.admin")) {
					File file = new File(plugin.getDataFolder()
							.getAbsolutePath() + File.separator + "Spawn.yml");
					FileConfiguration getSpawn = YamlConfiguration
							.loadConfiguration(file);
					File file2 = new File(plugin.getDataFolder()
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
						getSpawn.save(file);
					} catch (IOException e) {
						e.printStackTrace();
					}

				} else {
					player.sendMessage(ChatColor.DARK_RED
							+ "You don't have permission to do that!");

				}
			} else if (cmd.getName().equals("spawn")) {
				if (player.hasPermission("homespawn.player")) {
					File file = new File(plugin.getDataFolder()
							.getAbsolutePath() + File.separator + "Spawn.yml");
					FileConfiguration getSpawn = YamlConfiguration
							.loadConfiguration(file);
					File file2 = new File(plugin.getDataFolder()
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
							+ "You don't have permission to do that!");
				}

			} else if (cmd.getName().equalsIgnoreCase("delspawn")) {
				if (player.hasPermission("homespawn.admin")) {
					File file = new File(plugin.getDataFolder()
							.getAbsolutePath() + File.separator + "Spawn.yml");
					FileConfiguration getSpawn = YamlConfiguration
							.loadConfiguration(file);
					File file2 = new File(plugin.getDataFolder()
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
			} else if (cmd.getName().equalsIgnoreCase("homeslist")) {
				File file = new File(plugin.getDataFolder() + File.separator
						+ "PlayerData" + File.separator
						+ player.getUniqueId().toString() + ".yml");
				FileConfiguration getHomes = YamlConfiguration
						.loadConfiguration(file);
				File file2 = new File(plugin.getDataFolder().getAbsolutePath()
						+ File.separator + "Messages.yml");
				FileConfiguration getMessages = YamlConfiguration
						.loadConfiguration(file2);
				try {
					getHomes.load(file);
				} catch (IOException | InvalidConfigurationException e) {
					e.printStackTrace();
				}
				if (!getHomes.contains(player.getName() + ".list")) {
					getHomes.createSection(player.getName() + ".list");
				}
				List<String> list = getHomes.getStringList(player.getName()
						+ ".list");
				if (getHomes.getInt(player.getName() + ".Numb") > 0) {
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
					String string = args[0];
					if (args.length == 3) {
						if (string.equalsIgnoreCase("set")) {
							if (args[1] == args[2]) {
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
								player.sendMessage("Password Set To:");
								player.sendMessage(pass);
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
					} else if (args.length == 1) {
						PassHelp(player);
					} else if (args.length == 3) {
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
								e2.printStackTrace();
							}
							if (namefile.exists() && Password) {
								String uuid = getOldName.getString("UUID");
								File OldUUIDFile = new File(
										plugin.getDataFolder() + File.separator
												+ "PlayerData" + File.separator
												+ uuid + ".yml");
								file.delete();
								OldUUIDFile.renameTo(file);
								getHomes.set("Name", player.getName());
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
							+ "You don't have permission to do that!");
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
