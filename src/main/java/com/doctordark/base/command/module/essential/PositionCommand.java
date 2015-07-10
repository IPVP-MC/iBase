package com.doctordark.base.command.module.essential;

import com.doctordark.base.command.BaseCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

/**
 * Command used to check a players position.
 */
public class PositionCommand extends BaseCommand {

    public PositionCommand() {
        super("position", "Checks the position of a player.", "base.command.position");
        this.setUsage("/(command) <playerName>");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        final Player target;
        if (args.length > 0) {
            target = Bukkit.getServer().getPlayer(args[0]);
        } else if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Usage: " + getUsage(label));
            return true;
        } else {
            target = (Player) sender;
        }

        if ((target == null) || (!canSee(sender, target))) {
            sender.sendMessage(ChatColor.GOLD + "Player '" + ChatColor.WHITE + args[0] + ChatColor.GOLD + "' not found.");
            return true;
        }

        Location pos = target.getLocation();
        sender.sendMessage(ChatColor.YELLOW + "Player: " + target.getName());
        sender.sendMessage(ChatColor.YELLOW + "World: " + target.getWorld().getName());
        sender.sendMessage(ChatColor.YELLOW + String.format("Location: (%.3f, %.3f, %.3f)", pos.getX(), pos.getY(), pos.getZ()));
        sender.sendMessage(ChatColor.YELLOW + "Depth: " + (int) Math.floor(pos.getY()));
        //sender.sendMessage(ChatColor.YELLOW + String.format("Direction: %s", getCardinalDirection(player)));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return (args.length == 1) ? null : Collections.<String>emptyList();
    }
}
