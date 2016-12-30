package net.lapismc.HomeSpawn.commands;

import net.lapismc.HomeSpawn.HomeSpawn;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

public class HomeSpawnDelSpawn {

    private HomeSpawn plugin;

    public HomeSpawnDelSpawn(HomeSpawn p) {
        this.plugin = p;
    }

    public void delSpawn(String[] args, Player player) {
        HashMap<String, Integer> perms = plugin.HSPermissions.Permissions.get(plugin.HSPermissions.PlayerPermission
                .get(player.getUniqueId()));
        if (perms == null) {
            plugin.HSPermissions.init();
            perms = plugin.HSPermissions.Permissions.get(plugin.HSPermissions.PlayerPermission.
                    get(player.getUniqueId()));
        }
        if (perms.get("sSpawn") == 1) {
            if (Objects.equals(plugin.HSConfig.spawn.getString("spawn.SpawnSet"), "No")
                    || !plugin.HSConfig.spawn.contains("spawn.SpawnSet")) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.HSConfig.messages.getString("HomeSpawnSpawn.NotSet")));
            } else if (plugin.HSConfig.spawn.getString("spawn.SpawnSet")
                    .equalsIgnoreCase("Yes")) {
                plugin.HSConfig.spawn.set("spawn.SpawnSet", "No");
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.HSConfig.messages.getString("HomeSpawnSpawn.Removed")));
                try {
                    plugin.HSConfig.spawn.save(this.plugin.HSConfig.spawnFile);
                    this.plugin.HSConfig.reload("silent");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.HSConfig.messages.getString("NoPerms")));
        }
    }

}
