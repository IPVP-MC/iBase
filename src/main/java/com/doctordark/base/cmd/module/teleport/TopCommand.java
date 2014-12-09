package com.doctordark.base.cmd.module.teleport;

import com.doctordark.base.cmd.BaseCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * Command used to teleport to highest available point.
 */
public class TopCommand extends BaseCommand {

    public TopCommand() {
        super("top", "Teleports to the highest safe spot.", "base.command.top");
        this.setAliases(new String[]{});
        this.setUsage("/(command)");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        return true;
    }
}
