package com.doctordark.base.command.module.chat;

import com.doctordark.base.BasePlugin;
import com.doctordark.base.command.BaseCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Locale;

public class BroadcastCommand extends BaseCommand {

    private final BasePlugin plugin;

    public BroadcastCommand(BasePlugin plugin) {
        super("broadcast", "Broadcasts a message to the server.", "base.command.broadcast");
        setAliases(new String[]{"bc"});
        setUsage("/(command) <text..>");
        this.plugin = plugin;
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

        String format = this.plugin.getServerHandler().getBroadcastFormat();

        Bukkit.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', String.format(Locale.ENGLISH, format, message)));
        return true;
    }
}
