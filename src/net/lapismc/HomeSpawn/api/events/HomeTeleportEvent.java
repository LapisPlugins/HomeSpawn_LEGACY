package net.lapismc.HomeSpawn.api.events;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class HomeTeleportEvent extends Event implements Cancellable {

    private final HandlerList handlers = new HandlerList();
    private String homeName;
    private String cancelReason;
    private Location location;
    private Player p;
    private boolean cancelled;

    public HomeTeleportEvent(Player p, Location l, String name) {
        homeName = name;
        this.location = l;
        this.p = p;
        this.cancelled = false;
    }

    public String getHomeName() {
        return homeName;
    }

    public Location getLocation() {
        return location;
    }

    public Player getPlayer() {
        return p;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    @Deprecated
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }

    public void setCancelled(boolean cancel, String reason) {
        cancelled = cancel;
        cancelReason = reason;
    }

    public String getCancelReason() {
        if (cancelled) {
            return cancelReason;
        } else {
            return null;
        }
    }

    public HandlerList getHandlers() {
        return handlers;
    }
}
