package com.doctordark.base.listener;

import com.doctordark.base.BasePlugin;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Locale;
import java.util.UUID;

/**
 * Handles the chat thread for the server.
 */
public class ChatListener implements Listener {

    private final BasePlugin plugin;

    public ChatListener(BasePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (plugin.getUserManager().isInStaffChat(uuid) && player.hasPermission("base.command.staffchat")) {
            event.setCancelled(true);

            String format = ChatColor.AQUA + (String.format(Locale.ENGLISH, "%1$s: %2$s", player.getName(), event.getMessage()));
            //event.setFormat(format);

            Bukkit.getServer().getConsoleSender().sendMessage(format);
            for (Player other : Bukkit.getServer().getOnlinePlayers()) {
                if (other.hasPermission("base.command.staffchat")) {
                    other.sendMessage(format);
                }
            }

            return;
        }

        if (!plugin.getServerManager().isChatEnabled() && !player.hasPermission("base.command.disablechat")) {
            player.sendMessage(ChatColor.RED + "Global chat is currently disabled for non-staff.");
            event.setCancelled(true);
            return;
        }

        if (plugin.getServerManager().isChatSlowed() && !player.hasPermission("base.command.slowchat")) {
            long last = plugin.getUserManager().getRemainingChatDelayTime(uuid);

            if (last <= 0) {
                plugin.getUserManager().updateLastSpeakTime(uuid);
                return;
            }

            int chatSlowedDelay = plugin.getServerManager().getSlowChatDelay();

            event.setCancelled(true);
            player.sendMessage(ChatColor.DARK_AQUA + "Chat is currently in slow mode with a " + chatSlowedDelay + " second delay.");
            player.sendMessage(ChatColor.DARK_AQUA + "You spoke " + DurationFormatUtils.formatDurationWords((chatSlowedDelay * 1000L) - last, true, true) + " ago. " +
                    "You gotta wait another " + DurationFormatUtils.formatDurationWords(last, true, true) + ".");
        }
    }
}
