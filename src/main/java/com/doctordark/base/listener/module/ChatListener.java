package com.doctordark.base.listener.module;

import com.doctordark.base.BasePlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Handles the chat thread for the server.
 */
public class ChatListener implements Listener {

    private final BasePlugin plugin;

    public ChatListener(BasePlugin plugin) {
        this.plugin = plugin;
    }

    private final Map<UUID, Long> lastChatTime = new HashMap<UUID, Long>();

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onChatModifier(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        boolean chatEnabled = plugin.getServerManager().isChatEnabled();
        boolean chatSlowed = plugin.getServerManager().isChatSlowed();

        if (!chatEnabled) {
            if (!player.hasPermission("base.command.disablechat")) {
                player.sendMessage(ChatColor.RED + "Chat is currently disabled!");
                event.getRecipients().clear();
            }
        } else if (chatSlowed) {
            if (!player.hasPermission("base.command.slowchat")) {
                double last = plugin.getUserManager().getRemainingChatTime(uuid);

                if (last > 0) {
                    int chatSlowedDelay = plugin.getServerManager().getSlowChatDelay();

                    player.sendMessage(ChatColor.DARK_AQUA + "Chat is currently in slow mode with a " + ((int) chatSlowedDelay) + " second delay!");
                    player.sendMessage(ChatColor.DARK_AQUA + "You spoke " + String.format("%.1f", chatSlowedDelay + 0.0 - last) + " seconds ago! " +
                            "You gotta wait " + String.format("%.1f", last) + " more seconds!");
                    event.getRecipients().clear();
                } else {
                    long millis = System.currentTimeMillis();
                    plugin.getUserManager().setLastChatTime(uuid, millis);
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        boolean staffChat = plugin.getUserManager().isInStaffChat(uuid);

        if (staffChat) {
            event.setFormat(ChatColor.AQUA + player.getName() + ": %2$s");

            Set<Player> recipients = event.getRecipients();
            recipients.clear();

            for (Player other : Bukkit.getServer().getOnlinePlayers()) {
                if (other.hasPermission("base.command.staffchat")) {
                    recipients.add(other);
                }
            }
        }
    }
}
