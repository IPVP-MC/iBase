package com.doctordark.base.cmd.module.essential;

import com.doctordark.base.cmd.BaseCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Command used for broadcasting messages.
 */
public class BroadcastCommand extends BaseCommand {

    private static final String FORMAT = "`6[Prefix] `e%s";

    public BroadcastCommand() {
        super("broadcast", "Broadcasts a messaging to the server.", "base.command.broadcast");
        this.setAliases(new String[]{});
        this.setUsage("/(command) <message>");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Usage: " + getUsage());
            return true;
        }

        StringBuilder message = new StringBuilder(args[0]);
        for (int i = 1; i < args.length; i++) {
            message.append(" ").append(args[i]);
        }

        Bukkit.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('`',
                String.format(Locale.ENGLISH, FORMAT, message.toString())));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        return Collections.emptyList();
    }
}