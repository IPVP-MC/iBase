package com.doctordark.base.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a base command for the plugin.
 */
public abstract class BaseCommand implements CommandExecutor, TabCompleter {

    private final String name;
    private final String description;
    private final String permission;
    private String[] aliases;
    private String usage;

    /**
     * Constructs a new base command with a given name, description and permission.
     *
     * @param name the name of the command
     * @param description the description of the command
     * @param permission the permission of the command
     */
    public BaseCommand(String name, String description, String permission) {
        this.name = name;
        this.description = description;
        this.permission = permission;
    }

    /**
     * Gets completions based on the characters of argument.
     *
     * @param args the array of arguments for command
     * @param input the current completion list
     * @return the completions based on given arguments
     */
    public List<String> getCompletions(String[] args, List<String> input) {
        List<String> results = new ArrayList<String>();
        String argument = args[(args.length - 1)];

        for (String string : input) {
            if (string.regionMatches(true, 0, argument, 0, argument.length())) {
                results.add(string);
            }
        }

        return results;
    }

    /**
     * Checks if this command can only be executed by players.
     *
     * @return true if it is a player only command
     */
    public boolean isPlayerOnlyCommand() {
        return false;
    }

    /**
     * Gets the name of this base command.
     *
     * @return the name of command
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the description of this base command.
     *
     * @return the description of command
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the permission of this base command.
     *
     * @return the permission of command
     */
    public String getPermission() {
        return permission;
    }

    /**
     * Gets the usage for this base command.
     *
     * @return the usage of command
     */
    public String getUsage() {
        return usage;
    }

    /**
     * Gets the usage for this base command for a
     * given command label.
     *
     * @param label the label of command
     * @return the usage of command
     */
    public String getUsage(String label) {
        String usage = getUsage();
        return usage.replace(getName(), label);
    }

    /**
     * Sets the usage for this base command.
     *
     * @param usage the usage messaging to set
     */
    protected void setUsage(String usage) {
        this.usage = usage.replace("(command)", getName());
    }

    /**
     * Gets the aliases for this base command.
     *
     * @return the aliases for this command
     */
    public String[] getAliases() {
        if (aliases == null) {
            aliases = new String[0];
        }

        return aliases;
    }

    /**
     * Sets the aliases for this base command.
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
     * @param id the id of the player that may be null
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
     * @param id the id to search for
     * @return player with given name, null if is not visible to sender
     */
    public Player findPlayer(CommandSender sender, String id) {
        Player player = Bukkit.getServer().getPlayer(id);
        return (canSee(sender, player)) ? player : null;
    }

    /**
     * Checks if a CommandSender can see an online player.
     *
     * @param sender the sender of command
     * @param target the target to check for
     * @return true if sender is not player or the sending player can see target
     */
    public boolean canSee(CommandSender sender, Player target) {
        return target == null || (!(sender instanceof Player && !((Player) sender).canSee(target)));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return Collections.emptyList();
    }
}
