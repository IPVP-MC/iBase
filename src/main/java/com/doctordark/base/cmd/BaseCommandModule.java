package com.doctordark.base.cmd;

import java.util.Set;

/**
 * Represents a command module / package.
 */
public interface BaseCommandModule {

    /**
     * Gets all the commands in this module.
     *
     * @return set of commands in module
     */
    public abstract Set<BaseCommand> getCommands();

    /**
     * Un-registers a command from the module.
     *
     * @param command the command to unregister
     */
    public abstract void unregisterCommand(BaseCommand command);

    /**
     * Un-registers all the commands in the module.
     */
    public abstract void unregisterCommands();

    /**
     * Checks if this module is enabled.
     *
     * @return true if module is enabled
     */
    public abstract boolean isEnabled();
}
