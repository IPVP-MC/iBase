package com.doctordark.base.command;

import com.doctordark.base.BasePlugin;
import com.google.common.collect.Maps;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

/**
 * Implementation of the command manager.
 */
public class SimpleCommandManager implements CommandManager {

    private static final String PERMISSION_MESSAGE = ChatColor.RED + "You do not have permission to execute this command.";

    private final Map<String, BaseCommand> commandMap = Maps.newHashMap();

    public SimpleCommandManager(final BasePlugin plugin) {
        final ConsoleCommandSender console = plugin.getServer().getConsoleSender();

        // Load all the modules first.
        plugin.getServer().getScheduler().runTask(plugin, new Runnable() {
            @Override
            public void run() {
                for (BaseCommand command : commandMap.values()) {
                    String commandName = command.getName();
                    PluginCommand pluginCommand = plugin.getCommand(commandName);
                    if (pluginCommand == null) {
                        console.sendMessage("[" + plugin.getName() + "] " + ChatColor.YELLOW + "Failed to register command '" + commandName + "'.");
                        console.sendMessage("[" + plugin.getName() + "] " + ChatColor.YELLOW + "Reason: Undefined in plugin.yml.");
                        continue;
                    }

                    pluginCommand.setAliases(Arrays.asList(command.getAliases()));
                    pluginCommand.setDescription(command.getDescription());
                    pluginCommand.setExecutor(command);
                    pluginCommand.setTabCompleter(command);
                    pluginCommand.setUsage(command.getUsage());
                    pluginCommand.setPermission(command.getPermission());
                    pluginCommand.setPermissionMessage(PERMISSION_MESSAGE);
                }
            }
        });
    }

    @Override
    public boolean containsCommand(BaseCommand command) {
        return commandMap.containsValue(command);
    }

    @Override
    public void registerAll(BaseCommandModule module) {
        if (module.isEnabled()) {
            Set<BaseCommand> commands = module.getCommands();
            for (BaseCommand command : commands) {
                commandMap.put(command.getName(), command);
            }
        }
    }

    @Override
    public void registerCommand(BaseCommand command) {
        commandMap.put(command.getName(), command);
    }

    @Override
    public void registerCommands(BaseCommand[] commands) {
        for (BaseCommand command : commands) {
            commandMap.put(command.getName(), command);
        }
    }

    @Override
    public void unregisterCommand(BaseCommand command) {
        commandMap.values().remove(command);
    }

    @Override
    public BaseCommand getCommand(String id) {
        return commandMap.get(id);
    }
}
