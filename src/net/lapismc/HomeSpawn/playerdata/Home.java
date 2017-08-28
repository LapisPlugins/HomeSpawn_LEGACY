package net.lapismc.HomeSpawn.playerdata;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;

import java.util.UUID;

@SuppressWarnings("deprecation")
public class Home {

    private Location location;
    private OfflinePlayer owner;
    private String name;

    public Home(String name, Location loc, OfflinePlayer owner) {
        this.name = name;
        this.location = loc;
        this.owner = owner;
    }

    public Home(String name, Location loc, UUID ownersUuid) {
        this.name = name;
        this.location = loc;
        this.owner = Bukkit.getOfflinePlayer(ownersUuid);
    }

    public void teleportPlayer(Player p) {
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
