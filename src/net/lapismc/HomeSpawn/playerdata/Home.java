package net.lapismc.HomeSpawn.playerdata;

import net.lapismc.HomeSpawn.HomeSpawn;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

public class Home {

    private Location location;
    private OfflinePlayer owner;
    private String name;

    public Home(String name, Location loc, OfflinePlayer owner) {
        this.name = name;
        location = loc;
        this.owner = owner;
    }

    public Home(HomeSpawn p, String name, Location loc, UUID ownersUuid) {
        this.name = name;
        location = loc;
        owner = Bukkit.getOfflinePlayer(ownersUuid);
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
