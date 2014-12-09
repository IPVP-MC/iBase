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

    public TeleportCommand() {
        super("teleport", "Teleport to a player or position.", "base.command.teleport");
        this.setAliases(new String[]{});
        this.setUsage("/(command) (<playerName> [otherPlayerName]) | (x y z)");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Usage: " + getUsage());
            return true;
        }

        Player teleportee = (args.length > 1) ? Bukkit.getPlayer(args[0]) : sender instanceof Player ? ((Player) sender) : null;
        Player teleportTo = (args.length > 2) ? Bukkit.getPlayer(args[1]) : Bukkit.getPlayer(args[0]);

        if (teleportee == null || teleportTo == null) {
            sender.sendMessage(ChatColor.RED + "One of those players were not found!");
            return true;
        }

        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (!player.canSee(teleportee) || !player.canSee(teleportTo)) {
                sender.sendMessage(ChatColor.GOLD + "Player '" + ChatColor.WHITE + args[0] + ChatColor.GOLD + "' not found!");
                return true;
            }
        }

        teleportee.teleport(teleportTo, PlayerTeleportEvent.TeleportCause.COMMAND);

        Command.broadcastCommandMessage(sender, ChatColor.YELLOW + "Teleporting " + teleportee.getName() + " to " + teleportTo.getName() + ".");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        return (args.length == 1 || args.length == 2) ? null : Collections.<String>emptyList();
    }
}
