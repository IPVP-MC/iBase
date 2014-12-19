package com.doctordark.base.cmd.module.chat.messaging;

import com.doctordark.base.cmd.BaseCommand;
import com.doctordark.base.cmd.module.chat.messaging.event.PlayerMessageEvent;
import com.doctordark.base.cmd.module.chat.messaging.event.PlayerPreMessageEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ReplyCommand extends BaseCommand {

    public ReplyCommand() {
        super("reply", "Replies to the last conversing player.", "base.command.reply");
        this.setAliases(new String[]{"r", "respond"});
        this.setUsage("/(command) <message>");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only executable for players!");
            return true;
        }

        Player player = (Player) sender;

        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Usage: " + getUsage());
            return true;
        }

        Set<Player> recipients = new HashSet<Player>();
        Player target = getBasePlugin().getMessageHandler().getLastRepliedTo(player);

        if ((target == null) || (!canSee(sender, target))) {
            sender.sendMessage(ChatColor.GOLD + "There is no player to reply to!");
            return true;
        }

        StringBuilder builder = new StringBuilder();
        for (String argument : args) {
            builder.append(argument).append(" ");
        }

        recipients.add(target);

        PlayerPreMessageEvent preMessageEvent = new PlayerPreMessageEvent(player, recipients, false);
        Bukkit.getServer().getPluginManager().callEvent(preMessageEvent);

        if (preMessageEvent.isCancelled()) {
            return true;
        }

        String message = builder.toString();

        for (CommandSender recipient : recipients) {
            sender.sendMessage(ChatColor.GRAY + "<" + sender.getName() + " -> " + recipient.getName() + "> " + message);
            recipient.sendMessage(ChatColor.GRAY + "<" + sender.getName() + " -> " + recipient.getName() + "> " + message);
        }

        PlayerMessageEvent event = new PlayerMessageEvent(player, recipients, false);
        Bukkit.getServer().getPluginManager().callEvent(event);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        return (args.length == 1) ? null : Collections.<String>emptyList();
    }
}
