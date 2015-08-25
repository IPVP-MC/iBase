package com.doctordark.base.command.module.teleport;

import com.doctordark.base.command.BaseCommand;
import com.doctordark.util.BukkitUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.Objects;

public class TopCommand extends BaseCommand {

    public TopCommand() {
        super("top", "Teleports to the highest safe spot.");
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
        Location origin = player.getLocation().clone();
        Block originBlock;

        Location highestLocation = BukkitUtils.getHighestLocation(origin.clone());
        if (highestLocation == null || Objects.equals(highestLocation, origin) ||
                (highestLocation.getBlockY() - (originBlock = origin.getBlock()).getY() == 1 &&
                        originBlock.getType() == Material.WATER || originBlock.getType() == Material.STATIONARY_WATER)) {

            sender.sendMessage(ChatColor.RED + "No highest location found.");
            return true;
        }

        player.teleport(highestLocation.add(0, 1, 0), PlayerTeleportEvent.TeleportCause.COMMAND);
        sender.sendMessage(ChatColor.GOLD + "Teleported to highest location.");
        return true;
    }
}
