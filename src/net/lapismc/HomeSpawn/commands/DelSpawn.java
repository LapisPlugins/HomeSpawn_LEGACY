package net.lapismc.HomeSpawn.commands;

import net.lapismc.HomeSpawn.HomeSpawn;
import net.lapismc.HomeSpawn.HomeSpawnCommand;
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
        HashMap<String, Integer> perms = plugin.Permissions.get(plugin.PlayerPermission
                .get(player.getUniqueId()));
        if (perms.get("sSpawn") == 1) {
            if (Objects.equals(HomeSpawnCommand.getSpawn.getString("spawn.SpawnSet"), "No")
                    || !HomeSpawnCommand.getSpawn.contains("spawn.SpawnSet")) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        HomeSpawnCommand.getMessages.getString("Spawn.NotSet")));
            } else if (HomeSpawnCommand.getSpawn.getString("spawn.SpawnSet")
                    .equalsIgnoreCase("Yes")) {
                HomeSpawnCommand.getSpawn.set("spawn.SpawnSet", "No");
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        HomeSpawnCommand.getMessages.getString("Spawn.Removed")));
                try {
                    HomeSpawnCommand.getSpawn.save(this.plugin.spawnFile);
                    this.plugin.reload("silent");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    HomeSpawnCommand.getMessages.getString("NoPerms")));
        }
    }

}
