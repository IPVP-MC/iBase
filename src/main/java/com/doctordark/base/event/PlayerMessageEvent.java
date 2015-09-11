package com.doctordark.base.event;

import com.doctordark.base.BasePlugin;
import com.doctordark.base.user.BaseUser;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.Set;

public class PlayerMessageEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private boolean cancelled = false;
    private final Player sender;
    private final Player recipient;
    private final String message;
    private final boolean isReply;

    public PlayerMessageEvent(Player sender, Set<Player> recipients, String message, boolean isReply) {
        this.sender = sender;
        this.recipient = Iterables.getFirst(recipients, null);
        this.message = message;
        this.isReply = isReply;
    }

    public Player getSender() {
        return this.sender;
    }

    public Player getRecipient() {
        return this.recipient;
    }

    public String getMessage() {
        return this.message;
    }

    public boolean isReply() {
        return this.isReply;
    }

    public void send() {
        Preconditions.checkNotNull(sender, "The sender cannot be null");
        Preconditions.checkNotNull(recipient, "The recipient cannot be null");

        BasePlugin plugin = BasePlugin.getPlugin();
        BaseUser sendingUser = plugin.getUserManager().getUser(sender.getUniqueId());
        BaseUser recipientUser = plugin.getUserManager().getUser(recipient.getUniqueId());

        sendingUser.setLastRepliedTo(recipientUser.getUniqueId());
        recipientUser.setLastRepliedTo(sendingUser.getUniqueId());

        long millis = System.currentTimeMillis();
        recipientUser.setLastReceivedMessageMillis(millis);

        sender.sendMessage(ChatColor.BLUE + "(" + sender.getName() + " -> " + recipient.getName() + "): " + ChatColor.DARK_AQUA + message);
        recipient.sendMessage(ChatColor.BLUE + "(" + sender.getName() + " -> " + recipient.getName() + "): " + ChatColor.DARK_AQUA + message);
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
