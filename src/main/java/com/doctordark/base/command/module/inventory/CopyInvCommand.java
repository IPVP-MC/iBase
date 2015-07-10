package com.doctordark.base.command.module.inventory;

import com.doctordark.base.command.BaseCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class CopyInvCommand  extends BaseCommand {

    public CopyInvCommand() {
        super("copyinv", "Clears a players inventory.", "base.command.copyinv");
        this.setAliases(new String[]{"cloneinv", "cloneinventory", "copyinventory", "invcopy", "inventorycopy", "invclone", "inventoryclone"});
        this.setUsage("/(command) <playerName>");
    }

    @Override
    public boolean isPlayerOnlyCommand() {
        return true;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Usage: " + getUsage(label));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null || !canSee(sender, target)) {
            sender.sendMessage(ChatColor.GOLD + "Player '" + ChatColor.WHITE + args[0] + ChatColor.GOLD + "' not found.");
            return true;
        }

        ((Player) sender).getInventory().setContents(target.getInventory().getContents());

        Command.broadcastCommandMessage(sender, ChatColor.YELLOW + "Copied inventory of player " + target.getName() + '.');
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return (args.length == 1) ? null : Collections.<String>emptyList();
    }
}
