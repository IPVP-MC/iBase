package com.doctordark.base.cmd.module.chat.messaging.event;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.Set;

/**
 * Event called when a player has sent a message.
 */
public class PlayerMessageEvent extends Event {

    private static HandlerList handlers = new HandlerList();

    private final Player sender;
    private final Set<Player> recipients;
    private final boolean isReply;

    public PlayerMessageEvent(Player sender, Set<Player> recipients, boolean isReply) {
        this.sender = sender;
        this.recipients = recipients;
        this.isReply = isReply;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Player getSender() {
        return sender;
    }

    public Set<Player> getRecipients() {
        return recipients;
    }

    public boolean isReply() {
        return isReply;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
