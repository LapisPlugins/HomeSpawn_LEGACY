package net.lapismc.HomeSpawn;

import java.io.File;
import java.io.IOException;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class HomeSpawnListener implements Listener {
	private final HomeSpawn plugin;

	public HomeSpawnListener(HomeSpawn plugin) {
		this.plugin = plugin;
	}

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
			plugin.console.sendMessage("Player " + player.getName()
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
			} catch (IOException e) {
				e.printStackTrace();
				plugin.console
						.sendMessage("[HomeSpawn] Player Name Data File Creation Failed!");
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
				getHomes.set("name", player.getName());
				getHomes.set("HasHome", "No");
				getHomes.set(player.getName() + ".Numb", 0);
				getHomes.save(file);
				plugin.spawnnew(player);
			} catch (IOException e) {
				e.printStackTrace();
				plugin.console
						.sendMessage("[HomeSpawn] Player Data File Creation Failed!");
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
				player.sendMessage(ChatColor.GOLD
						+ "[HomeSpawn] An update is available on Bukkit Dev");
			}
		}
	}
}
