package com.doctordark.base.command.module.essential;

import com.doctordark.base.command.BaseCommand;
import com.doctordark.base.util.BaseUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class NearCommand extends BaseCommand {

    public NearCommand() {
        super("near", "Count entities near a player.", "base.command.near");
        setAliases(new String[0]);
        setUsage("/(command) <playerName> <radius>");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: " + getUsage(label));
            return true;
        }

        Player target = Bukkit.getServer().getPlayer(args[0]);

        if ((target == null) || (!canSee(sender, target))) {
            sender.sendMessage(ChatColor.GOLD + "Player '" + ChatColor.WHITE + args[0] + ChatColor.GOLD + "' not found.");
            return true;
        }

        Integer radius = BaseUtil.getInteger(args[1]);

        if (radius == null) {
            sender.sendMessage(ChatColor.RED + "'" + args[1] + "' is not a number.");
            return true;
        }

        int entityCount = target.getNearbyEntities(radius, Math.max(radius.intValue(), 256), radius).size();

        sender.sendMessage(ChatColor.YELLOW.toString() + entityCount + " entities are near " + target.getName() + " with a radius of " + radius + ".");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return args.length == 1 ? null : Collections.emptyList();
    }
}
