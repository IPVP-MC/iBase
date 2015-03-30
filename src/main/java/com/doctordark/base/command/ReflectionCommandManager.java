package com.doctordark.base.command;

import com.doctordark.base.BasePlugin;
import com.google.common.collect.Maps;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ReflectionCommandManager implements CommandManager {

    private static final String PERMISSION_MESSAGE = ChatColor.RED + "You do not have permission for this command.";
    private final Map<String, BaseCommand> commandMap = Maps.newHashMap();

    public ReflectionCommandManager(final BasePlugin plugin) {
        final ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();

        Bukkit.getServer().getScheduler().runTaskLater(plugin, new Runnable() {
            public void run() {
                CommandMap bukkitCommandMap = getCommandMap();
                if (bukkitCommandMap == null) {
                    console.sendMessage("[" + plugin.getDescription().getFullName() + "] Command map not found");
                    return;
                }

                for (BaseCommand command : ReflectionCommandManager.this.commandMap.values()) {
                    String cmdName = command.getName();
                    PluginCommand pluginCommand = getPluginCommand(cmdName, plugin);
                    if (pluginCommand == null) {
                        console.sendMessage("[" + plugin.getName() + "] " + ChatColor.YELLOW + "Failed to register command '" + cmdName + "'.");
                    } else {
                        pluginCommand.setAliases(Arrays.asList(command.getAliases()));
                        pluginCommand.setDescription(command.getDescription());
                        pluginCommand.setExecutor(command);
                        pluginCommand.setTabCompleter(command);
                        pluginCommand.setUsage(command.getUsage());
                        pluginCommand.setPermission(command.getPermission());
                        pluginCommand.setPermissionMessage(ReflectionCommandManager.PERMISSION_MESSAGE);
                        bukkitCommandMap.register(plugin.getDescription().getName(), pluginCommand);
                    }
                }
            }
        }, 1L);
    }

    public List<String> getCompletions(String[] args, List<String> input) {
        return CommandArgumentHandler.getCompletions(args, input);
    }

    @Override
    public boolean isCommand(BaseCommand command) {
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
        return commandMap.containsKey(id) ? commandMap.get(id) : null;
    }

    private PluginCommand getPluginCommand(String name, Plugin plugin) {
        try {
            Constructor<PluginCommand> constructor = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
            constructor.setAccessible(true);
            return constructor.newInstance(name, plugin);
        } catch (IllegalArgumentException | IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException | SecurityException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private CommandMap getCommandMap() {
        PluginManager pluginManager = Bukkit.getServer().getPluginManager();
        if (pluginManager instanceof SimplePluginManager) {
            try {
                Field field = SimplePluginManager.class.getDeclaredField("commandMap");
                field.setAccessible(true);
                return (CommandMap) field.get(pluginManager);
            } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
                ex.printStackTrace();
            }
        }

        return null;
    }
}
