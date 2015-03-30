package com.doctordark.base.command.module.chat.event;

import com.doctordark.base.BasePlugin;
import com.doctordark.base.user.BaseUser;
import com.doctordark.base.user.UserManager;
import com.google.common.collect.Iterables;
import org.apache.commons.lang.Validate;
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
        Validate.notNull(sender, "The sender cannot be null");
        Validate.notNull(recipient, "The recipient cannot be null");

        BasePlugin plugin = BasePlugin.getPlugin(BasePlugin.class);
        UserManager userManager = plugin.getUserManager();

        BaseUser sendingUser = userManager.getUser(sender.getUniqueId());
        BaseUser recipientUser = userManager.getUser(recipient.getUniqueId());

        sendingUser.setLastRepliedTo(recipientUser);
        recipientUser.setLastRepliedTo(sendingUser);

        long millis = System.currentTimeMillis();
        recipientUser.setLastReceivedMessageMillis(millis);

        this.sender.sendMessage(ChatColor.BLUE + "(" + sender.getName() + " -> " + recipient.getName() + "): " + ChatColor.DARK_AQUA + message);
        this.recipient.sendMessage(ChatColor.BLUE + "(" + sender.getName() + " -> " + recipient.getName() + "): " + ChatColor.DARK_AQUA + message);
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
