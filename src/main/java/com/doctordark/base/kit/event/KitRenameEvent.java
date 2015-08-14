package com.doctordark.base.kit.event;

import com.doctordark.base.kit.Kit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Event called when a {@link Kit} has been renamed.
 */
public class KitRenameEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled = false;

    private final Kit kit;
    private final String oldName;
    private String newName;

    public KitRenameEvent(Kit kit, String oldName, String newName) {
        this.kit = kit;
        this.oldName = oldName;
        this.newName = newName;
    }

    /**
     * Gets the {@link Kit} being renamed.
     *
     * @return the {@link Kit} being renamed
     */
    public Kit getKit() {
        return kit;
    }

    /**
     * Gets the previous name of this {@link Kit}.
     *
     * @return the previous {@link Kit} name
     */
    public String getOldName() {
        return oldName;
    }

    /**
     * Gets the new name of this {@link Kit}.
     *
     * @return the new {@link Kit} name
     */
    public String getNewName() {
        return newName;
    }

    /**
     * Sets the new name of this {@link Kit}.
     *
     * @param newName the new name to set
     */
    public void setNewName(String newName) {
        this.newName = newName;
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