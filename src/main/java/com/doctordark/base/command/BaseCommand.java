package com.doctordark.base.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a {@link BaseCommand} for a {@link org.bukkit.plugin.Plugin}.
 */
public abstract class BaseCommand implements CommandExecutor, TabCompleter {

    private final String name;
    private final String description;
    private final String permission;

    private String[] aliases;
    private String usage;

    /**
     * Constructs a new {@link BaseCommand} with a given name, description and permission.
     *
     * @param name        the name of the command
     * @param description the description of the command
     * @param permission  the permission of the command
     */
    public BaseCommand(String name, String description, String permission) {
        this.name = name;
        this.description = description;
        this.permission = permission;
    }

    /**
     * Constructs a new {@link BaseCommand} with a given name, description and {@link Permission}.
     *
     * @param name        the name of the command
     * @param description the description of the command
     * @param permission  the @link Permission} of the command
     */
    public BaseCommand(String name, String description, Permission permission) {
        this(name, description, permission.getName());
    }

    /**
     * Gets completions based on the characters of argument.
     *
     * @param args  the array of arguments for command
     * @param input the current completion list
     * @return the completions based on given arguments
     */
    public List<String> getCompletions(String[] args, List<String> input) {
        String argument = args[(args.length - 1)];
        return input.stream().filter(string -> string.regionMatches(true, 0, argument, 0, argument.length())).collect(Collectors.toList());
    }

    /**
     * Checks if this {@link BaseCommand} can only be executed by {@link Player}s.
     *
     * @return true if is a {@link Player} only command
     */
    public boolean isPlayerOnlyCommand() {
        return false;
    }

    /**
     * Gets the name of this {@link BaseCommand}.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the description of this {@link BaseCommand}.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the permission of this {@link BaseCommand}.
     *
     * @return the permission
     */
    public String getPermission() {
        return permission;
    }

    /**
     * Gets the {@link Permission} of this {@link BaseCommand}.
     *
     * @return the {@link Permission} node
     */
    public Permission getBukkitPermission() {
        return new Permission(getPermission());
    }

    /**
     * Gets the usage for this {@link BaseCommand}.
     *
     * @return the usage
     */
    public String getUsage() {
        return usage;
    }

    /**
     * Gets the usage for this {@link BaseCommand} for a given {@link Command} label.
     *
     * @param label the label to get for
     * @return the usage for given label
     */
    public String getUsage(String label) {
        String usage = getUsage();
        return usage.replace(getName(), label);
    }

    /**
     * Sets the usage for this {@link BaseCommand}.
     *
     * @param usage the usage message to set
     */
    protected void setUsage(String usage) {
        this.usage = usage.replace("(command)", getName());
    }

    /**
     * Gets the aliases for this {@link BaseCommand}.
     *
     * @return the aliases
     */
    public String[] getAliases() {
        if (aliases == null) {
            aliases = new String[0];
        }

        return aliases;
    }

    /**
     * Sets the aliases for this {@link BaseCommand}.
     *
     * @param aliases the aliases to set
     */
    protected void setAliases(String[] aliases) {
        this.aliases = aliases;
    }

    /**
     * Validates that the player is null for the CommandSender
     * by sending a player not found messaging.
     *
     * @param sender the sender to validate for
     * @param id     the id of the player that may be null
     * @return true if player was null
     */
    public boolean validateNullPlayer(CommandSender sender, String id) {
        sender.sendMessage(ChatColor.GOLD + "Player '" + ChatColor.WHITE + id + ChatColor.GOLD + "' not found.");
        return true;
    }

    /**
     * Gets a player whose name matches the given id.
     *
     * @param sender the sender executing command
     * @param id     the id to search for
     * @return player with given name, null if is not visible to sender
     */
    public Player findPlayer(CommandSender sender, String id) {
        Player player = Bukkit.getServer().getPlayer(id);
        return (canSee(sender, player)) ? player : null;
    }

    /**
     * Checks if a {@link CommandSender} can see a {@link Player}.
     *
     * @param sender the {@link CommandSender}
     * @param target the {@link Player} to check against
     * @return true if {@link CommandSender} is not a {@link Player} or can see
     */
    public boolean canSee(CommandSender sender, Player target) {
        return target != null && (!(sender instanceof Player) || ((Player) sender).canSee(target));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return Collections.emptyList();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseCommand)) return false;

        BaseCommand that = (BaseCommand) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        if (permission != null ? !permission.equals(that.permission) : that.permission != null) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(aliases, that.aliases)) return false;
        return !(usage != null ? !usage.equals(that.usage) : that.usage != null);

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (permission != null ? permission.hashCode() : 0);
        result = 31 * result + (aliases != null ? Arrays.hashCode(aliases) : 0);
        result = 31 * result + (usage != null ? usage.hashCode() : 0);
        return result;
    }
}
