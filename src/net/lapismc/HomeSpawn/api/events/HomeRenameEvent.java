package net.lapismc.HomeSpawn.api.events;

import net.lapismc.HomeSpawn.playerdata.Home;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@SuppressWarnings("unused")
public class HomeRenameEvent extends Event implements Cancellable {

    public static final HandlerList handlers = new HandlerList();
    private Home oldHome;
    private Home newHome;
    private Player p;
    private String reason;
    private boolean cancelled;

    public HomeRenameEvent(Player p, Home oldHome, Home newHome) {
        this.oldHome = oldHome;
        this.newHome = newHome;
        this.p = p;
        this.cancelled = false;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Home getOldHome() {
        return oldHome;
    }

    public Home getNewHome() {
        return newHome;
    }

    public Player getPlayer() {
        return p;
    }

    public String getReason() {
        return reason;
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
        this.reason = reason;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
