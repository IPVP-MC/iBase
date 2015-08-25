package com.doctordark.base.listener;

import com.doctordark.base.BasePlugin;
import com.doctordark.base.event.PlayerMessageEvent;
import com.doctordark.base.user.BaseUser;
import com.doctordark.base.user.ServerParticipator;
import com.doctordark.util.BukkitUtils;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.permissions.Permissible;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ChatListener implements Listener {

    private static final String MESSAGE_SPY_FORMAT = ChatColor.GOLD + "[" + ChatColor.DARK_RED + "SS: " +
            ChatColor.YELLOW + "%1$s" + ChatColor.WHITE + " -> " + ChatColor.YELLOW + "%2$s" + ChatColor.GOLD + "] %3$s";

    private static final String STAFF_CHAT_FORMAT = "%1$s: %2$s";

    private static final String STAFF_CHAT_NOTIFY = "base.command.staffchat";
    private static final String SLOWED_CHAT_BYPASS = "base.slowchat.bypass";
    private static final String TOGGLED_CHAT_BYPASS = "base.disablechat.bypass";

    private static final long AUTO_IDLE_TIME = TimeUnit.MINUTES.toMillis(5L);

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
            if (baseUser.isInStaffChat() && !targetUser.isStaffChatVisible()) {
                iterator.remove();
            } else if (targetUser.getIgnoring().contains(name)) {
                iterator.remove();
            } else if (!targetUser.isGlobalChatVisible()) {
                iterator.remove();
            }
        }

        // More thread-safe alternative.
        if (baseUser.isInStaffChat()) {
            Set<CommandSender> staffChattable = Sets.newHashSet();
            for (Permissible permissible : Bukkit.getPluginManager().getPermissionSubscriptions(STAFF_CHAT_NOTIFY)) {
                if (permissible instanceof CommandSender) {
                    staffChattable.add((CommandSender) permissible);
                }
            }

            if (staffChattable.contains(player) && baseUser.isInStaffChat()) {
                String format = ChatColor.AQUA + String.format(Locale.ENGLISH, STAFF_CHAT_FORMAT, player.getName(), event.getMessage());
                for (CommandSender target : staffChattable) {
                    // Ignore those who have staff chat toggled off.
                    if (target instanceof Player) {
                        Player targetPlayer = (Player) target;
                        BaseUser targetUser = plugin.getUserManager().getUser(targetPlayer.getUniqueId());
                        if (targetUser.isStaffChatVisible()) {
                            target.sendMessage(format);
                        } else if (target.equals(player)) {
                            target.sendMessage(ChatColor.RED + "Your message was sent, but you cannot see staff chat messages as your notifications are disabled: Use /togglesc.");
                        }
                    }
                }

                event.setCancelled(true);
                return;
            }
        }

        long remainingChatDisabled = plugin.getServerHandler().getRemainingChatDisabledMillis();
        if (remainingChatDisabled > 0L && !player.hasPermission(TOGGLED_CHAT_BYPASS)) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "Global chat is currently disabled for another " + ChatColor.GOLD +
                    DurationFormatUtils.formatDurationWords(remainingChatDisabled, true, true) + ChatColor.RED + '.');
            return;
        }

        long remainingChatSlowed = plugin.getServerHandler().getRemainingChatSlowedMillis();
        if ((remainingChatSlowed > 0L) && (!player.hasPermission(SLOWED_CHAT_BYPASS))) {
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
                    DurationFormatUtils.formatDurationWords(speakTimeRemaining, true, true) + ChatColor.DARK_AQUA + '.');
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerPreMessage(PlayerMessageEvent event) {
        CommandSender sender = event.getSender();
        Player recipient = event.getRecipient();
        UUID recipientUUID = recipient.getUniqueId();
        if (!sender.hasPermission("base.messaging.bypass")) {
            BaseUser recipientUser = plugin.getUserManager().getUser(recipientUUID);
            if (!recipientUser.isMessagesVisible() || recipientUser.getIgnoring().contains(sender.getName())) {
                event.setCancelled(true);
                sender.sendMessage(ChatColor.RED + recipient.getName() + " has private messaging toggled.");
            }

            return;
        }

        ServerParticipator senderParticipator = plugin.getUserManager().getParticipator(sender);
        if (!senderParticipator.isMessagesVisible()) {
            event.setCancelled(true);
            sender.sendMessage(ChatColor.RED + "You have private messages toggled.");
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerMessage(PlayerMessageEvent event) {
        Player sender = event.getSender();
        Player recipient = event.getRecipient();
        String message = event.getMessage();

        if (BukkitUtils.getIdleTime(recipient) > AUTO_IDLE_TIME) {
            sender.sendMessage(ChatColor.RED + recipient.getName() + " may not respond as their idle time is over " +
                    DurationFormatUtils.formatDurationWords(AUTO_IDLE_TIME, true, true) + '.');
        }

        UUID senderUUID = sender.getUniqueId();
        String senderId = senderUUID.toString();
        String recipientId = recipient.getUniqueId().toString();

        Collection<CommandSender> recipients = new HashSet<>(Bukkit.getOnlinePlayers());
        recipients.add(Bukkit.getConsoleSender());
        for (CommandSender target : recipients) {
            if (!target.equals(sender) && !recipient.equals(sender)) {
                ServerParticipator participator = plugin.getUserManager().getParticipator(target);
                Set<String> messageSpying = participator.getMessageSpying();
                if (messageSpying.contains("all") || messageSpying.contains(recipientId) || messageSpying.contains(senderId)) {
                    target.sendMessage(String.format(Locale.ENGLISH, MESSAGE_SPY_FORMAT, sender.getName(), recipient.getName(), message));
                }
            }
        }
    }
}
