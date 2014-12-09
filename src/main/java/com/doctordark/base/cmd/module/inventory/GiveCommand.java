package com.doctordark.base.cmd.module.inventory;

import com.doctordark.base.cmd.BaseCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * Command used to give items to players.
 */
public class GiveCommand extends BaseCommand {

    public GiveCommand() {
        super("give", "Gives an item to a player.", "base.command.give");
        this.setAliases(new String[]{});
        this.setUsage("/(command) <playerName> <itemName> [quantity]");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        return true;
    }
}
