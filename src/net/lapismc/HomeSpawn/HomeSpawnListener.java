package net.lapismc.HomeSpawn;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class HomeSpawnListener implements Listener {
	private final HomeSpawn plugin;

	public HomeSpawnListener(HomeSpawn plugin) {
		this.plugin = plugin;
	}

	List<Player> Players = new ArrayList<Player>();

	@EventHandler(priority = EventPriority.HIGH)
	void PlayerJoinEvent(final PlayerJoinEvent event) {
		Player player = event.getPlayer();
		File file = new File(plugin.getDataFolder() + File.separator
				+ "PlayerData" + File.separator
				+ player.getUniqueId().toString() + ".yml");
		FileConfiguration getHomes = YamlConfiguration.loadConfiguration(file);
		File file2 = new File(plugin.getDataFolder().getAbsolutePath()
				+ File.separator + "PlayerData" + File.separator
				+ "PlayerNames" + File.separator + player.getName() + ".yml");
		FileConfiguration getName = YamlConfiguration.loadConfiguration(file2);
		if (file == null || file2 == null) {
			plugin.logger.severe("Player " + player.getName()
					+ "'s Data File Is Null!");
			return;
		}
		if (!file2.exists()) {
			try {
				file2.createNewFile();
				getName.createSection("Name");
				getName.createSection("UUID");
				getName.save(file2);
				getName.set("Name", player.getName());
				getName.set("UUID", player.getUniqueId().toString());
				getName.save(file2);
				if (plugin.getConfig().getBoolean("CommandBook")) {
					PlayerInventory pi = player.getInventory();
					ItemStack commandBook = InstructionBook.getBook();
					pi.addItem(commandBook);
				}
			} catch (IOException e) {
				e.printStackTrace();
				plugin.logger
						.severe("[HomeSpawn] Player Name Data File Creation Failed!");
				return;
			}
		}
		if (!file.exists()) {
			try {
				file.createNewFile();
				getHomes.createSection("name");
				getHomes.createSection("HasHome");
				getHomes.createSection(player.getName() + ".Numb");
				getHomes.save(file);
				getHomes.set("name", player.getUniqueId().toString());
				getHomes.set("HasHome", "No");
				getHomes.set(player.getUniqueId() + ".Numb", 0);
				getHomes.save(file);
				plugin.spawnnew(player);
			} catch (IOException e) {
				e.printStackTrace();
				plugin.logger
						.severe("[HomeSpawn] Player Data File Creation Failed!");
				return;
			}
		}
		if (player.hasPermission("homespawn.admin")) {
			File file1 = new File(plugin.getDataFolder().getAbsolutePath()
					+ File.separator + "Update.yml");
			FileConfiguration getUpdate = YamlConfiguration
					.loadConfiguration(file1);
			if (file1 != null || getUpdate.contains("Avail")
					&& getUpdate.getString("Avail").equalsIgnoreCase("true")) {
				if (!plugin.getConfig().getBoolean("AutoUpdate")
						&& plugin.getConfig().getBoolean("UpdateNotification")) {
					player.sendMessage(ChatColor.GOLD
							+ "[HomeSpawn] An update is available on Bukkit Dev");
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void OnPlayerMove(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		Location From = e.getFrom();
		Location To = e.getTo();
		List<Integer> To1 = new ArrayList<Integer>();
		List<Integer> From1 = new ArrayList<Integer>();
		if (plugin.HomeSpawnLocations.containsKey(p)) {
			if (plugin.HomeSpawnTimeLeft.containsKey(p)) {
				To1.add(To.getBlockX());
				To1.add(To.getBlockY());
				To1.add(To.getBlockZ());
				From1.add(From.getBlockX());
				From1.add(From.getBlockY());
				From1.add(From.getBlockZ());
				if (From1.equals(To1)) {
					return;
				} else {
					if (!Players.contains(p)) {
						plugin.HomeSpawnLocations.put(p, null);
						plugin.HomeSpawnTimeLeft.remove(p);
						p.sendMessage(ChatColor.GOLD
								+ "Teleport Canceled Because You Moved!");
					} else {
						e.setCancelled(true);
						plugin.HomeSpawnTimeLeft.put(p, 1);
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void OnPlayerDamage(EntityDamageByEntityEvent e) {
		Entity Hitter = e.getDamager();
		Entity Hit = e.getEntity();
		if (Hit instanceof Player) {
			Player p = ((Player) Hit);
			if (plugin.HomeSpawnTimeLeft.containsKey(p)) {
				if (Hitter instanceof Arrow) {
					final Arrow arrow = (Arrow) Hitter;
					if (arrow.getShooter() instanceof Player) {
						plugin.HomeSpawnLocations.put(p, null);
						p.sendMessage(ChatColor.GOLD
								+ "Teleport Canceled Because You Were Hit!");
					} else if (arrow.getShooter() instanceof Skeleton) {
						Players.add(p);
						e.setCancelled(true);
					}
				}
				if (Hitter instanceof Player || Hitter instanceof Wolf) {
					plugin.HomeSpawnLocations.put(p, null);
					p.sendMessage(ChatColor.GOLD
							+ "Teleport Canceled Because You Were Hit!");
				} else {
					Players.add(p);
					e.setCancelled(true);
				}
				if (Hitter instanceof Player || Hitter instanceof Wolf) {
					plugin.HomeSpawnLocations.put(p, null);
					plugin.HomeSpawnTimeLeft.remove(p);
					p.sendMessage(ChatColor.GOLD
							+ "Teleport Canceled Because You Were Hit!");
				} else {
					e.setCancelled(false);
				}
			}
		}
	}
}
