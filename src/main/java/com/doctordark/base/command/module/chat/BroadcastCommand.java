package com.doctordark.base.command.module.chat;

import com.doctordark.base.BasePlugin;
import com.doctordark.base.command.BaseCommand;
import org.apache.commons.lang3.StringUtils;
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

        String message = StringUtils.join(args, ' ', 0, args.length);

        if (message.length() < 3) {
            sender.sendMessage(ChatColor.RED + "Broadcasts must be at least 3 characters.");
            return true;
        }

        String format = plugin.getServerHandler().getBroadcastFormat();

        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', String.format(Locale.ENGLISH, format, message)));
        return true;
    }
}
