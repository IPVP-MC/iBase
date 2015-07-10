package com.doctordark.base.command.module.chat;

import com.doctordark.base.BasePlugin;
import com.doctordark.base.command.BaseCommand;
import com.doctordark.base.user.ServerParticipator;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class ToggleStaffChatCommand extends BaseCommand {

    private final BasePlugin plugin;

    public ToggleStaffChatCommand(BasePlugin plugin) {
        super("togglestaffchat", "Toggles staff chat visibility.", "base.command.togglestaffchat");
        setAliases(new String[]{"tsc", "togglesc"});
        setUsage("/(command)");
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        ServerParticipator participator = plugin.getUserManager().getParticipator(sender);
        if (participator == null) {
            sender.sendMessage(ChatColor.RED + "You are not allowed to do this.");
            return true;
        }

        boolean newChatToggled = !participator.isStaffChatVisible();
        participator.setStaffChatVisible(newChatToggled);

        sender.sendMessage(ChatColor.YELLOW + "You have toggled staff chat visibility " + (newChatToggled ? ChatColor.GREEN + "on" : ChatColor.RED + "off") + ChatColor.YELLOW + '.');
        return true;
    }
}
