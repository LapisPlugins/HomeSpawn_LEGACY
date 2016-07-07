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
        HashMap<String, Integer> perms = plugin.Permissions.get(plugin.PlayerPermission
                .get(player.getUniqueId()));
        if (perms.get("sSpawn") == 1) {
            if (args.length == 0) {
                plugin.spawn.set("spawn.SpawnSet", "Yes");
                plugin.spawn.set("spawn.X", player.getLocation()
                        .getBlockX());
                plugin.spawn.set("spawn.Y", player.getLocation()
                        .getBlockY());
                plugin.spawn.set("spawn.Z", player.getLocation()
                        .getBlockZ());
                plugin.spawn.set("spawn.World", player.getWorld().getName());
                plugin.spawn.set("spawn.Yaw", player.getLocation().getYaw());
                plugin.spawn.set("spawn.Pitch", player.getLocation()
                        .getPitch());
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.messages.getString("Spawn.SpawnSet")));
            } else if (args[0].equalsIgnoreCase("new")) {
                plugin.spawn.set("spawnnew.SpawnSet", "Yes");
                plugin.spawn.set("spawnnew.X", player.getLocation()
                        .getBlockX());
                plugin.spawn.set("spawnnew.Y", player.getLocation()
                        .getBlockY());
                plugin.spawn.set("spawnnew.Z", player.getLocation()
                        .getBlockZ());
                plugin.spawn.set("spawnnew.World", player.getWorld()
                        .getName());
                plugin.spawn.set("spawnnew.Yaw", player.getLocation()
                        .getYaw());
                plugin.spawn.set("spawnnew.Pitch", player.getLocation()
                        .getPitch());
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.messages.getString("Spawn.SpawnNewSet")));
            } else {
                this.plugin.help(player);
            }
            try {
                plugin.spawn.save(this.plugin.spawnFile);
                this.plugin.reload("Silent");
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.messages.getString("NoPerms")));

        }
    }

}
