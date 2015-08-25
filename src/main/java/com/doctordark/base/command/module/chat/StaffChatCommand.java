package com.doctordark.base.command.module.chat;

import com.doctordark.base.BasePlugin;
import com.doctordark.base.command.BaseCommand;
import com.doctordark.base.user.BaseUser;
import com.doctordark.base.user.ServerParticipator;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Locale;

public class StaffChatCommand extends BaseCommand {

    private final BasePlugin plugin;

    public StaffChatCommand(BasePlugin plugin) {
        super("staffchat", "Enters staff chat mode.");
        setAliases(new String[]{"sc", "ac"});
        setUsage("/(command) [playerName]");
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        final ServerParticipator participator = plugin.getUserManager().getParticipator(sender);

        if (participator == null) {
            sender.sendMessage(ChatColor.RED + "You are not allowed to do this.");
            return true;
        }

        final ServerParticipator target;
        if (args.length <= 0) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Usage: /" + label + " <message|playerName>");
                return true;
            }

            target = participator;
        } else {
            Player targetPlayer = Bukkit.getPlayerExact(args[0]);
            if (targetPlayer == null || !canSee(sender, targetPlayer) || !sender.hasPermission(command.getPermission() + ".others")) {
                String message = StringUtils.join(args, ' ');
                String format = ChatColor.AQUA + String.format(Locale.ENGLISH, "%1$s: %2$s", sender.getName(), message);

                Bukkit.getConsoleSender().sendMessage(format);
                for (Player other : Bukkit.getOnlinePlayers()) {
                    BaseUser otherUser = plugin.getUserManager().getUser(other.getUniqueId());
                    if (otherUser.isStaffChatVisible() && other.hasPermission("base.command.staffchat")) {
                        other.sendMessage(format);
                    }
                }

                return true;
            }

            target = plugin.getUserManager().getUser(targetPlayer.getUniqueId());
        }

        boolean newStaffChat = !target.isInStaffChat() || (args.length >= 2 && Boolean.parseBoolean(args[1]));
        target.setInStaffChat(newStaffChat);

        Command.broadcastCommandMessage(sender, ChatColor.YELLOW + "Staff chat mode of " + target.getName() + " set to " + newStaffChat + '.');
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return null;
    }
}
