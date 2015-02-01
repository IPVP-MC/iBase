package com.doctordark.base.cmd.module.chat.messaging;

import com.doctordark.base.BasePlugin;
import com.doctordark.base.cmd.BaseCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class IgnoreCommand extends BaseCommand {

    private final BasePlugin plugin;

    public IgnoreCommand(BasePlugin plugin) {
        super("ignore", "Ignores a player from messages.", "base.command.ignore");
        this.setAliases(new String[]{});
        this.setUsage("/(command) <playerName>");
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only executable for players.");
            return true;
        }

        sender.sendMessage(ChatColor.RED + "This command is currently not implemented.");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return (args.length == 1) ? null : Collections.<String>emptyList();
    }
}
