package net.lapismc.HomeSpawn.commands;

import net.lapismc.HomeSpawn.HomeSpawn;
import net.lapismc.HomeSpawn.HomeSpawnCommand;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class Spawn {

    private HomeSpawn plugin;
    private HomeSpawnCommand hsc;

    public Spawn(HomeSpawn p, HomeSpawnCommand hsc) {
        this.plugin = p;
        this.hsc = hsc;
    }

    public void spawn(String[] args, Player player) {
        HashMap<String, Integer> perms = plugin.permissions.Permissions.get(plugin.permissions.PlayerPermission.
                get(player.getUniqueId()));
        if (perms.get("spawn") == 1) {
            if (!plugin.spawn.contains("spawn.SpawnSet")) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.messages.getString("Spawn.NotSet")));
                return;
            }
            if (plugin.spawn.getString("spawn.SpawnSet").equalsIgnoreCase(
                    "yes")) {
                int x = plugin.spawn.getInt("spawn.X");
                int y = plugin.spawn.getInt("spawn.Y");
                int z = plugin.spawn.getInt("spawn.Z");
                float yaw = plugin.spawn.getInt("spawn.Yaw");
                float pitch = plugin.spawn.getInt("spawn.Pitch");
                String cworld = plugin.spawn.getString("spawn.World");
                World world = this.plugin.getServer().getWorld(cworld);
                Location Spawn = new Location(world, x, y, z, yaw,
                        pitch);
                Spawn.add(0.5, 0, 0.5);
                hsc.TeleportPlayer(player, Spawn, "Spawn", null);
            } else {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.messages.getString("Spawn.NotSet")));
            }
        } else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.messages.getString("NoPerms")));
        }
    }

}
