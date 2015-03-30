package com.doctordark.base.command.module.essential;

import com.doctordark.base.command.BaseCommand;
import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class SudoCommand extends BaseCommand {

    public SudoCommand() {
        super("sudo", "Forces a player to run command.", "base.command.sudo");
        setAliases(new String[0]);
        setUsage("/(command) <force> <all|playerName> <command args...> \n[Warning!] Forcing will give player temporary OP until executed.");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Usage: " + getUsage());
            return true;
        }

        boolean force;
        try {
            force = Boolean.parseBoolean(args[0]);
        } catch (IllegalArgumentException ex) {
            sender.sendMessage(ChatColor.RED + "Usage: " + getUsage());
            return true;
        }

        StringBuilder builder = new StringBuilder();
        for (int i = 2; i < args.length; i++) {
            builder.append(args[i]).append(" ");
        }

        String executingCommand = builder.toString();
        if (args[1].equalsIgnoreCase("all")) {
            for (Player target : Bukkit.getServer().getOnlinePlayers()) {
                executeCommand(target, executingCommand, force);
            }

            sender.sendMessage(ChatColor.RED + "Forcing all players to run " + executingCommand + (force ? "with permission bypasses" : "") + ".");
            return true;
        }

        Player target = Bukkit.getServer().getPlayer(args[1]);

        if ((target == null) || (!canSee(sender, target))) {
            sender.sendMessage(ChatColor.GOLD + "Player '" + ChatColor.WHITE + args[1] + ChatColor.GOLD + "' not found.");
            return true;
        }

        executeCommand(target, executingCommand, force);
        sender.sendMessage(ChatColor.RED + "Making " + target.getName() + " to run " + executingCommand + (force ? "with permission bypasses" : "") + ".");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 2) {
            return Collections.emptyList();
        }

        List<String> results = Lists.newArrayList();
        if (args.length == 0) {
            results.add("true");
            results.add("false");
        } else if (args.length == 1) {
            results.add("ALL");
            for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                if ((!(sender instanceof Player)) || ((Player) sender).canSee(player)) {
                }
            }
        }

        return getCompletions(args, results);
    }

    /**
     * Makes a player execute a command, optionally bypassing permissions.
     *
     * @param target           the player to force
     * @param executingCommand the command to execute
     * @param force            if the player is bypassing permissions
     * @return true if the command could be executed
     */
    private boolean executeCommand(Player target, String executingCommand, boolean force) {
        if (target.isOp()) {
            force = false;
        }

        try {
            if (force) {
                target.setOp(true);
            }

            target.performCommand(executingCommand);
            return true;
        } catch (Exception ex) {
            return false;
        } finally {
            if (force) {
                target.setOp(false);
            }
        }
    }
}
