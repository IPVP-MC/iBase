package com.doctordark.base.command.module.teleport;

import com.doctordark.base.command.BaseCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.Collections;
import java.util.List;

/**
 * Command used to teleport all players to positions.
 */
public class TeleportAllCommand extends BaseCommand {

    public TeleportAllCommand() {
        super("teleportall", "Teleport all players to yourself.", "base.command.teleportall");
        this.setAliases(new String[]{"tpall"});
        this.setUsage("/(command)");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only exeuctable by players.");
            return true;
        }

        Player player = (Player) sender;
        for (Player target : Bukkit.getServer().getOnlinePlayers()) {
            target.teleport(player, PlayerTeleportEvent.TeleportCause.COMMAND);
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return (args.length == 1) ? null : Collections.<String>emptyList();
    }
}
