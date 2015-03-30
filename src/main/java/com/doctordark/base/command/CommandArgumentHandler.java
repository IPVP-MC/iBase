package com.doctordark.base.command;

import com.google.common.collect.Lists;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class CommandArgumentHandler implements CommandExecutor, TabCompleter {

    private final Collection<CommandArgument> arguments;

    public CommandArgumentHandler(Collection<CommandArgument> arguments) {
        this.arguments = arguments;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only executable for players.");
            return true;
        }

        if (args.length < 1) {
            printUsage(sender, label, arguments);
            return true;
        }

        CommandArgument argument = getArgument(args[0], sender, arguments);

        if (argument == null) {
            printUsage(sender, label, arguments);
            return true;
        }

        return argument.onCommand(sender, command, label, args);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return Collections.emptyList();
        }

        List<String> results;
        if (args.length == 1) {
            results = getArgumentList(sender, arguments);
        } else {
            CommandArgument argument = getArgument(args[0], sender, arguments);
            if (argument == null) {
                return Collections.emptyList();
            } else {
                results = argument.onTabComplete(sender, command, label, args);
            }
        }

        if (results == null) {
            return null;
        }

        return getCompletions(args, results);
    }

    public static void printUsage(CommandSender sender, String label, Collection<CommandArgument> arguments) {
        sender.sendMessage(ChatColor.DARK_AQUA + "*** " + WordUtils.capitalizeFully(label) + " Help ***");
        for (CommandArgument argument : arguments) {
            String permission = argument.getPermission();
            if ((permission == null) || (sender.hasPermission(permission))) {
                sender.sendMessage(ChatColor.GRAY + argument.getUsage(label) + " - " + argument.getDescription());
            }
        }
    }

    public static CommandArgument getArgument(String id, CommandSender sender, Collection<CommandArgument> arguments) {
        for (CommandArgument argument : arguments) {
            String permission = argument.getPermission();
            if ((permission == null) || (sender.hasPermission(permission))) {
                if (argument.getName().equalsIgnoreCase(id)) {
                    return argument;
                }

                List<String> aliases = Arrays.asList(argument.getAliases());
                if (aliases.contains(id)) {
                    return argument;
                }
            }
        }

        return null;
    }

    public static List<String> getArgumentList(CommandSender sender, Collection<CommandArgument> arguments) {
        List<String> results = Lists.newArrayList();
        for (CommandArgument argument : arguments) {
            String permission = argument.getPermission();
            if ((permission == null) || (sender.hasPermission(permission))) {
                results.add(argument.getName());
            }
        }

        return results;
    }

    public static List<String> getCompletions(String[] args, List<String> input) {
        List<String> results = Lists.newArrayList();
        String argument = args[(args.length - 1)];
        for (String string : input) {
            if (string.regionMatches(true, 0, argument, 0, argument.length())) {
                results.add(string);
            }
        }

        return results;
    }
}
