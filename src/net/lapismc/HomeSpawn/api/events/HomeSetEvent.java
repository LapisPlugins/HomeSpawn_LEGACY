package net.lapismc.HomeSpawn.api.events;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class HomeSetEvent extends Event implements Cancellable {

    private final HandlerList handlers = new HandlerList();
    private String homeName;
    private World world;
    private Player p;
    private boolean cancelled;

    public HomeSetEvent(Player p, World w, String name) {
        homeName = name;
        this.world = w;
        this.p = p;
    }

    public String getHomeName() {
        return homeName;
    }

    public World getWorld() {
        return world;
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
