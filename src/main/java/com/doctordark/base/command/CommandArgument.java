package com.doctordark.base.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Represents an argument for a {@link org.bukkit.command.CommandExecutor}.
 */
public abstract class CommandArgument {

    private final String name;
    private final String description;
    private String[] aliases;

    /**
     * Constructs a new {@link CommandArgument} with a given name,
     * description and set of aliases.
     *
     * @param name        the name to construct with
     * @param description the description to construct with
     */
    public CommandArgument(String name, String description) {
        this(name, description, new String[0]);
    }

    /**
     * Constructs a new {@link CommandArgument} with a given name,
     * description and set of aliases.
     *
     * @param name        the name to construct with
     * @param description the description to construct with
     * @param aliases     array of aliases to construct with
     */
    public CommandArgument(String name, String description, String[] aliases) {
        this.name = name;
        this.description = description;
        this.aliases = aliases;
    }

    /**
     * Gets the name of this {@link CommandArgument}.
     *
     * @return the name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Gets the description of this {@link CommandArgument}.
     *
     * @return the description
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Gets the permission of this {@link CommandArgument}.
     *
     * @return the permission
     */
    public String getPermission() {
        return null;
    }

    /**
     * Gets the aliases used for this {@link CommandArgument}.
     *
     * @return array of aliases
     */
    public String[] getAliases() {
        if (aliases == null) {
            aliases = new String[0];
        }

        return aliases;
    }

    /**
     * Gets the usage for this {@link CommandArgument}.
     *
     * @param label the label to check for
     * @return the usage message
     */
    public abstract String getUsage(String label);

    /**
     * @see org.bukkit.command.CommandExecutor#onCommand(CommandSender, Command, String, String[])
     */
    public abstract boolean onCommand(CommandSender sender, Command command, String label, String[] args);

    /**
     * @see org.bukkit.command.TabCompleter#onTabComplete(CommandSender, Command, String, String[])
     */
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return Collections.emptyList();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CommandArgument)) return false;

        CommandArgument that = (CommandArgument) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(aliases, that.aliases);

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (aliases != null ? Arrays.hashCode(aliases) : 0);
        return result;
    }
}
