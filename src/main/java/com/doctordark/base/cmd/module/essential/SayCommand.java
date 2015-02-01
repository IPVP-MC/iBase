package com.doctordark.base.cmd.module.essential;

import com.doctordark.base.cmd.BaseCommand;
import com.doctordark.base.util.BaseUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * Command used to a say messaging to the server.
 */
public class SayCommand extends BaseCommand {

    public SayCommand() {
        super("say", "Say a messaging to the server.", "base.command.say");
        this.setAliases(new String[]{});
        this.setUsage("/(command) <message>");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Usage: " + getUsage(label));
            return true;
        }

        StringBuilder message = new StringBuilder(args[0]);
        for (int i = 1; i < args.length; i++) {
            message.append(" ").append(args[i]);
        }

        String broadcast = message.toString();

        if (broadcast.length() < 6) {
            sender.sendMessage(ChatColor.RED + "Messages must be at least 6 characters.");
            return true;
        }

        String name = BaseUtil.getDisplayName(sender);
        String prefix = ChatColor.LIGHT_PURPLE + "[" + name + ChatColor.LIGHT_PURPLE + "]";
        Bukkit.getServer().broadcastMessage(prefix + " " + broadcast);
        return true;
    }
}
