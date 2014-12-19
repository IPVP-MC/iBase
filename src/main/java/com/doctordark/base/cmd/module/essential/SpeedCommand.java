package com.doctordark.base.cmd.module.essential;

import com.doctordark.base.cmd.BaseCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Command used to set walk or fly speeds.
 */
public class SpeedCommand extends BaseCommand {

    private static final float DEFAULT_FLIGHT_SPEED = 0.1f, DEFAULT_WALK_SPEED = 0.2f;

    public SpeedCommand() {
        super("speed", "Sets the fly/walk speed of a player.", "base.command.speed");
        this.setAliases(new String[]{});
        this.setUsage("/(command) <fly|walk> <speedMultiplier> [playerName]");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: " + getUsage());
            return true;
        }

        // Find out who we are applying the speed to first.
        Player target;
        if (args.length < 3) {
            if (sender instanceof Player) {
                target = (Player)sender;
            } else {
                sender.sendMessage(ChatColor.RED + "Usage: " + getUsage());
                return true;
            }
        } else {
            target = Bukkit.getServer().getPlayer(args[2]);
        }

        if ((target == null) || (!canSee(sender, target))) {
            sender.sendMessage(ChatColor.GOLD + "Player '" + ChatColor.WHITE + args[2] + ChatColor.GOLD + "' not found!");
            return true;
        }

        // Check if we are setting the walk of fly speed.
        Boolean flight = null;
        if (args[0].equalsIgnoreCase("fly")) {
            flight = true;
        } else if (args[0].equalsIgnoreCase("walk")) {
            flight = false;
        }

        if (flight == null) {
            sender.sendMessage(ChatColor.RED + "Usage: " + getUsage());
            return true;
        }

        float multiplier;

        if (args[1].equalsIgnoreCase("reset")) {
            multiplier = (flight) ? DEFAULT_FLIGHT_SPEED : DEFAULT_WALK_SPEED;
        } else {
            try {
                multiplier = Float.parseFloat(args[1]);
            } catch (NumberFormatException ex) {
                sender.sendMessage(ChatColor.RED + "Invalid speed multiplier: '" + args[1] + "'.");
                return true;
            }
        }

        if (flight) {
            final float flySpeed = DEFAULT_FLIGHT_SPEED * multiplier; // Apply multiplier

            try {
                target.setFlySpeed(flySpeed);
                Command.broadcastCommandMessage(sender, ChatColor.YELLOW + "Flight speed of " + target.getName() + " set been set to " + multiplier + ".");
            } catch (IllegalArgumentException ex) { // We gave an invalid value, tell the user nicely
                if (flySpeed < DEFAULT_FLIGHT_SPEED) {
                    sender.sendMessage(ChatColor.RED + "Speed multiplier too low: " + multiplier);
                } else if (flySpeed > DEFAULT_FLIGHT_SPEED) {
                    sender.sendMessage(ChatColor.RED + "Speed multiplier too high: " + multiplier);
                }
            }
        } else {
            final float walkSpeed = DEFAULT_WALK_SPEED * multiplier; // Apply multiplier

            try {
                target.setWalkSpeed(walkSpeed);
                Command.broadcastCommandMessage(sender, ChatColor.YELLOW + "Walking speed of " + target.getName() + " set been set to " + multiplier + ".");
            } catch (IllegalArgumentException ex) { // We gave an invalid value, tell the user nicely
                if (walkSpeed < DEFAULT_WALK_SPEED) {
                    sender.sendMessage(ChatColor.RED + "Speed multiplier too low: " + multiplier);
                } else if (walkSpeed > DEFAULT_WALK_SPEED) {
                    sender.sendMessage(ChatColor.RED + "Speed multiplier too high: " + multiplier);
                }
            }
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> results = new ArrayList<String>();

        if (args.length == 1) {
            results.add("fly");
            results.add("walk");
        } else if (args.length == 2) {
            results.add("reset");
        } else if (args.length == 3) {
            return null;
        }

        return getCompletions(results, args);
    }
}
