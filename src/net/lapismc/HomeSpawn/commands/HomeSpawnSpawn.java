package net.lapismc.HomeSpawn.commands;

import net.lapismc.HomeSpawn.HomeSpawn;
import net.lapismc.HomeSpawn.HomeSpawnCommand;
import net.lapismc.HomeSpawn.HomeSpawnPermissions;
import net.lapismc.HomeSpawn.api.events.SpawnTeleportEvent;
import org.bukkit.Bukkit;
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
        HashMap<HomeSpawnPermissions.perm, Integer> perms = plugin.HSPermissions.getPlayerPermissions(player.getUniqueId());
        if (perms.get(HomeSpawnPermissions.perm.spawn) == 1) {
            if (!plugin.HSConfig.spawn.contains("spawn.SpawnSet")) {
                player.sendMessage(plugin.HSConfig.getColoredMessage("Spawn.NotSet"));
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
                hsc.TeleportPlayer(player, Spawn, "Spawn", null);
            } else {
                player.sendMessage(plugin.HSConfig.getColoredMessage("Spawn.NotSet"));
            }
        } else {
            player.sendMessage(plugin.HSConfig.getColoredMessage("NoPerms"));
        }
    }

}
