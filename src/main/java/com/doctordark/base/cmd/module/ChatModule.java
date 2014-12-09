package com.doctordark.base.cmd.module;

import com.doctordark.base.cmd.BaseCommand;
import com.doctordark.base.cmd.BaseCommandModule;
import com.doctordark.base.cmd.module.chat.ClearChatCommand;

import java.util.HashSet;
import java.util.Set;

/**
 * Module used for chat based commands.
 */
public class ChatModule extends BaseCommandModule {

    private Set<BaseCommand> commands;

    public ChatModule() {
        commands = new HashSet<BaseCommand>();
        commands.add(new ClearChatCommand());
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
