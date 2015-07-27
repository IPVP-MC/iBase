package com.doctordark.base.command.module.chat;

import com.doctordark.base.BasePlugin;
import com.doctordark.base.command.BaseCommand;
import com.doctordark.base.user.BaseUser;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ToggleChatCommand extends BaseCommand {

    private final BasePlugin plugin;

    public ToggleChatCommand(BasePlugin plugin) {
        super("togglechat", "Toggles global chat visibility.", "base.command.togglechat");
        setAliases(new String[]{"tgc", "toggleglobalchat"});
        setUsage("/(command)");
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only executable for players.");
            return true;
        }

        Player player = (Player) sender;

        BaseUser baseUser = plugin.getUserManager().getUser(player.getUniqueId());
        boolean newChatToggled = !baseUser.isGlobalChatVisible();
        baseUser.setGlobalChatVisible(newChatToggled);

        sender.sendMessage(ChatColor.YELLOW + "You have toggled global chat visibility " + (newChatToggled ? ChatColor.GREEN + "on" : ChatColor.RED + "off") + ChatColor.YELLOW + '.');
        return true;
    }
}
