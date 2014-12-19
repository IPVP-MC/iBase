package com.doctordark.base.cmd.module.essential;

import com.doctordark.base.cmd.BaseCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Command used to check a players position.
 */
public class PositionCommand extends BaseCommand {

    public PositionCommand() {
        super("position", "Checks the position of a player.", "base.command.position");
        this.setAliases(new String[]{});
        this.setUsage("/(command) <playerName>");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
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

        if ((target == null) || (!canSee(sender, target))) {
            sender.sendMessage(ChatColor.GOLD + "Player '" + ChatColor.WHITE + args[0] + ChatColor.GOLD + "' not found!");
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
}
