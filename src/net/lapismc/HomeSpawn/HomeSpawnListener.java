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
		File file = new File(plugin.getDataFolder().getAbsolutePath()
				+ File.separator + "PlayerData" + File.separator
				+ player.getUniqueId() + ".yml");
		FileConfiguration getHomes = YamlConfiguration.loadConfiguration(file);
		File file2 = new File(plugin.getDataFolder().getAbsolutePath()
				+ File.separator + "PlayerData" + File.separator
				+ "PlayerNames" + File.separator + player.getName() + ".yml");
		FileConfiguration getName = YamlConfiguration.loadConfiguration(file2);
		if (!file2.exists()) {
			try {
				file.createNewFile();
			getName.createSection("Name");
			getName.createSection("UUID");
			getName.save(file2);
			getName.set("Name", player.getName());
			getName.set("UUID", player.getUniqueId());
			getName.save(file2);
			} catch (IOException e) {
				e.printStackTrace();
				plugin.console
				.sendMessage("[HomeSpawn] Player Name Data File Creation Failed!");
			}
		}
		if (!file.exists()) {
			try {
				file.createNewFile();
				getHomes.createSection("HasHome");
				getHomes.save(file);
				getHomes.set("HasHome", "No");
				getHomes.save(file);
				plugin.spawnnew(player);
			} catch (IOException e) {
				e.printStackTrace();
				plugin.console
						.sendMessage("[HomeSpawn] Player Data File Creation Failed!");
				return;
			}

			getHomes.createSection("name");
			getHomes.set("name", player.getName());
			plugin.console.sendMessage(ChatColor.GOLD
					+ "[HomeSpawn] Blank Config Has Been Created For "
					+ player.getName());
			try {
				getHomes.save(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (player.hasPermission("homespawn.admin")) {
			File file1 = new File(plugin.getDataFolder().getAbsolutePath()
					+ File.separator + "Update.yml");
			FileConfiguration getUpdate = YamlConfiguration
					.loadConfiguration(file1);
			if (getUpdate.getString("Avail").equalsIgnoreCase("true")) {
				player.sendMessage(ChatColor.GOLD
						+ "[HomeSpawn] An update is available on bukkit");
			}
		}
	}
}
