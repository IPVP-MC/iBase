package com.doctordark.base.cmd.module.chat.messaging.event;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.Set;

/**
 * Event called when a player is about to send a message.
 */
public class PlayerPreMessageEvent extends Event implements Cancellable {

    private static HandlerList handlers = new HandlerList();
    private final Player sender;
    private final Set<Player> recipients;
    private final String message;
    private final boolean isReply;
    private boolean cancelled = false;

    public PlayerPreMessageEvent(Player sender, Set<Player> recipients, String message, boolean isReply) {
        this.sender = sender;
        this.recipients = recipients;
        this.message = message;
        this.isReply = isReply;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public CommandSender getSender() {
        return sender;
    }

    public Set<Player> getRecipients() {
        return recipients;
    }

    public String getMessage() {
        return message;
    }

    public boolean isReply() {
        return isReply;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
