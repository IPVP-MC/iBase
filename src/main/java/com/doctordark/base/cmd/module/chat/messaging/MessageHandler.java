package com.doctordark.base.cmd.module.chat.messaging;

import com.doctordark.base.cmd.module.chat.messaging.event.PlayerMessageEvent;
import com.google.common.collect.Iterables;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class MessageHandler implements Listener {

    private Map<UUID, UUID> lastRepliedTo = new HashMap<UUID, UUID>();

    public Player getLastRepliedTo(Player sender) {
        UUID uuid = sender.getUniqueId();
        UUID last = lastRepliedTo.containsKey(uuid) ? lastRepliedTo.get(uuid) : null;
        return Bukkit.getServer().getPlayer(last);
    }

    public void setLastRepliedTo(Player sender, Player recipient) {
        lastRepliedTo.put(sender.getUniqueId(), recipient.getUniqueId());
    }

    @EventHandler
    public void onPlayerMessage(PlayerMessageEvent event) {
        Player player = event.getSender();
        Set<Player> recipients = event.getRecipients();
        setLastRepliedTo(player, Iterables.getLast(recipients));
        for (Player recipient : recipients) {
            setLastRepliedTo(recipient, player);
        }
    }
}
