package com.doctordark.base.cmd.module.inventory;

import com.doctordark.base.cmd.BaseCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * Command used to check the ID of an item.
 */
public class IdCommand extends BaseCommand {

    public IdCommand() {
        super("id", "Checks the ID/name of an item.", "base.command.id");
        this.setAliases(new String[]{});
        this.setUsage("/(command) [itemName]");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        return true;
    }
}
