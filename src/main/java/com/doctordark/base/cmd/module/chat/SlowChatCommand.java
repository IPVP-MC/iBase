package com.doctordark.base.cmd.module.chat;

import com.doctordark.base.BasePlugin;
import com.doctordark.base.cmd.BaseCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * Command used to slow the chat down.
 */
public class SlowChatCommand extends BaseCommand {

    private final BasePlugin plugin;

    public SlowChatCommand(BasePlugin plugin) {
        super("slowchat", "Slows the chat down for non-staff.", "base.command.slowchat");
        this.setAliases(new String[]{});
        this.setUsage("/(command)");
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        boolean newEnabled = !plugin.getServerManager().isChatSlowed();
        plugin.getServerManager().setChatSlowed(newEnabled);
        Bukkit.broadcastMessage(ChatColor.YELLOW + "Global chat is " + (newEnabled ? "now" : "no longer") + " slowed.");
        return true;
    }
}
