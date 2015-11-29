package org.ipvp.util.command;

import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.ipvp.util.BukkitUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CommandWrapper implements CommandExecutor, TabCompleter {

    private final Collection<CommandArgument> arguments;

    public CommandWrapper(Collection<CommandArgument> arguments) {
        this.arguments = arguments;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            printUsage(sender, label, arguments);
            return true;
        }

        CommandArgument argument = matchArgument(args[0], sender, arguments);

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
            results = getAccessibleArgumentNames(sender, arguments);
        } else {
            CommandArgument argument = matchArgument(args[0], sender, arguments);
            if (argument == null) return Collections.emptyList();

            // If a plugin sets the results to null, let Bukkit handle player name completion
            results = argument.onTabComplete(sender, command, label, args);
            if (results == null) return null;
        }

        return BukkitUtils.getCompletions(args, results);
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

    public static CommandArgument matchArgument(String id, CommandSender sender, Collection<CommandArgument> arguments) {
        for (CommandArgument argument : arguments) {
            String permission = argument.getPermission();
            if (permission == null || sender.hasPermission(permission)) {
                if (argument.getName().equalsIgnoreCase(id) || Arrays.asList(argument.getAliases()).contains(id)) {
                    return argument;
                }
            }
        }

        return null;
    }

    public static List<String> getAccessibleArgumentNames(CommandSender sender, Collection<CommandArgument> arguments) {
        List<String> results = new ArrayList<>();
        for (CommandArgument argument : arguments) {
            String permission = argument.getPermission();
            if (permission == null || sender.hasPermission(permission)) {
                results.add(argument.getName());
            }
        }

        return results;
    }

    public static class ArgumentComparator implements Comparator<CommandArgument>, Serializable {

        @Override
        public int compare(CommandArgument primaryArgument, CommandArgument secondaryArgument) {
            return secondaryArgument.getName().compareTo(primaryArgument.getName());
        }
    }
}
