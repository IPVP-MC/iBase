package com.doctordark.base.command;

import com.google.common.collect.Sets;

import java.util.Set;

/**
 * Represents a command module / package.
 */
public abstract class BaseCommandModule {

    protected final Set<BaseCommand> commands;
    protected boolean enabled;

    public BaseCommandModule() {
        enabled = true; //TODO: non hardcoded
        commands = Sets.newHashSet();
    }

    /**
     * Gets all the commands in this module.
     *
     * @return set of commands in module
     */
    Set<BaseCommand> getCommands() {
        return commands;
    }

    /**
     * Un-registers a command from the module.
     *
     * @param command the command to unregister
     */
    void unregisterCommand(BaseCommand command) {
        commands.remove(command);
    }

    /**
     * Un-registers all the commands in the module.
     */
    void unregisterCommands() {
        commands.clear();
    }

    /**
     * Checks if this module is enabled.
     *
     * @return true if module is enabled
     */
    boolean isEnabled() {
        return enabled;
    }

    /**
     * Sets if this module is enabled.
     *
     * @param enabled if module should be enabled
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
