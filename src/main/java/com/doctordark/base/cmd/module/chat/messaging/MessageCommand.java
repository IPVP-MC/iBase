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

public class MessageCommand extends BaseCommand {

    public MessageCommand() {
        super("message", "Sends a message to a recipient(s).", "base.command.message");
        this.setAliases(new String[]{"msg", "m", "whisper", "w", "tell"});
        this.setUsage("/(command) <playerName> <message>");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only executable for players!");
            return true;
        }

        Player player = (Player) sender;

        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: " + getUsage());
            return true;
        }

        Set<Player> recipients = new HashSet<Player>();
        Player target = Bukkit.getServer().getPlayer(args[0]);

        if ((target == null) || (!canSee(sender, target))) {
            sender.sendMessage(ChatColor.GOLD + "Player '" + ChatColor.WHITE + args[0] + ChatColor.GOLD + "' not found!");
            return true;
        }

        StringBuilder builder = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            builder.append(args[i]).append(" ");
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
