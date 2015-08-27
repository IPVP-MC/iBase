package com.doctordark.base.command.module.essential;

import com.doctordark.base.command.BaseCommand;
import com.doctordark.util.BukkitUtils;
import com.google.common.collect.ImmutableList;
import com.google.common.primitives.Floats;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class SpeedCommand extends BaseCommand {

    private static final float DEFAULT_FLIGHT_SPEED = 1.0F;
    private static final float DEFAULT_WALK_SPEED = 2.0F;

    public SpeedCommand() {
        super("speed", "Sets the fly/walk speed of a player.");
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
            target = BukkitUtils.playerWithNameOrUUID(args[2]);
        } else if (sender instanceof Player) {
            target = (Player) sender;
        } else {
            sender.sendMessage(ChatColor.RED + "Usage: " + getUsage(label));
            return true;
        }

        if (target == null || !canSee(sender, target)) {
            sender.sendMessage(ChatColor.GOLD + "Player named or with UUID '" + ChatColor.WHITE + args[2] + ChatColor.GOLD + "' not found.");
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
                Command.broadcastCommandMessage(sender, ChatColor.YELLOW + "Flight speed of " + target.getName() + " has been set to " + multiplier + '.');
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
                Command.broadcastCommandMessage(sender, ChatColor.YELLOW + "Walking speed of " + target.getName() + " has been set to " + multiplier + '.');
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
        switch (args.length) {
            case 1:
                return BukkitUtils.getCompletions(args, COMPLETIONS_FIRST);
            case 2:
                return BukkitUtils.getCompletions(args, COMPLETIONS_SECOND);
            case 3:
                return null;
            default:
                return Collections.emptyList();
        }
    }

    private static final ImmutableList<String> COMPLETIONS_FIRST = ImmutableList.of("fly", "walk");
    private static final ImmutableList<String> COMPLETIONS_SECOND = ImmutableList.of("reset");
}
