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
        HashMap<String, Integer> perms = plugin.Permissions.get(plugin.PlayerPermission.
                get(player.getUniqueId()));
        if (perms.get("spawn") == 1) {
            if (!HomeSpawnCommand.getSpawn.contains("spawn.SpawnSet")) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        HomeSpawnCommand.getMessages.getString("Spawn.NotSet")));
                return;
            }
            if (HomeSpawnCommand.getSpawn.getString("spawn.SpawnSet").equalsIgnoreCase(
                    "yes")) {
                int x = HomeSpawnCommand.getSpawn.getInt("spawn.X");
                int y = HomeSpawnCommand.getSpawn.getInt("spawn.Y");
                int z = HomeSpawnCommand.getSpawn.getInt("spawn.Z");
                float yaw = HomeSpawnCommand.getSpawn.getInt("spawn.Yaw");
                float pitch = HomeSpawnCommand.getSpawn.getInt("spawn.Pitch");
                String cworld = HomeSpawnCommand.getSpawn.getString("spawn.World");
                World world = this.plugin.getServer().getWorld(cworld);
                Location Spawn = new Location(world, x, y, z, yaw,
                        pitch);
                Spawn.add(0.5, 0, 0.5);
                hsc.TeleportPlayer(player, Spawn, "Spawn");
            } else {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        HomeSpawnCommand.getMessages.getString("Spawn.NotSet")));
            }
        } else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    HomeSpawnCommand.getMessages.getString("NoPerms")));
        }
    }

}
