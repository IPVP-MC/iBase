package com.doctordark.base.cmd.module.chat.messaging;

import com.doctordark.base.cmd.module.chat.messaging.event.PlayerMessageEvent;
import com.google.common.collect.Iterables;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class MessageHandler implements Listener {

    private final Map<UUID, UUID> lastRepliedTo = new HashMap<UUID, UUID>();

    /**
     * Gets the last replied recipient for a player.
     *
     * @param sender the player to get for
     * @return the recipient last replied to
     */
    public Player getLastRepliedTo(Player sender) {
        UUID uuid = sender.getUniqueId();
        if (lastRepliedTo.containsKey(uuid)) {
            UUID last = lastRepliedTo.get(uuid);
            return Bukkit.getServer().getPlayer(last);
        } else {
            return null;
        }
    }

    /**
     * Sets the last replied recipient for a player.
     *
     * @param sender the player to set for
     * @param recipient the recipient last replied to
     */
    public void setLastRepliedTo(Player sender, Player recipient) {
        lastRepliedTo.put(sender.getUniqueId(), recipient.getUniqueId());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerMessage(PlayerMessageEvent event) {
        Player player = event.getSender();
        Set<Player> recipients = event.getRecipients();
        setLastRepliedTo(player, Iterables.getLast(recipients));
        for (Player recipient : recipients) {
            setLastRepliedTo(recipient, player);
        }
    }
}
