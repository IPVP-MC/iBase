package com.doctordark.base.command.module.inventory;

import com.doctordark.base.command.BaseCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class GiveCommand extends BaseCommand {

    public GiveCommand() {
        super("give", "Gives an item to a player.");
        setUsage("/(command) <playerName> <itemName> [quantity]");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage(ChatColor.RED + "This command is currently not implemented.");
        return true;
    }
}
