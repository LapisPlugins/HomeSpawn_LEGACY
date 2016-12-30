package net.lapismc.HomeSpawn.api.events;

import net.lapismc.HomeSpawn.HomeSpawn;
import net.lapismc.HomeSpawn.HomeSpawnConfiguration;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SpawnTeleportEvent extends Event implements Cancellable {

    private final HandlerList handlers = new HandlerList();
    private String cancelReason;
    private Location location;
    private Player p;
    private boolean cancelled;

    public SpawnTeleportEvent(HomeSpawn plugin, Player p, Location l) {
        this.location = l;
        this.p = p;
        this.cancelled = false;
        if (plugin.HSComponents.logging()) {
            plugin.HSConfig.log(HomeSpawnConfiguration.logType.TeleportSpawn, p, "Spawn");
        }
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
