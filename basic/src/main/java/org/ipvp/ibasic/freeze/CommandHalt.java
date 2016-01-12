package org.ipvp.ibasic.freeze;

import org.ipvp.ibasic.IBasic;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class CommandHalt implements CommandExecutor, Listener {

    private final IBasic plugin;

    public CommandHalt(IBasic plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Correct Usage: /" + command.getName() + " <player>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Player '" + args[0] + "' is not online.");
            return true;
        }

        FreezeType freezeType = plugin.getFreezeListener().getFreezeType(target);

        if (freezeType == null) {
            plugin.getFreezeListener().freeze(target, sender, FreezeType.HALT);
            sender.sendMessage(ChatColor.GREEN + target.getName() + " is now halted.");
            return true;
        }

        if (FreezeType.HALT == freezeType) {
            plugin.getFreezeListener().unfreeze(target, FreezeType.HALT);
            sender.sendMessage(ChatColor.RED + target.getName() + " is no longer halted.");
            target.sendMessage(ChatColor.RED + "You are no longer halted.");
            return true;
        }

        /*
        Allow halts to override freezes.
        if (FreezeType.FREEZE == freezeType) {
            sender.sendMessage(ChatColor.RED + target.getName() + " is currently frozen. Use /freeze " + target.getName() + " to unfreeze.");
            return true;
        }*/

        // Support in-case extra enums are added.
        sender.sendMessage(ChatColor.RED + "Freeze type currently not implemented, inform an Administrator");
        return true;
    }
}
