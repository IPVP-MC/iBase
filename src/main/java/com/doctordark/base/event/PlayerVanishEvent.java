package com.doctordark.base.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import java.util.Collection;

/**
 * Event called when a {@link Player} is about to be frozen of movement and commands.
 */
public class PlayerVanishEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final boolean vanished;
    private final Collection<Player> viewers;
    private boolean cancelled;

    public PlayerVanishEvent(Player player, Collection<Player> viewers, boolean vanished) {
        super(player);
        this.viewers = viewers;
        this.vanished = vanished;
    }

    public Collection<Player> getViewers() {
        return viewers;
    }

    /**
     * Checks if this {@link Player} will be vanished during
     * this event.
     *
     * @return true if will be vanished
     */
    public boolean isVanished() {
        return vanished;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
