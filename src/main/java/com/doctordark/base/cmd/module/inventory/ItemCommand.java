package com.doctordark.base.cmd.module.inventory;

import com.doctordark.base.cmd.BaseCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * Command used to spawn an item.
 */
public class ItemCommand extends BaseCommand {

    public ItemCommand() {
        super("item", "Spawns an item.", "base.command.item");
        this.setAliases(new String[]{});
        this.setUsage("/(command) <itemName> [quantity]");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        sender.sendMessage(ChatColor.RED + "This command is currently not implemented!");
        return true;
    }
}
