package com.doctordark.base.cmd.module.essential;

import com.doctordark.base.cmd.BaseCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Command used for healing of players.
 */
public class StatisticCommand extends BaseCommand {

    public StatisticCommand() {
        super("statistic", "Management of bukkit statistics.", "base.command.statistic");
        this.setAliases(new String[]{});
        this.setUsage("/(command) <playerName> <staticName> <get|set|add|subtract> [amount]");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player target;
        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Usage: " + getUsage(label));
            return true;
        } else {
            target = Bukkit.getServer().getPlayer(args[0]);
        }

        if ((target == null) || (!canSee(sender, target))) {
            sender.sendMessage(ChatColor.GOLD + "Player '" + ChatColor.WHITE + args[0] + ChatColor.GOLD + "' not found.");
            return true;
        }

        Statistic statistic;
        try {
            statistic = Statistic.valueOf(args[1]);
        } catch (IllegalArgumentException ex) {
            sender.sendMessage(ChatColor.RED + "Statistic '" + args[1] + "' not found.");
            return true;
        }

        String statisticName = statistic.name();

        if (args[2].equalsIgnoreCase("get")) {
            int level = target.getStatistic(statistic);

            sender.sendMessage(ChatColor.YELLOW + target.getName() + " has " + level + " for " + statisticName + ".");
            return true;
        }

        if (args[2].equalsIgnoreCase("set")) {
            if (args.length < 4) {
                sender.sendMessage(ChatColor.RED + "Usage: " + getUsage(label));
                return true;
            }

            Integer newLevel;

            try {
                newLevel = Integer.parseInt(args[3]);
            } catch (IllegalArgumentException ex) {
                sender.sendMessage(ChatColor.RED + "'" + args[3] +"' is not a number.");
                return true;
            }

            target.setStatistic(statistic, newLevel);

            sender.sendMessage(ChatColor.YELLOW + "Set statistic " + statisticName + " to " + newLevel + " for " + target.getName() + ".");
            return true;
        }

        sender.sendMessage(ChatColor.RED + "Usage: " + getUsage(label));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            return null;
        }

        List<String> results = new ArrayList<String>();

        if (args.length == 2) {
            for (Statistic statistic : Statistic.values()) {
                results.add(statistic.name());
            }
        } else if (args.length == 3) {
            results.add("get");
            results.add("set");
            results.add("add");
            results.add("subtract");
        }

        return getCompletions(args, results);
    }
}