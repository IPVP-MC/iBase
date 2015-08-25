package com.doctordark.base.command;

import com.doctordark.util.BukkitUtils;
import com.doctordark.util.command.ArgumentExecutor;
import org.apache.commons.lang3.ArrayUtils;
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
import java.util.regex.Pattern;

/**
 * Represents a {@link BaseCommand} for a {@link org.bukkit.plugin.Plugin}.
 */
public abstract class BaseCommand extends ArgumentExecutor {

    private static final Pattern USAGE_REPLACER_PATTERN = Pattern.compile("(command)", Pattern.LITERAL);

    private final String name;
    private final String description;

    private String[] aliases;
    private String usage;

    /**
     * Constructs a new {@link BaseCommand} with a given name, description and permission.
     *
     * @param name        the name of the command
     * @param description the description of the command
     */
    public BaseCommand(String name, String description) {
        super(name);
        this.name = name;
        this.description = description;
    }

    public final String getPermission() {
        return "base.command." + name;
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
     * Gets the usage for this {@link BaseCommand}.
     *
     * @return the usage
     */
    public String getUsage() {
        // Safe-check for broken commands.
        if (usage == null) usage = "";
        return USAGE_REPLACER_PATTERN.matcher(usage).replaceAll(name);
    }

    /**
     * Gets the usage for this {@link BaseCommand} for a given {@link Command} label.
     *
     * @param label the label to get for
     * @return the usage for given label
     */
    public String getUsage(String label) {
        return USAGE_REPLACER_PATTERN.matcher(usage).replaceAll(label);
    }

    /**
     * Sets the usage for this {@link BaseCommand}.
     *
     * @param usage the usage to set
     */
    public void setUsage(String usage) {
        this.usage = usage;
    }

    /**
     * Gets the aliases for this {@link BaseCommand}.
     *
     * @return the aliases
     */
    public String[] getAliases() {
        if (aliases == null) {
            aliases = ArrayUtils.EMPTY_STRING_ARRAY;
        }

        return Arrays.copyOf(aliases, aliases.length);
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
     * Checks if a {@link CommandSender} can see a {@link Player}.
     *
     * @param sender the {@link CommandSender}
     * @param target the {@link Player} to check against
     * @return true if {@link CommandSender} is not a {@link Player} or can see
     */
    public static boolean canSee(CommandSender sender, Player target) {
        return target != null && (!(sender instanceof Player) || ((Player) sender).canSee(target));
    }
}
