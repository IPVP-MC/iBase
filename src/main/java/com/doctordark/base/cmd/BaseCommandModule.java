package com.doctordark.base.cmd;

import java.util.Set;

/**
 * Represents a command module / package.
 */
public abstract class BaseCommandModule {

    /**
     * Gets all the commands in this module.
     *
     * @return set of commands in module
     */
    public abstract Set<BaseCommand> getCommands();

    /**
     * Checks if this module is enabled.
     *
     * @return true if module is enabled
     */
    public abstract boolean isEnabled();
}
