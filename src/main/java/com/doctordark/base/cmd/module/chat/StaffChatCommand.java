package com.doctordark.base.cmd.module.chat;

import com.doctordark.base.cmd.BaseCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * Command used for chatting to staff members.
 */
public class StaffChatCommand extends BaseCommand {

    public StaffChatCommand() {
        super("staffchat", "Enters staff chat mode.", "base.command.staffchat");
        this.setAliases(new String[]{"sc"});
        this.setUsage("/(command)");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        return true;
    }
}
