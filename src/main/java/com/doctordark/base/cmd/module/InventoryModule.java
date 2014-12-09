package com.doctordark.base.cmd.module;

import com.doctordark.base.cmd.BaseCommand;
import com.doctordark.base.cmd.BaseCommandModule;
import com.doctordark.base.cmd.module.inventory.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Module used for inventory based commands.
 */
public class InventoryModule extends BaseCommandModule {

    private Set<BaseCommand> commands;

    public InventoryModule() {
        commands = new HashSet<BaseCommand>();
        commands.add(new ClearInvCommand());
        commands.add(new GiveCommand());
        commands.add(new IdCommand());
        commands.add(new InvSeeCommand());
        commands.add(new ItemCommand());
        commands.add(new MoreCommand());
        commands.add(new SkullCommand());
    }

    @Override
    public Set<BaseCommand> getCommands() {
        return commands;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
