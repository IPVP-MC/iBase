package com.doctordark.util.command;

import com.doctordark.util.BukkitUtils;
import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Helper class used to build commands that have multiple
 * sub-commands referred to as {@link CommandArgument}s
 */
public abstract class ArgumentExecutor implements CommandExecutor, TabCompleter {

    protected final List<CommandArgument> arguments = new ArrayList<>();
    protected final String label;

    public ArgumentExecutor(String label) {
        this.label = label;
    }

    /**
     * Checks if this executor contains an {@link CommandArgument}.
     *
     * @param argument the argument to check for
     * @return true if this executor contains argument
     */
    public boolean containsArgument(CommandArgument argument) {
        return arguments.contains(argument);
    }

    /**
     * Adds an {@link CommandArgument} to this {@link ArgumentExecutor}.
     *
     * @param argument the {@link CommandArgument} to add
     */
    public void addArgument(CommandArgument argument) {
        arguments.add(argument);
    }

    /**
     * Removes an {@link CommandArgument} from this {@link ArgumentExecutor}.
     *
     * @param argument the {@link CommandArgument} to remove
     */
    public void removeArgument(CommandArgument argument) {
        arguments.remove(argument);
    }

    /**
     * Gets a command argument that has a given name
     * or alias included in this executor.
     *
     * @param id the name to search for
     * @return the command argument, null if absent
     */
    public CommandArgument getArgument(String id) {
        for (CommandArgument argument : arguments) {
            String name = argument.getName();
            if (name.equalsIgnoreCase(id) || Arrays.asList(argument.getAliases()).contains(id.toLowerCase())) {
                return argument;
            }
        }

        return null;
    }

    /**
     * Gets the label of the command this executor is for.
     *
     * @return the command for this executor
     */
    public String getLabel() {
        return label;
    }

    /**
     * Gets an {@link ImmutableList} of the {@link CommandArgument}s
     * used for this executor.
     *
     * @return list of {@link CommandArgument}s for this executor
     */
    public List<CommandArgument> getArguments() {
        return ImmutableList.copyOf(arguments);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(ChatColor.AQUA + "*** " + WordUtils.capitalizeFully(this.label) + " Help ***");
            for (CommandArgument argument : arguments) {
                String permission = argument.getPermission();
                if (permission == null || sender.hasPermission(permission)) {
                    sender.sendMessage(ChatColor.GRAY + argument.getUsage(label) + " - " + argument.getDescription() + '.');
                }
            }

            return true;
        }

        CommandArgument argument = getArgument(args[0]);
        String permission = (argument == null) ? null : argument.getPermission();
        if (argument == null || (permission != null && !sender.hasPermission(permission))) {
            sender.sendMessage(ChatColor.RED + WordUtils.capitalizeFully(this.label) + " sub-command " + args[0] + " not found.");
            return true;
        }

        argument.onCommand(sender, command, label, args);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> results = new ArrayList<>();
        if (args.length < 2) {
            for (CommandArgument argument : arguments) {
                String permission = argument.getPermission();
                if (permission == null || sender.hasPermission(permission)) {
                    results.add(argument.getName());
                }
            }
        } else {
            CommandArgument argument = getArgument(args[0]);
            if (argument == null) {
                return results;
            }

            String permission = argument.getPermission();
            if (permission == null || sender.hasPermission(permission)) {
                results = argument.onTabComplete(sender, command, label, args);
                if (results == null) {
                    return null;
                }
            }
        }

        return BukkitUtils.getCompletions(args, results);
    }
}
