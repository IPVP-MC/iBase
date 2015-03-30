package com.doctordark.base.command.module.chat;

import com.doctordark.base.BasePlugin;
import com.doctordark.base.command.BaseCommand;
import com.doctordark.base.user.BaseUser;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

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
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only executable for players.");
            return true;
        }

        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();

        BaseUser baseUser = this.plugin.getUserManager().getUser(uuid);
        boolean newChatToggled = !baseUser.isToggledStaffChat();
        baseUser.setToggledStaffChat(newChatToggled);

        sender.sendMessage(ChatColor.GREEN + "You have toggled staff chat visibility on.");
        return true;
    }
}
