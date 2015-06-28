package com.doctordark.base.command.module.essential;

import com.doctordark.base.command.BaseCommand;
import com.google.common.collect.Lists;
import com.google.common.primitives.Floats;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class SpeedCommand extends BaseCommand {

    private static final float DEFAULT_FLIGHT_SPEED = 0.1F;
    private static final float DEFAULT_WALK_SPEED = 0.2F;

    public SpeedCommand() {
        super("speed", "Sets the fly/walk speed of a player.", "base.command.speed");
        setAliases(new String[0]);
        setUsage("/(command) <fly|walk> <speedMultiplier|reset> [playerName]");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: " + getUsage(label));
            return true;
        }

        final Player target;
        if (args.length > 2 && sender.hasPermission(command.getPermission() + ".others")) {
            target = Bukkit.getServer().getPlayer(args[2]);
        } else if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Usage: " + getUsage(label));
            return true;
        } else {
            target = (Player) sender;
        }

        if (target == null || !canSee(sender, target)) {
            sender.sendMessage(ChatColor.GOLD + "Player '" + ChatColor.WHITE + args[2] + ChatColor.GOLD + "' not found.");
            return true;
        }

        Boolean flight;
        if (args[0].equalsIgnoreCase("fly")) {
            flight = true;
        } else if (args[0].equalsIgnoreCase("walk")) {
            flight = false;
        } else {
            sender.sendMessage(ChatColor.RED + "Usage: " + getUsage(label));
            return true;
        }

        Float multiplier;
        if (args[1].equalsIgnoreCase("reset")) {
            multiplier = flight ? DEFAULT_FLIGHT_SPEED : DEFAULT_WALK_SPEED;
        } else {
            multiplier = Floats.tryParse(args[1]);
            if (multiplier == null) {
                sender.sendMessage(ChatColor.RED + "Invalid speed multiplier: '" + args[1] + "'.");
                return true;
            }
        }

        if (flight) {
            float flySpeed = 0.1F * multiplier;

            try {
                target.setFlySpeed(flySpeed);
                Command.broadcastCommandMessage(sender, ChatColor.YELLOW + "Flight speed of " + target.getName() + " has been set to " + multiplier + ".");
            } catch (IllegalArgumentException ex) {
                if (flySpeed < 0.1F) {
                    sender.sendMessage(ChatColor.RED + "Speed multiplier too low: " + multiplier);
                } else if (flySpeed > 0.1F) {
                    sender.sendMessage(ChatColor.RED + "Speed multiplier too high: " + multiplier);
                }
            }
        } else {
            float walkSpeed = 0.2F * multiplier;

            try {
                target.setWalkSpeed(walkSpeed);
                Command.broadcastCommandMessage(sender, ChatColor.YELLOW + "Walking speed of " + target.getName() + " has been set to " + multiplier + ".");
            } catch (IllegalArgumentException ex) {
                if (walkSpeed < 0.2F) {
                    sender.sendMessage(ChatColor.RED + "Speed multiplier too low: " + multiplier);
                } else if (walkSpeed > 0.2F) {
                    sender.sendMessage(ChatColor.RED + "Speed multiplier too high: " + multiplier);
                }
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> results = Lists.newArrayList();
        if (args.length == 1) {
            results.add("fly");
            results.add("walk");
        } else if (args.length == 2) {
            results.add("reset");
        } else if (args.length == 3) {
            return null;
        }

        return getCompletions(args, results);
    }
}
