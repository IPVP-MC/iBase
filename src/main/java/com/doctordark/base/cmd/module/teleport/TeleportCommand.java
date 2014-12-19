package com.doctordark.base.cmd.module.teleport;

import com.doctordark.base.cmd.BaseCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.Collections;
import java.util.List;

/**
 * Command used to teleport to positions.
 */
public class TeleportCommand extends BaseCommand {

    static final int MAX_COORD = 30000000;
    static final int MIN_COORD_MINUS_ONE = -30000001;
    static final int MIN_COORD = -30000000;

    public TeleportCommand() {
        super("teleport", "Teleport to a player or position.", "base.command.teleport");
        this.setAliases(new String[]{});
        this.setUsage("/(command) (<playerName> [otherPlayerName]) | (x y z)");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length < 1 || args.length > 4) {
            sender.sendMessage(ChatColor.RED + "Usage: " + getUsage());
            return true;
        }

        Player targetA;

        if (args.length == 1 || args.length == 3) {
            if (sender instanceof Player) {
                targetA = (Player) sender;
            } else {
                sender.sendMessage("Usage: " + getUsage());
                return true;
            }
        } else {
            targetA = Bukkit.getServer().getPlayer(args[0]);
        }

        if ((targetA == null) || (!canSee(sender, targetA))) {
            sender.sendMessage(ChatColor.GOLD + "Player '" + ChatColor.WHITE + args[0] + ChatColor.GOLD + "' not found!");
            return true;
        }

        if (args.length < 3) {
            Player targetB = Bukkit.getServer().getPlayer(args[args.length - 1]);

            if ((targetB == null) || (!canSee(sender, targetB))) {
                sender.sendMessage(ChatColor.GOLD + "Player '" + ChatColor.WHITE + args[args.length - 1] + ChatColor.GOLD + "' not found!");
                return true;
            }

            if (targetA.getName().equalsIgnoreCase(targetB.getName())) {
                sender.sendMessage(ChatColor.RED + "The teleportee and teleported are the same player!");
                return true;
            }

            if (targetA.teleport(targetB, PlayerTeleportEvent.TeleportCause.COMMAND)) {
                Command.broadcastCommandMessage(sender, ChatColor.YELLOW + "Teleported " + targetA.getName() + " to " + targetB.getName());
            } else {
                sender.sendMessage(ChatColor.RED + "Failed to teleport you to " + targetB.getName() + "!");
            }
        } else if (targetA.getWorld() != null) {
            Location targetALocation = targetA.getLocation();
            double x = getCoordinate(sender, targetALocation.getX(), args[args.length - 3]);
            double y = getCoordinate(sender, targetALocation.getY(), args[args.length - 2], 0, 0);
            double z = getCoordinate(sender, targetALocation.getZ(), args[args.length - 1]);

            if (x == MIN_COORD_MINUS_ONE || y == MIN_COORD_MINUS_ONE || z == MIN_COORD_MINUS_ONE) {
                sender.sendMessage("Please provide a valid location!");
                return true;
            }

            targetALocation.setX(x);
            targetALocation.setY(y);
            targetALocation.setZ(z);

            if (targetA.teleport(targetALocation, PlayerTeleportEvent.TeleportCause.COMMAND)) {
                Command.broadcastCommandMessage(sender, String.format(ChatColor.YELLOW + "Teleported %s to %.2f, %.2f, %.2f.", targetA.getName(), x, y, z));
            } else {
                sender.sendMessage(ChatColor.RED + "Failed to teleport you!");
            }
        }

        return true;
    }

    private double getCoordinate(CommandSender sender, double current, String input) {
        return getCoordinate(sender, current, input, MIN_COORD, MAX_COORD);
    }

    private double getCoordinate(CommandSender sender, double current, String input, int min, int max) {
        boolean relative = input.startsWith("~");
        double result = relative ? current : 0;

        if (!relative || input.length() > 1) {
            boolean exact = input.contains(".");
            if (relative) input = input.substring(1);

            double testResult = getDouble(sender, input);
            if (testResult == MIN_COORD_MINUS_ONE) {
                return MIN_COORD_MINUS_ONE;
            }
            result += testResult;

            if (!exact && !relative) result += 0.5f;
        }
        if (min != 0 || max != 0) {
            if (result < min) {
                result = MIN_COORD_MINUS_ONE;
            }

            if (result > max) {
                result = MIN_COORD_MINUS_ONE;
            }
        }

        return result;
    }

    public static double getDouble(CommandSender sender, String input) {
        try {
            return Double.parseDouble(input);
        } catch (NumberFormatException ex) {
            return MIN_COORD_MINUS_ONE;
        }
    }

    public static double getDouble(CommandSender sender, String input, double min, double max) {
        double result = getDouble(sender, input);

        // TODO: This should throw an exception instead.
        if (result < min) {
            result = min;
        } else if (result > max) {
            result = max;
        }

        return result;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        return (args.length == 1 || args.length == 2) ? null : Collections.<String>emptyList();
    }
}
