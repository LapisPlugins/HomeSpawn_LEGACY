package net.lapismc.HomeSpawn.commands;

import net.lapismc.HomeSpawn.HomeSpawn;
import net.lapismc.HomeSpawn.HomeSpawnCommand;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class HomeSpawnSpawn {

    private HomeSpawn plugin;
    private HomeSpawnCommand hsc;

    public HomeSpawnSpawn(HomeSpawn p, HomeSpawnCommand hsc) {
        this.plugin = p;
        this.hsc = hsc;
    }

    public void spawn(String[] args, Player player) {
        HashMap<String, Integer> perms = plugin.HSPermissions.Permissions.get(plugin.HSPermissions.PlayerPermission.
                get(player.getUniqueId()));
        if (perms == null) {
            plugin.HSPermissions.init();
            perms = plugin.HSPermissions.Permissions.get(plugin.HSPermissions.PlayerPermission.
                    get(player.getUniqueId()));
        }
        if (perms.get("spawn") == 1) {
            if (!plugin.HSConfig.spawn.contains("spawn.SpawnSet")) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.HSConfig.messages.getString("HomeSpawnSpawn.NotSet")));
                return;
            }
            if (plugin.HSConfig.spawn.getString("spawn.SpawnSet").equalsIgnoreCase(
                    "yes")) {
                int x = plugin.HSConfig.spawn.getInt("spawn.X");
                int y = plugin.HSConfig.spawn.getInt("spawn.Y");
                int z = plugin.HSConfig.spawn.getInt("spawn.Z");
                float yaw = plugin.HSConfig.spawn.getInt("spawn.Yaw");
                float pitch = plugin.HSConfig.spawn.getInt("spawn.Pitch");
                String cworld = plugin.HSConfig.spawn.getString("spawn.World");
                World world = this.plugin.getServer().getWorld(cworld);
                Location Spawn = new Location(world, x, y, z, yaw,
                        pitch);
                Spawn.add(0.5, 0, 0.5);
                hsc.TeleportPlayer(player, Spawn, "HomeSpawnSpawn", null);
            } else {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.HSConfig.messages.getString("HomeSpawnSpawn.NotSet")));
            }
        } else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.HSConfig.messages.getString("NoPerms")));
        }
    }

}
