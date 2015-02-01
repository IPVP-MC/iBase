package com.doctordark.base.cmd.module.chat;

import com.doctordark.base.BasePlugin;
import com.doctordark.base.cmd.BaseCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Command used for chatting to staff members.
 */
public class StaffChatCommand extends BaseCommand {

    private final BasePlugin plugin;

    public StaffChatCommand(BasePlugin plugin) {
        super("staffchat", "Enters staff chat mode.", "base.command.staffchat");
        this.setAliases(new String[]{"sc"});
        this.setUsage("/(command) [playerName]");
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player target;
        if (args.length < 1) {
            if (sender instanceof Player) {
                target = (Player) sender;
            } else {
                sender.sendMessage(ChatColor.RED + "Usage: /" + label + getUsage());
                return true;
            }
        } else {
            target = Bukkit.getServer().getPlayer(args[0]);
        }

        if (target == null || (sender instanceof Player && !((Player) sender).canSee(target))) {
            sender.sendMessage(ChatColor.GOLD + "Player '" + ChatColor.WHITE + args[0] + ChatColor.GOLD + "' not found.");
            return true;
        }

        UUID uuid = target.getUniqueId();

        boolean staffChat = (args.length >= 2) ? Boolean.parseBoolean(args[1]) : !plugin.getUserManager().isInStaffChat(uuid);
        plugin.getUserManager().setInStaffChat(uuid, staffChat);

        Command.broadcastCommandMessage(sender, ChatColor.YELLOW + "Staff chat mode of " + target.getName() + " set to " + staffChat + ".");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return (args.length == 1) ? null : Collections.<String>emptyList();
    }
}
