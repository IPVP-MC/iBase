package com.doctordark.base.listener;

import com.doctordark.base.BasePlugin;
import com.doctordark.base.command.module.chat.event.PlayerMessageEvent;
import com.doctordark.base.user.BaseUser;
import com.doctordark.base.util.BaseUtil;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ChatListener implements Listener {

    private static final long AFK_TIME = TimeUnit.MINUTES.toMillis(5L);
    public static final String STAFF_CHAT_FORMAT = "%1$s: %2$s";
    private final BasePlugin plugin;

    public ChatListener(BasePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        String name = player.getName();
        BaseUser baseUser = plugin.getUserManager().getUser(uuid);
        for (Iterator<Player> iterator = event.getRecipients().iterator(); iterator.hasNext(); ) {
            Player target = iterator.next();
            BaseUser targetUser = plugin.getUserManager().getUser(target.getUniqueId());
            if (targetUser.isToggledChat()) {
                iterator.remove();
            } else {
                Set<String> ignoring = targetUser.getIgnoring();
                if (ignoring.contains(name)) {
                    iterator.remove();
                }
            }
        }

        if ((baseUser.isInStaffChat()) && (player.hasPermission("base.command.staffchat"))) {
            event.setCancelled(true);
            String format = ChatColor.AQUA + String.format(Locale.ENGLISH, "%1$s: %2$s", player.getName(), event.getMessage());
            Bukkit.getServer().getConsoleSender().sendMessage(format);
            for (Player other : Bukkit.getServer().getOnlinePlayers()) {
                BaseUser otherUser = plugin.getUserManager().getUser(other.getUniqueId());
                if ((!otherUser.isToggledStaffChat()) && (other.hasPermission("base.command.staffchat"))) {
                    other.sendMessage(format);
                }
            }

            return;
        }

        long remainingChatDisabled = plugin.getServerHandler().getRemainingChatDisabledMillis();
        if ((remainingChatDisabled > 0L) && (!player.hasPermission("base.command.disablechat"))) {
            player.sendMessage(ChatColor.RED + "For non staff members; global chat is currently disabled for another " + ChatColor.GOLD +
                    DurationFormatUtils.formatDurationWords(remainingChatDisabled, true, true) + ChatColor.RED + ".");
            event.setCancelled(true);
            return;
        }

        long remainingChatSlowed = plugin.getServerHandler().getRemainingChatSlowedMillis();
        if ((remainingChatSlowed > 0L) && (!player.hasPermission("base.command.slowchat"))) {
            long speakTimeRemaining = baseUser.getLastSpeakTimeRemaining();
            if (speakTimeRemaining <= 0L) {
                baseUser.updateLastSpeakTime();
                return;
            }

            event.setCancelled(true);
            long delayMillis = plugin.getServerHandler().getChatSlowedDelay() * 1000L;
            player.sendMessage(ChatColor.DARK_AQUA + "Global chat is currently in slow mode with a " + ChatColor.GRAY +
                    DurationFormatUtils.formatDurationWords(delayMillis, true, true) + ChatColor.DARK_AQUA + " delay for another " + ChatColor.GRAY +
                    DurationFormatUtils.formatDurationWords(remainingChatSlowed, true, true) + ChatColor.DARK_AQUA + ". You spoke " + ChatColor.GRAY +
                    DurationFormatUtils.formatDurationWords(delayMillis - speakTimeRemaining, true, true) + ChatColor.DARK_AQUA + " ago, so you gotta wait another " + ChatColor.GRAY +
                    DurationFormatUtils.formatDurationWords(speakTimeRemaining, true, true) + ChatColor.DARK_AQUA + ".");
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerPreMessage(PlayerMessageEvent event) {
        String message = event.getMessage();
        CommandSender sender = event.getSender();
        Player recipient = event.getRecipient();
        UUID recipientUUID = recipient.getUniqueId();
        if (!sender.hasPermission("base.messaging.bypass")) {
            BaseUser recipientUser = plugin.getUserManager().getUser(recipientUUID);
            if ((recipientUser.isToggledMessages()) || (recipientUser.getIgnoring().contains(sender.getName()))) {
                event.setCancelled(true);
                sender.sendMessage(ChatColor.RED + "That player has private messages toggled.");
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerMessage(PlayerMessageEvent event) {
        Player sender = event.getSender();
        Player recipient = event.getRecipient();
        String message = event.getMessage();

        if (BaseUtil.getIdleTime(recipient) > AFK_TIME) {
            sender.sendMessage(ChatColor.RED + "That player may not respond as their idle time is over " + DurationFormatUtils.formatDurationWords(AFK_TIME, true, true) + ".");
        }

        UUID senderUUID = sender.getUniqueId();
        String senderId = senderUUID.toString();
        String recipientId = recipient.getUniqueId().toString();

        String format = ChatColor.GOLD + "[" + ChatColor.DARK_RED + "SS: " + ChatColor.YELLOW + "%1$s" + ChatColor.WHITE + " -> " + ChatColor.YELLOW + "%2$s" + ChatColor.GOLD + "] %3$s";
        for (Player online : Bukkit.getServer().getOnlinePlayers()) {
            if ((!sender.equals(online)) && (!recipient.equals(online))) {
                BaseUser baseOnline = plugin.getUserManager().getUser(online.getUniqueId());
                Set<String> messageSpying = baseOnline.getMessageSpyingIds();
                if ((messageSpying.contains("all")) || (messageSpying.contains(recipientId)) || (messageSpying.contains(senderId))) {
                    online.sendMessage(String.format(Locale.ENGLISH, format, sender.getName(), recipient.getName(), message));
                }
            }
        }
    }
}
