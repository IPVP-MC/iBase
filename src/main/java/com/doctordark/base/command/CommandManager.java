package com.doctordark.base.command;

/**
 * Interface used for registering {@link BaseCommand}s to a {@link CommandManager}.
 */
public interface CommandManager {

    /**
     * Checks if a {@link BaseCommand} is held by this {@link CommandManager}.
     *
     * @param command the {@link BaseCommand} to check
     * @return true if this manager contains the command
     */
    boolean containsCommand(BaseCommand command);

    /**
     * Registers all the {@link BaseCommand}s from a {@link BaseCommandModule}
     * into this {@link CommandManager}.
     *
     * @param module the {@link BaseCommandModule} to register
     */
    void registerAll(BaseCommandModule module);

    /**
     * Registers a {@link BaseCommand} to this {@link CommandManager}.
     *
     * @param command the {@link BaseCommand} to register
     */
    void registerCommand(BaseCommand command);

    /**
     * Registers an array of {@link BaseCommand}s to this {@link CommandManager}.
     *
     * @param commands the {@link BaseCommand}s to register
     */
    void registerCommands(BaseCommand[] commands);

    /**
     * Un-registers a {@link BaseCommand} from this {@link CommandManager}.
     *
     * @param command the {@link BaseCommand} to unregister
     */
    void unregisterCommand(BaseCommand command);

    /**
     * Gets a {@link BaseCommand} by its name or aliases.
     *
     * @param id the id to search
     * @return the {@link BaseCommand} that matches query
     */
    BaseCommand getCommand(String id);
}
