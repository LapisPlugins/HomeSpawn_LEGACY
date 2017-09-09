package net.lapismc.HomeSpawn.playerdata;

import net.lapismc.HomeSpawn.HomeSpawn;
import net.lapismc.HomeSpawn.HomeSpawnPermissions;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

@SuppressWarnings("deprecation")
public class Home {

    private final HomeSpawn plugin;
    private Location location;
    private final OfflinePlayer owner;
    private String name;

    public Home(HomeSpawn plugin, String name, Location loc, OfflinePlayer owner) {
        this.plugin = plugin;
        this.name = name;
        this.location = loc;
        this.owner = owner;
    }

    public Home(HomeSpawn plugin, String name, Location loc, UUID ownersUuid) {
        this.plugin = plugin;
        this.name = name;
        this.location = loc;
        this.owner = Bukkit.getOfflinePlayer(ownersUuid);
    }

    public void teleportPlayer(Player p) {
        HashMap<HomeSpawnPermissions.perm, Integer> perms = plugin.HSPermissions.getPlayerPermissions(p.getUniqueId());
        if (perms.get(HomeSpawnPermissions.perm.TeleportDelay) == 0) {
            if (!location.getChunk().isLoaded()) {
                location.getChunk().load();
            }
            if (p.isInsideVehicle()) {
                if (p.getVehicle() instanceof Horse) {
                    Horse horse = (Horse) p.getVehicle();
                    horse.eject();
                    horse.teleport(location);
                    p.teleport(location);
                    horse.setPassenger(p);
                }
            } else {
                p.teleport(location);
            }
            p.sendMessage(plugin.HSConfig.getColoredMessage("Home.SentHome"));
        } else {
            String waitraw = plugin.HSConfig.getColoredMessage("Wait");
            String Wait = waitraw.replace("{time}", perms.get(HomeSpawnPermissions.perm.TeleportDelay).toString());
            p.sendMessage(Wait);
            plugin.HomeSpawnLocations.put(p, location);
            plugin.HomeSpawnTimeLeft.put(p, perms.get(HomeSpawnPermissions.perm.TeleportDelay));
        }
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location loc) {
        location = loc;
    }

    public OfflinePlayer getOwner() {
        return owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String newName) {
        name = newName;
    }

    @Override
    public String toString() {
        return name;
    }
}
