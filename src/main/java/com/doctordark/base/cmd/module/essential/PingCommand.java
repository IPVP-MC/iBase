package com.doctordark.base.cmd.module.essential;

import com.doctordark.base.cmd.BaseCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

/**
 * Command used for healing of players.
 */
public class PingCommand extends BaseCommand {

    public PingCommand() {
        super("ping", "Checks the ping of a player.", "base.command.ping");
        this.setAliases(new String[]{});
        this.setUsage("/(command) <playerName>");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player target;
        if (args.length < 1) {
            if (sender instanceof Player) {
                target = (Player)sender;
            } else {
                sender.sendMessage(ChatColor.RED + "Usage: " + getUsage(label));
                return true;
            }
        } else if (sender.hasPermission(getPermission() + ".others")) {
            target = Bukkit.getServer().getPlayer(args[0]);
        } else {
            target = (Player)sender;
        }

        if ((target == null) || (!canSee(sender, target))) {
            sender.sendMessage(ChatColor.GOLD + "Player '" + ChatColor.WHITE + args[0] + ChatColor.GOLD + "' not found.");
            return true;
        }

        int ping = ((CraftPlayer) target).getHandle().ping;

        sender.sendMessage(ChatColor.GOLD + "Ping of " + target.getName() + ChatColor.YELLOW + ": " + ChatColor.BLUE + ping);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        String permission = getPermission() + ".others";
        return (args.length == 1 && sender.hasPermission(permission)) ? null : Collections.<String>emptyList();
    }
}