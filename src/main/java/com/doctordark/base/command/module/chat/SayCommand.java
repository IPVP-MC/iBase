package com.doctordark.base.command.module.chat;

import com.doctordark.base.command.BaseCommand;
import com.doctordark.util.BukkitUtils;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class SayCommand extends BaseCommand {

    public SayCommand() {
        super("say", "Say a messaging to the server.", "base.command.say");
        setUsage("/(command) <message>");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Usage: " + getUsage(label));
            return true;
        }

        String message = StringUtils.join(args, ' ', 1, args.length);

        if (message.length() < 6) {
            sender.sendMessage(ChatColor.RED + "Messages must be at least 6 characters.");
            return true;
        }

        String prefix = ChatColor.LIGHT_PURPLE + "[" + BukkitUtils.getDisplayName(sender) + ChatColor.LIGHT_PURPLE + ']';
        Bukkit.broadcastMessage(prefix + ' ' + message);
        return true;
    }
}
