package net.lapismc.HomeSpawn.commands;

import net.lapismc.HomeSpawn.HomeSpawn;
import net.lapismc.HomeSpawn.HomeSpawnCommand;
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
                HomeSpawnCommand.getSpawn.set("spawn.SpawnSet", "Yes");
                HomeSpawnCommand.getSpawn.set("spawn.X", player.getLocation()
                        .getBlockX());
                HomeSpawnCommand.getSpawn.set("spawn.Y", player.getLocation()
                        .getBlockY());
                HomeSpawnCommand.getSpawn.set("spawn.Z", player.getLocation()
                        .getBlockZ());
                HomeSpawnCommand.getSpawn.set("spawn.World", player.getWorld().getName());
                HomeSpawnCommand.getSpawn.set("spawn.Yaw", player.getLocation().getYaw());
                HomeSpawnCommand.getSpawn.set("spawn.Pitch", player.getLocation()
                        .getPitch());
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        HomeSpawnCommand.getMessages.getString("Spawn.SpawnSet")));
            } else if (args[0].equalsIgnoreCase("new")) {
                HomeSpawnCommand.getSpawn.set("spawnnew.SpawnSet", "Yes");
                HomeSpawnCommand.getSpawn.set("spawnnew.X", player.getLocation()
                        .getBlockX());
                HomeSpawnCommand.getSpawn.set("spawnnew.Y", player.getLocation()
                        .getBlockY());
                HomeSpawnCommand.getSpawn.set("spawnnew.Z", player.getLocation()
                        .getBlockZ());
                HomeSpawnCommand.getSpawn.set("spawnnew.World", player.getWorld()
                        .getName());
                HomeSpawnCommand.getSpawn.set("spawnnew.Yaw", player.getLocation()
                        .getYaw());
                HomeSpawnCommand.getSpawn.set("spawnnew.Pitch", player.getLocation()
                        .getPitch());
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        HomeSpawnCommand.getMessages.getString("Spawn.SpawnNewSet")));
            } else {
                this.plugin.help(player);
            }
            try {
                HomeSpawnCommand.getSpawn.save(this.plugin.spawnFile);
                this.plugin.reload("Silent");
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    HomeSpawnCommand.getMessages.getString("NoPerms")));

        }
    }

}
