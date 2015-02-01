package com.doctordark.base.cmd.module;

import com.doctordark.base.BasePlugin;
import com.doctordark.base.cmd.BaseCommand;
import com.doctordark.base.cmd.BaseCommandModule;
import com.doctordark.base.cmd.module.chat.*;
import com.doctordark.base.cmd.module.chat.messaging.IgnoreCommand;
import com.doctordark.base.cmd.module.chat.messaging.MessageCommand;
import com.doctordark.base.cmd.module.chat.messaging.MessageSpyCommand;
import com.doctordark.base.cmd.module.chat.messaging.ReplyCommand;
import com.doctordark.base.cmd.module.chat.messaging.TogglepmCommand;

import java.util.HashSet;
import java.util.Set;

/**
 * Module used for chat based commands.
 */
public class ChatModule implements BaseCommandModule {

    private final Set<BaseCommand> commands;

    public ChatModule(BasePlugin plugin) {
        commands = new HashSet<BaseCommand>();
        commands.add(new AnnouncementsCommand(plugin));
        commands.add(new ClearChatCommand());
        commands.add(new DisableChatCommand(plugin));
        commands.add(new SlowChatCommand(plugin));
        commands.add(new StaffChatCommand(plugin));

        // Private messaging related:
        commands.add(new IgnoreCommand(plugin));
        commands.add(new MessageCommand());
        commands.add(new MessageSpyCommand(plugin));
        commands.add(new ReplyCommand(plugin));
        commands.add(new TogglepmCommand());
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
