package com.doctordark.base.command.module.inventory;

import com.doctordark.base.command.BaseCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class ItemCommand extends BaseCommand {

    public ItemCommand() {
        super("item", "Spawns an item.");
        setAliases(new String[]{"i", "get"});
        setUsage("/(command) <itemName> [quantity]");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage(ChatColor.RED + "This command is currently not implemented.");
        return true;
    }
}
