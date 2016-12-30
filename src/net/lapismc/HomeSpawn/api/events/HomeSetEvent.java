package net.lapismc.HomeSpawn.api.events;

import net.lapismc.HomeSpawn.HomeSpawn;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class HomeSetEvent extends Event implements Cancellable {

    private final HandlerList handlers = new HandlerList();
    private String homeName;
    private Location loc;
    private Player p;
    private boolean cancelled;

    public HomeSetEvent(HomeSpawn plugin, Player p, Location loc, String name) {
        homeName = name;
        this.loc = loc;
        this.p = p;
        this.cancelled = false;
        if (plugin.HSComponents.logging()) {
            //TODO: setup logging methods
        }
    }

    public String getHomeName() {
        return homeName;
    }

    public Location getLoc() {
        return loc;
    }

    public Player getPlayer() {
        return p;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }

    public HandlerList getHandlers() {
        return handlers;
    }
}
