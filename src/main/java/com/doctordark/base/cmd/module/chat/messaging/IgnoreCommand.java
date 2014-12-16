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

public class IgnoreCommand extends BaseCommand {

    public IgnoreCommand() {
        super("ignore", "Ignores a player from messages.", "base.command.ignore");
        this.setAliases(new String[]{});
        this.setUsage("/(command) <playerName>");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only executable for players!");
            return true;
        }

        sender.sendMessage(ChatColor.RED + "This command is currently not implemented!");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        return (args.length == 1) ? null : Collections.<String>emptyList();
    }
}
