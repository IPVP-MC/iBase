package com.doctordark.base.cmd.module.teleport;

import com.doctordark.base.cmd.BaseCommand;
import com.doctordark.base.util.BaseUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

/**
 * Command used to teleport to highest available point.
 */
public class TopCommand extends BaseCommand {

    public TopCommand() {
        super("top", "Teleports to the highest safe spot.", "base.command.top");
        this.setAliases(new String[]{});
        this.setUsage("/(command)");
    }

    @Override
    public boolean isPlayerOnlyCommand() {
        return true;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only executable for players!");
            return true;
        }

        Player player = (Player) sender;
        Location location = player.getLocation();

        Location highestLocation = BaseUtil.getHighestBlock(location);

        if (highestLocation == null) {
            sender.sendMessage(ChatColor.RED + "No highest location found!");
            return true;
        }

        player.teleport(highestLocation, PlayerTeleportEvent.TeleportCause.COMMAND);
        sender.sendMessage(ChatColor.GOLD + "Teleported to highest location!");
        return true;
    }
}
