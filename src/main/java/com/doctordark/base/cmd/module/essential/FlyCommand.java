package com.doctordark.base.cmd.module.essential;

import com.doctordark.base.cmd.BaseCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

/**
 * Command used for flight toggling of players.
 */
public class FlyCommand extends BaseCommand {

    public FlyCommand() {
        super("fly", "Toggles flight mode for a player.", "base.command.fly");
        this.setAliases(new String[]{});
        this.setUsage("/(command) <playerName>");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player target;
        if (args.length < 1) {
            if (sender instanceof Player) {
                target = (Player)sender;
            } else {
                sender.sendMessage(ChatColor.RED + "Usage: " + getUsage());
                return true;
            }
        } else {
            target = Bukkit.getServer().getPlayer(args[0]);
        }

        if ((target == null) || (sender instanceof Player && !((Player)sender).canSee(target))) {
            sender.sendMessage(ChatColor.GOLD + "Player '" + ChatColor.WHITE + args[0] + ChatColor.GOLD + "' not found!");
            return true;
        }

        boolean flight = target.getAllowFlight();

        if (flight) {
            target.setFlying(false);
        }

        target.setAllowFlight(flight);

        Command.broadcastCommandMessage(sender, ChatColor.YELLOW + "Flight mode of " + target.getDisplayName() + " set to " + flight + ".");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        return (args.length == 1) ? null : Collections.<String>emptyList();
    }
}
