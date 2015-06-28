package com.doctordark.util.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Represents an argument used for a Bukkit command.
 */
public abstract class CommandArgument {

    private final String name;
    private final String description;
    protected String[] aliases;

    /**
     * Creates a new command argument with a name and description.
     *
     * @param name        the name of argument
     * @param description the description of the argument
     */
    public CommandArgument(String name, String description) {
        this(name, description, new String[]{});
    }

    /**
     * Creates a new command argument with a name and description.
     *
     * @param name        the name of argument
     * @param description the description of the argument
     * @param aliases     the aliases of this argument
     */
    public CommandArgument(String name, String description, String[] aliases) {
        this.name = name;
        this.description = description;
        this.aliases = aliases;
    }

    /**
     * Gets the name for this command argument.
     *
     * @return the name of this CommandArgument.
     */
    public final String getName() {
        return name;
    }

    /**
     * Checks if this {@link CommandArgument} can only be
     * executed by {@link org.bukkit.entity.Player}s.
     *
     * @return true if execution only by players
     */
    public boolean isPlayerOnly() {
        return false;
    }

    /**
     * Gets the description for this {@link CommandArgument}.
     *
     * @return the description of this argument
     */
    public final String getDescription() {
        return description;
    }

    /**
     * Gets the permission for this {@link CommandArgument}.
     *
     * @return the permission node, null if not permissible
     */
    public String getPermission() {
        return null;
    }

    /**
     * Gets the optional {@link Permission} for this {@link CommandArgument}
     *
     * @return the optional {@link Permission} of this argument
     */
    public Optional<Permission> getBukkitPermission() {
        return Optional.ofNullable(new Permission(getPermission()));
    }

    /**
     * Gets the aliases for this {@link CommandArgument}.
     *
     * @return array of argument aliases
     */
    public String[] getAliases() {
        return aliases;
    }

    /**
     * Gets the usage for this {@link CommandArgument}.
     *
     * @param label the label of command
     * @return usage with the label
     */
    public abstract String getUsage(String label);

    /**
     * Handles the command execution for this {@link CommandArgument}.
     *
     * @param sender  the executor of command
     * @param command the command being executed
     * @param label   the label of executed command
     * @param args    the arguments for executed command
     * @return true if command was successfully completed
     */
    public abstract boolean onCommand(CommandSender sender, Command command, String label, String[] args);

    /**
     * Handles the command tab completion for this {@link CommandArgument}.
     *
     * @param sender  the executor of command
     * @param command the command being executed
     * @param label   the label of executed command
     * @param args    the arguments for executed command
     * @return true if command was successfully completed
     */
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return Collections.emptyList();
    }
}
