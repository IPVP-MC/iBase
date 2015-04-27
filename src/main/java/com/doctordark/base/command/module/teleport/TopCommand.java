package com.doctordark.base.command.module.teleport;

import com.doctordark.base.command.BaseCommand;
import com.doctordark.util.BukkitUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

public class TopCommand extends BaseCommand {

    public TopCommand() {
        super("top", "Teleports to the highest safe spot.", "base.command.top");
        setAliases(new String[0]);
        setUsage("/(command)");
    }

    @Override
    public boolean isPlayerOnlyCommand() {
        return true;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only executable for players.");
            return true;
        }

        Player player = (Player) sender;
        Location location = player.getLocation();
        Location highestLocation = BukkitUtils.getHighestLocation(location);

        if (highestLocation.equals(location)) {
            sender.sendMessage(ChatColor.RED + "No highest location found.");
            return true;
        }

        player.teleport(highestLocation.add(0, 2, 0), PlayerTeleportEvent.TeleportCause.COMMAND);
        sender.sendMessage(ChatColor.GOLD + "Teleported to highest location.");
        return true;
    }
}
