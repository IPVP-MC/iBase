package com.doctordark.base.cmd.module.essential;

import com.doctordark.base.cmd.BaseCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * Command used to set a players gamemode.
 */
public class GamemodeCommand extends BaseCommand {

    public GamemodeCommand() {
        super("gamemode", "Sets a gamemode for a player.", "base.command.gamemode");
        this.setAliases(new String[]{"gm"});
        this.setUsage("/(command) <gamemode> <playerName>");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        return true;
    }
}
