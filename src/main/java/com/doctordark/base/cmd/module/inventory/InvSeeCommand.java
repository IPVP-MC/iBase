package com.doctordark.base.cmd.module.inventory;

import com.doctordark.base.cmd.BaseCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

/**
 * Command used for checking inventory of players.
 */
public class InvSeeCommand extends BaseCommand {

    public InvSeeCommand() {
        super("invsee", "View the inventory of a player.", "base.command.invsee");
        this.setAliases(new String[]{"inventorysee", "inventory", "inv"});
        this.setUsage("/(command) <playerName>");
    }

    @Override
    public boolean isPlayerOnlyCommand() {
        return true;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only executable for players!");
            return true;
        }

        Player player = (Player) sender;

        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Usage: " + getUsage());
            return true;
        }

        Player target = Bukkit.getServer().getPlayer(args[0]);

        if (target == null) {
            sender.sendMessage(ChatColor.GOLD + "Player '" + ChatColor.WHITE + args[0] + ChatColor.GOLD + "' not found!");
            return true;
        }

        player.openInventory(target.getInventory());
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        return (args.length == 1) ? null : Collections.<String>emptyList();
    }
}
