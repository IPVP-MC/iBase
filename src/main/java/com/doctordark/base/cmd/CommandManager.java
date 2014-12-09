package com.doctordark.base.cmd;

/**
 * Interface used for registering commands to plugin.
 */
public interface CommandManager {

    /**
     * Checks if a command is in the base.
     *
     * @param command the command to check
     */
    public boolean isCommand(BaseCommand command);

    /**
     * Registers all the commands from the module into the base.
     *
     * @param module the module to register
     */
    public void registerAll(BaseCommandModule module);

    /**
     * Registers a command to the base.
     *
     * @param command the command to register
     */
    public void registerCommand(BaseCommand command);

    /**
     * Registers an array of commands to the base.
     *
     * @param commands the commands to register
     */
    public void registerCommands(BaseCommand[] commands);

    /**
     * Un-registers a command from the base.
     *
     * @param command the command to unregister
     */
    public void unregisterCommand(BaseCommand command);

    /**
     * Gets a command by its name or aliases.
     *
     * @param id the id to search
     * @return the command that matches id
     */
    public BaseCommand getCommand(String id);
}
