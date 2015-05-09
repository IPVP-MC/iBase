package com.doctordark.base.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PlayerFreezeEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final boolean frozen;
    private boolean cancelled;

    public PlayerFreezeEvent(Player player, boolean frozen) {
        super(player);
        this.frozen = frozen;
    }

    /**
     * Checks if the user will be frozen during
     * this event.
     *
     * @return true if will be frozen
     */
    public boolean isFrozen() {
        return frozen;
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
