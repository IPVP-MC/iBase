package com.doctordark.base.command.module;

import com.doctordark.base.BasePlugin;
import com.doctordark.base.command.BaseCommandModule;
import com.doctordark.base.command.module.chat.*;

public class ChatModule extends BaseCommandModule {

    public ChatModule(BasePlugin plugin) {
        commands.add(new BroadcastCommand(plugin));
        commands.add(new BroadcastRawCommand());
        commands.add(new ClearChatCommand());
        commands.add(new DisableChatCommand(plugin));
        commands.add(new SlowChatCommand(plugin));
        commands.add(new StaffChatCommand(plugin));

        commands.add(new IgnoreCommand(plugin));
        commands.add(new MessageCommand());
        commands.add(new MessageSpyCommand(plugin));
        commands.add(new ReplyCommand(plugin));
        commands.add(new ToggleChatCommand(plugin));
        commands.add(new ToggleMessagesCommand(plugin));
        commands.add(new ToggleStaffChatCommand(plugin));
    }
}
