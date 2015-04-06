package com.doctordark.base.command.module.chat;

import com.doctordark.base.BasePlugin;
import com.doctordark.base.command.BaseCommand;
import com.doctordark.base.command.module.chat.event.PlayerMessageEvent;
import com.google.common.collect.Sets;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ReplyCommand extends BaseCommand {

    private static final long VANISH_REPLY_TIMEOUT = TimeUnit.SECONDS.toMillis(45L);
    private final BasePlugin plugin;

    public ReplyCommand(BasePlugin plugin) {
        super("reply", "Replies to the last conversing player.", "base.command.reply");
        this.setAliases(new String[]{"r", "respond"});
        this.setUsage("/(command) <message>");
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only executable for players.");
            return true;
        }

        Player player = (Player) sender;
        Player target = plugin.getUserManager().getUser(player.getUniqueId()).getLastRepliedTo();

        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Usage: " + getUsage(label));

            if ((target != null) && (canSee(sender, target))) {
                sender.sendMessage(ChatColor.RED + "You are in a conversation with " + target.getName() + ".");
            }

            return true;
        }

        UUID uuid = player.getUniqueId();

        if ((target == null) || (!canSee(sender, target) && System.currentTimeMillis() - plugin.getUserManager().getUser(uuid).getLastReceivedMessageMillis() > VANISH_REPLY_TIMEOUT)) {
            sender.sendMessage(ChatColor.GOLD + "There is no player to reply to.");
            return true;
        }

        StringBuilder builder = new StringBuilder();
        for (String argument : args) {
            builder.append(argument).append(" ");
        }

        String message = builder.toString();
        Set<Player> recipients = Sets.newHashSet(target);

        PlayerMessageEvent playerMessageEvent = new PlayerMessageEvent(player, recipients, message, false);
        Bukkit.getServer().getPluginManager().callEvent(playerMessageEvent);
        if (!playerMessageEvent.isCancelled()) {
            playerMessageEvent.send();
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return (args.length == 1) ? null : Collections.<String>emptyList();
    }
}