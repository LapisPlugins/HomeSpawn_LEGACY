package net.lapismc.HomeSpawn.commands;

import net.lapismc.HomeSpawn.HomeSpawn;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.HashMap;

public class SetSpawn {

    private HomeSpawn plugin;

    public SetSpawn(HomeSpawn p) {
        this.plugin = p;
    }

    public void setSpawn(String[] args, Player player) {
        HashMap<String, Integer> perms = plugin.HSPermissions.Permissions.get(plugin.HSPermissions.PlayerPermission
                .get(player.getUniqueId()));
        if (perms == null) {
            plugin.HSPermissions.init();
            perms = plugin.HSPermissions.Permissions.get(plugin.HSPermissions.PlayerPermission.
                    get(player.getUniqueId()));
        }
        if (perms.get("sSpawn") == 1) {
            if (args.length == 0) {
                plugin.HSConfig.spawn.set("spawn.SpawnSet", "Yes");
                plugin.HSConfig.spawn.set("spawn.X", player.getLocation()
                        .getBlockX());
                plugin.HSConfig.spawn.set("spawn.Y", player.getLocation()
                        .getBlockY());
                plugin.HSConfig.spawn.set("spawn.Z", player.getLocation()
                        .getBlockZ());
                plugin.HSConfig.spawn.set("spawn.World", player.getWorld().getName());
                plugin.HSConfig.spawn.set("spawn.Yaw", player.getLocation().getYaw());
                plugin.HSConfig.spawn.set("spawn.Pitch", player.getLocation()
                        .getPitch());
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.HSConfig.messages.getString("Spawn.SpawnSet")));
            } else if (args[0].equalsIgnoreCase("new")) {
                plugin.HSConfig.spawn.set("spawnnew.SpawnSet", "Yes");
                plugin.HSConfig.spawn.set("spawnnew.X", player.getLocation()
                        .getBlockX());
                plugin.HSConfig.spawn.set("spawnnew.Y", player.getLocation()
                        .getBlockY());
                plugin.HSConfig.spawn.set("spawnnew.Z", player.getLocation()
                        .getBlockZ());
                plugin.HSConfig.spawn.set("spawnnew.World", player.getWorld()
                        .getName());
                plugin.HSConfig.spawn.set("spawnnew.Yaw", player.getLocation()
                        .getYaw());
                plugin.HSConfig.spawn.set("spawnnew.Pitch", player.getLocation()
                        .getPitch());
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.HSConfig.messages.getString("Spawn.SpawnNewSet")));
            } else {
                this.plugin.help(player);
            }
            try {
                plugin.HSConfig.spawn.save(this.plugin.HSConfig.spawnFile);
                this.plugin.HSConfig.reload("Silent");
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.HSConfig.messages.getString("NoPerms")));

        }
    }

}
