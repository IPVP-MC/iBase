package com.doctordark.base.command;

import com.google.common.collect.Sets;

import java.util.Set;

/**
 * Represents a {@link BaseCommandModule} for management of {@link BaseCommand}s.
 */
public abstract class BaseCommandModule {

    protected final Set<BaseCommand> commands;
    protected boolean enabled;

    public BaseCommandModule() {
        this.enabled = true; //TODO: Configurable
        this.commands = Sets.newHashSet();
    }

    /**
     * Gets all the {@link BaseCommand}s registered to this {@link BaseCommandModule}.
     *
     * @return set of registered {@link BaseCommand}s
     */
    Set<BaseCommand> getCommands() {
        return commands;
    }

    /**
     * Un-registers a {@link BaseCommand} from this {@link BaseCommandModule}.
     *
     * @param command the {@link BaseCommand} to unregister
     */
    void unregisterCommand(BaseCommand command) {
        commands.remove(command);
    }

    /**
     * Un-registers all the {@link BaseCommand}s in this {@link BaseCommandModule}.
     */
    void unregisterCommands() {
        commands.clear();
    }

    /**
     * Checks if this {@link BaseCommandModule} is enabled.
     *
     * @return true if is enabled
     */
    boolean isEnabled() {
        return enabled;
    }

    /**
     * Sets if this {@link BaseCommandModule} is enabled.
     *
     * @param enabled if should be enabled
     */
    void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
