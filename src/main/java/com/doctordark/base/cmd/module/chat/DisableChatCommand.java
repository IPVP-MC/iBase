package com.doctordark.base.cmd.module.chat;

import com.doctordark.base.cmd.BaseCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * Command used to restrict the chat.
 */
public class DisableChatCommand extends BaseCommand {

    public DisableChatCommand() {
        super("disablechat", "Disables the chat for non-staff.", "base.command.disablechat");
        this.setAliases(new String[]{"mutechat", "disablechat", "rc", "restrictchat"});
        this.setUsage("/(command)");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        return true;
    }
}
