package net.lapismc.HomeSpawn.commands;

import net.lapismc.HomeSpawn.HomeSpawn;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

public class DelSpawn {

    private HomeSpawn plugin;

    public DelSpawn(HomeSpawn p) {
        this.plugin = p;
    }

    public void delSpawn(String[] args, Player player) {
        HashMap<String, Integer> perms = plugin.permissions.Permissions.get(plugin.permissions.PlayerPermission
                .get(player.getUniqueId()));
        if (perms.get("sSpawn") == 1) {
            if (Objects.equals(plugin.spawn.getString("spawn.SpawnSet"), "No")
                    || !plugin.spawn.contains("spawn.SpawnSet")) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.messages.getString("Spawn.NotSet")));
            } else if (plugin.spawn.getString("spawn.SpawnSet")
                    .equalsIgnoreCase("Yes")) {
                plugin.spawn.set("spawn.SpawnSet", "No");
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.messages.getString("Spawn.Removed")));
                try {
                    plugin.spawn.save(this.plugin.spawnFile);
                    this.plugin.reload("silent");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.messages.getString("NoPerms")));
        }
    }

}
