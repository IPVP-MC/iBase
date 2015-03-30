package com.doctordark.base.command.module.chat;

import com.doctordark.base.command.BaseCommand;
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

        StringBuilder builder = new StringBuilder(args[0]);
        for (int i = 1; i < args.length; i++) {
            builder.append(" ").append(args[i]);
        }

        String message = builder.toString();

        if (message.length() < 6) {
            sender.sendMessage(ChatColor.RED + "Broadcasts must be at least 6 characters.");
            return true;
        }

        Bukkit.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', message));
        return true;
    }
}
