package com.doctordark.base.cmd.module.essential;

import com.doctordark.base.cmd.BaseCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * Command used to set walk or fly speeds.
 */
public class SpeedCommand extends BaseCommand {

    public SpeedCommand() {
        super("speed", "Sets the fly/walk speed of a player.", "base.command.speed");
        this.setAliases(new String[]{});
        this.setUsage("/(command) <fly|walk> <speedMultiplier> [playerName]");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        sender.sendMessage(ChatColor.RED + "This command is currently not implemented!");
        return true;
    }
}
