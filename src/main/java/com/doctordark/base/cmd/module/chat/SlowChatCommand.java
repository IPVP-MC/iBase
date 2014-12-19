package com.doctordark.base.cmd.module.chat;

import com.doctordark.base.cmd.BaseCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * Command used to slow the chat down.
 */
public class SlowChatCommand extends BaseCommand {

    public SlowChatCommand() {
        super("slowchat", "Slows the chat down for non-staff.", "base.command.slowchat");
        this.setAliases(new String[]{});
        this.setUsage("/(command)");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        boolean newEnabled = !getBasePlugin().getServerManager().isChatSlowed();
        Bukkit.broadcastMessage(ChatColor.YELLOW + "Chat is " + (newEnabled ? "now" : "no longer") + " slowed!");
        getBasePlugin().getServerManager().setChatSlowed(newEnabled);
        return true;
    }
}
