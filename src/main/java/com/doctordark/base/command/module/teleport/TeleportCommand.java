package com.doctordark.base.command.module.teleport;

import com.doctordark.base.BaseConstants;
import com.doctordark.base.command.BaseCommand;
import com.doctordark.util.BukkitUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.VanillaCommand;
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
        super("teleport", "Teleport to a player or position.");
        this.setUsage("/(command) (<playerName> [otherPlayerName]) | (x y z)");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1 || args.length > 4) {
            sender.sendMessage(ChatColor.RED + "Usage: " + getUsage(label));
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
            targetA = BukkitUtils.playerWithNameOrUUID(args[0]);
        }

        if (targetA == null || !canSee(sender, targetA)) {
            sender.sendMessage(String.format(BaseConstants.PLAYER_WITH_NAME_OR_UUID_NOT_FOUND, args[0]));
            return true;
        }

        if (args.length < 3) {
            Player targetB = BukkitUtils.playerWithNameOrUUID(args[args.length - 1]);

            if (targetB == null || !canSee(sender, targetB)) {
                sender.sendMessage(ChatColor.GOLD + "Player named or with UUID '" + ChatColor.WHITE + args[args.length - 1] + ChatColor.GOLD + "' not found.");
                return true;
            }

            if (targetA.equals(targetB)) {
                sender.sendMessage(ChatColor.RED + "The teleportee and teleported are the same player.");
                return true;
            }

            if (targetA.teleport(targetB, PlayerTeleportEvent.TeleportCause.COMMAND)) {
                Command.broadcastCommandMessage(sender, ChatColor.YELLOW + "Teleported " + targetA.getName() + " to " + targetB.getName() + '.');
            } else {
                sender.sendMessage(ChatColor.RED + "Failed to teleport you to " + targetB.getName() + '.');
            }
        } else if (targetA.getWorld() != null) {
            Location targetALocation = targetA.getLocation();
            double x = getCoordinate(sender, targetALocation.getX(), args[args.length - 3]);
            double y = getCoordinate(sender, targetALocation.getY(), args[args.length - 2], 0, 0);
            double z = getCoordinate(sender, targetALocation.getZ(), args[args.length - 1]);

            if (x == MIN_COORD_MINUS_ONE || y == MIN_COORD_MINUS_ONE || z == MIN_COORD_MINUS_ONE) {
                sender.sendMessage("Please provide a valid location.");
                return true;
            }

            targetALocation.setX(x);
            targetALocation.setY(y);
            targetALocation.setZ(z);

            if (targetA.teleport(targetALocation, PlayerTeleportEvent.TeleportCause.COMMAND)) {
                Command.broadcastCommandMessage(sender, String.format(ChatColor.YELLOW + "Teleported %s to %.2f, %.2f, %.2f.", targetA.getName(), x, y, z));
            } else {
                sender.sendMessage(ChatColor.RED + "Failed to teleport you.");
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

            double testResult = VanillaCommand.getDouble(sender, input);
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

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return (args.length == 1 || args.length == 2) ? null : Collections.<String>emptyList();
    }
}
