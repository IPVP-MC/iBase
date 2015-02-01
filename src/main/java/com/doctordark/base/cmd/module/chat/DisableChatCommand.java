package com.doctordark.base.cmd.module.chat;

import com.doctordark.base.BasePlugin;
import com.doctordark.base.cmd.BaseCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * Command used to restrict the chat.
 */
public class DisableChatCommand extends BaseCommand {

    private final BasePlugin plugin;

    public DisableChatCommand(BasePlugin plugin) {
        super("disablechat", "Disables the chat for non-staff.", "base.command.disablechat");
        this.setAliases(new String[]{"mutechat", "disablechat", "rc", "restrictchat"});
        this.setUsage("/(command)");
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        boolean newEnabled = !plugin.getServerManager().isChatEnabled();
        plugin.getServerManager().setChatEnabled(newEnabled);
        Bukkit.broadcastMessage(ChatColor.YELLOW + "Global chat is " + (newEnabled ? "now" : "no longer") + " enabled.");
        return true;
    }
}
