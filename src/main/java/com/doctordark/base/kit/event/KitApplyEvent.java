package com.doctordark.base.kit.event;

import com.doctordark.base.kit.Kit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * Event called when an {@link Kit} has been applied to a {@link Player}.
 */
public class KitApplyEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled = false;

    private final Kit kit;
    private final boolean force;

    public KitApplyEvent(Kit kit, Player player, boolean force) {
        super(player);
        this.kit = kit;
        this.force = force;
    }

    /**
     * Gets the kit being applied.
     *
     * @return the applied kit
     */
    public Kit getKit() {
        return kit;
    }

    /**
     * Gets if this kit is being forced upon a player.
     * <p>This will bypass even if this event is cancelled.</p>
     *
     * @return if this kit is being forced
     */
    public boolean isForce() {
        return force;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}