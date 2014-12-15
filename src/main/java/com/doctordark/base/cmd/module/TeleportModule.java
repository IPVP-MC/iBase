package com.doctordark.base.cmd.module;

import com.doctordark.base.cmd.BaseCommand;
import com.doctordark.base.cmd.BaseCommandModule;
import com.doctordark.base.cmd.module.teleport.TeleportCommand;
import com.doctordark.base.cmd.module.teleport.TopCommand;
import com.doctordark.base.cmd.module.teleport.WorldCommand;

import java.util.HashSet;
import java.util.Set;

/**
 * Module used for teleport based commands.
 */
public class TeleportModule implements BaseCommandModule {

    private Set<BaseCommand> commands;

    public TeleportModule() {
        commands = new HashSet<BaseCommand>();
        commands.add(new TeleportCommand());
        commands.add(new TopCommand());
        commands.add(new WorldCommand());
    }

    @Override
    public Set<BaseCommand> getCommands() {
        return commands;
    }

    @Override
    public void unregisterCommand(BaseCommand command) {
        commands.remove(command);
    }

    @Override
    public void unregisterCommands() {
        commands.clear();
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
