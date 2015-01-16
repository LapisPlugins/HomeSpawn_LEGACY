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
		if (!(file.exists())) {
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
			File file2 = new File(plugin.getDataFolder().getAbsolutePath()
					+ File.separator + "update.yml");
			FileConfiguration getUpdate = YamlConfiguration
					.loadConfiguration(file2);
			if (getUpdate.getBoolean("Avail")) {
				player.sendMessage(ChatColor.GOLD
						+ "[HomeSpawn] An update is available on bukkit");
			}
		}
	}
}
