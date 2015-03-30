package com.doctordark.base.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public abstract class CommandArgument {

    private final String name;
    private final String description;

    public CommandArgument(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public final String getName() {
        return this.name;
    }

    public final String getDescription() {
        return this.description;
    }

    public String getPermission() {
        return null;
    }

    public abstract String[] getAliases();

    public abstract String getUsage(String paramString);

    public abstract boolean onCommand(CommandSender paramCommandSender, Command paramCommand, String paramString, String[] paramArrayOfString);

    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return Collections.emptyList();
    }
}
