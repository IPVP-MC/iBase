package com.doctordark.base.cmd.module;

import com.doctordark.base.cmd.BaseCommand;
import com.doctordark.base.cmd.BaseCommandModule;
import com.doctordark.base.cmd.module.chat.ClearChatCommand;
import com.doctordark.base.cmd.module.chat.DisableChatCommand;
import com.doctordark.base.cmd.module.chat.SlowChatCommand;
import com.doctordark.base.cmd.module.chat.StaffChatCommand;
import com.doctordark.base.cmd.module.chat.messaging.IgnoreCommand;
import com.doctordark.base.cmd.module.chat.messaging.MessageCommand;
import com.doctordark.base.cmd.module.chat.messaging.ReplyCommand;

import java.util.HashSet;
import java.util.Set;

/**
 * Module used for chat based commands.
 */
public class ChatModule implements BaseCommandModule {

    private Set<BaseCommand> commands;

    public ChatModule() {
        commands = new HashSet<BaseCommand>();
        commands.add(new ClearChatCommand());
        commands.add(new DisableChatCommand());
        commands.add(new SlowChatCommand());
        commands.add(new StaffChatCommand());

        commands.add(new IgnoreCommand());
        commands.add(new MessageCommand());
        commands.add(new ReplyCommand());
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
