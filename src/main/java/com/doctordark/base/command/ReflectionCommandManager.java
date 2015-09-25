package com.doctordark.base.command;

import com.doctordark.base.BasePlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.CommandMap;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.SimplePluginManager;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class ReflectionCommandManager implements CommandManager {

    private static final String PERMISSION_MESSAGE = ChatColor.RED + "You do not have permission for this command.";
    private final Map<String, BaseCommand> commandMap = new HashMap<>();

    public ReflectionCommandManager(final BasePlugin plugin) {
        final ConsoleCommandSender console = Bukkit.getConsoleSender();
        final Server server = Bukkit.getServer();

        server.getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {
                Optional<CommandMap> optionalCommandMap = getCommandMap(server);
                if (!optionalCommandMap.isPresent()) {
                    console.sendMessage('[' + plugin.getDescription().getFullName() + "] Command map not found");
                    return;
                }

                CommandMap bukkitCommandMap = optionalCommandMap.get();
                for (BaseCommand command : commandMap.values()) {
                    String commandName = command.getName();
                    Optional<PluginCommand> optional = getPluginCommand(commandName, plugin);
                    if (optional.isPresent()) {
                        PluginCommand pluginCommand = optional.get();
                        pluginCommand.setAliases(Arrays.asList(command.getAliases()));
                        pluginCommand.setDescription(command.getDescription());
                        pluginCommand.setExecutor(command);
                        pluginCommand.setTabCompleter(command);
                        pluginCommand.setUsage(command.getUsage());
                        pluginCommand.setPermission(command.getPermission());
                        pluginCommand.setPermissionMessage(ReflectionCommandManager.PERMISSION_MESSAGE);
                        bukkitCommandMap.register(plugin.getDescription().getName(), pluginCommand);
                    } else {
                        console.sendMessage('[' + plugin.getName() + "] " + ChatColor.YELLOW + "Failed to register command '" + commandName + "'.");
                    }
                }
            }
        }, 1L);
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

    /**
     * Gets a plugin command by its name and owning plugin.
     *
     * @param name   the name of the command
     * @param plugin the ownership of the command
     * @return the plugin command with given name
     */
    private Optional<PluginCommand> getPluginCommand(String name, Plugin plugin) {
        try {
            Constructor<PluginCommand> constructor = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
            constructor.setAccessible(true);
            return Optional.of(constructor.newInstance(name, plugin));
        } catch (IllegalArgumentException | IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException | SecurityException ex) {
            ex.printStackTrace();
            return Optional.empty();
        }
    }

    /**
     * Gets the command map of a server.
     *
     * @param server the server to get for
     * @return the command map object
     */
    private Optional<CommandMap> getCommandMap(Server server) {
        PluginManager pluginManager = server.getPluginManager();
        if (pluginManager instanceof SimplePluginManager) {
            try {
                Field field = SimplePluginManager.class.getDeclaredField("commandMap");
                field.setAccessible(true);
                return Optional.of((CommandMap) field.get(pluginManager));
            } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
                ex.printStackTrace();
                return Optional.empty();
            }
        }

        return Optional.empty();
    }
}
