package org.ipvp.ibasic.freeze;

import org.ipvp.ibasic.IBasic;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandFreeze implements CommandExecutor {

    private IBasic plugin;

    public CommandFreeze(IBasic plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 1) {
            sender.sendMessage(ChatColor.RED + "Correct Usage: /" + label + " <player>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Player '" + args[0] + "' is not online.");
            return true;
        }

        FreezeType freezeType = plugin.getFreezeListener().getFreezeType(target);

        if (freezeType == null) {
            plugin.getFreezeListener().freeze(target, sender, FreezeType.FREEZE);
            sender.sendMessage(ChatColor.GREEN + target.getName() + " is now frozen.");
            target.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "You are now frozen.");
            return true;
        }

        if (FreezeType.FREEZE == freezeType) {
            plugin.getFreezeListener().unfreeze(target, FreezeType.FREEZE);
            sender.sendMessage(ChatColor.RED + target.getName() + " is now unfrozen.");
            target.sendMessage(ChatColor.GREEN.toString() + ChatColor.BOLD + "You are un-frozen.");
            return true;
        }

        if (FreezeType.HALT == freezeType) {
            sender.sendMessage(ChatColor.RED + target.getName() + " is currently halted. Use /halt " + target.getName() + " to unhalt.");
            return true;
        }

        // Support in-case extra enums are added.
        sender.sendMessage(ChatColor.RED + "Freeze type currently not implemented, inform an Administrator");
        return true;
    }
}
