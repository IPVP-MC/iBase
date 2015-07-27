package com.doctordark.base.command.module.chat;

import com.doctordark.base.command.BaseCommand;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class BroadcastRawCommand extends BaseCommand {

    public BroadcastRawCommand() {
        super("broadcastraw", "Broadcasts a raw message to the server.", "base.command.broadcastrwa");
        setAliases(new String[]{"bcraw", "raw", "rawcast"});
        setUsage("/(command) <text..>");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Usage: " + getUsage(label));
            return true;
        }

        String message = StringUtils.join(args, ' ', 0, args.length);

        if (message.length() < 6) {
            sender.sendMessage(ChatColor.RED + "Broadcasts must be at least 6 characters.");
            return true;
        }

        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', message));
        return true;
    }
}
