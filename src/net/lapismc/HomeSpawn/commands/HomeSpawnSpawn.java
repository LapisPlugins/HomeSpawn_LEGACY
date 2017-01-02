package net.lapismc.HomeSpawn.commands;

import net.lapismc.HomeSpawn.HomeSpawn;
import net.lapismc.HomeSpawn.HomeSpawnCommand;
import net.lapismc.HomeSpawn.api.events.SpawnTeleportEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

import java.util.HashMap;

public class HomeSpawnSpawn {

    private HomeSpawn plugin;
    private HomeSpawnCommand hsc;

    public HomeSpawnSpawn(HomeSpawn p, HomeSpawnCommand hsc) {
        this.plugin = p;
        this.hsc = hsc;
    }

    public void spawn(String[] args, Player player) {
        Permission playerPerm = plugin.HSPermissions.PlayerPermission.get(player.getUniqueId());
        if (playerPerm == null) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.HSConfig.messages.getString("NoPerms")));
            return;
        }
        HashMap<String, Integer> perms = plugin.HSPermissions.Permissions.get(playerPerm);
        if (perms == null) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.HSConfig.messages.getString("NoPerms")));
            return;
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
                SpawnTeleportEvent STE = new SpawnTeleportEvent(plugin, player, Spawn);
                Bukkit.getPluginManager().callEvent(STE);
                if (STE.isCancelled()) {
                    player.sendMessage("Your teleport was cancelled because " + STE.getCancelReason());
                    return;
                }
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
