package com.doctordark.base.command.module.inventory;

import com.doctordark.base.command.BaseCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class IdCommand extends BaseCommand {

    public IdCommand() {
        super("id", "Checks the ID/name of an item.");
        setUsage("/(command) [itemName]");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage(ChatColor.RED + "This command is currently not implemented.");
        return true;
    }
}
