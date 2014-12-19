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

public class TogglepmCommand extends BaseCommand {

    public TogglepmCommand() {
        super("togglepm", "Toggles private messages.", "base.command.togglepm");
        this.setAliases(new String[]{});
        this.setUsage("/(command)");
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
        return Collections.emptyList();
    }
}
