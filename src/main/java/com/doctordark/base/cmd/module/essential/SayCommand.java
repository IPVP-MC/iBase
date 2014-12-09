package com.doctordark.base.cmd.module.essential;

import com.doctordark.base.cmd.BaseCommand;
import com.doctordark.base.util.BaseUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

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
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Usage: " + getUsage());
            return true;
        }

        StringBuilder message = new StringBuilder(args[0]);
        for (int i = 1; i < args.length; i++) {
            message.append(" ").append(args[i]);
        }

        String name = BaseUtil.getDisplayName(sender);
        Bukkit.getServer().broadcastMessage(ChatColor.LIGHT_PURPLE + "[" + name + ChatColor.LIGHT_PURPLE + "] " + message.toString());
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        return Collections.emptyList();
    }
}
