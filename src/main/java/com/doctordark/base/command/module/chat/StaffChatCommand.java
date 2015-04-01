package com.doctordark.base.command.module.chat;

import com.doctordark.base.BasePlugin;
import com.doctordark.base.command.BaseCommand;
import com.doctordark.base.user.BaseUser;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class StaffChatCommand extends BaseCommand {

    private final BasePlugin plugin;

    public StaffChatCommand(BasePlugin plugin) {
        super("staffchat", "Enters staff chat mode.", "base.command.staffchat");
        setAliases(new String[]{"sc", "ac"});
        setUsage("/(command) [playerName]");
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        final Player target;
        if (args.length > 1 && sender.hasPermission(command.getPermission() + ".others")) {
            target = Bukkit.getServer().getPlayer(args[0]);
        } else if (!(sender instanceof Player))  {
            sender.sendMessage(ChatColor.RED + "Usage: " + getUsage(label));
            return true;
        } else {
            target = (Player) sender;
        }

        if ((target == null) || (((sender instanceof Player)) && (!((Player) sender).canSee(target)))) {
            StringBuilder builder = new StringBuilder();
            for (String argument : args) {
                builder.append(argument).append(" ");
            }

            String message = builder.toString();
            String format = ChatColor.AQUA + String.format(Locale.ENGLISH, "%1$s: %2$s", sender.getName(), message);

            Bukkit.getServer().getConsoleSender().sendMessage(format);
            for (Player other : Bukkit.getServer().getOnlinePlayers()) {
                BaseUser otherUser = this.plugin.getUserManager().getUser(other.getUniqueId());
                if ((!otherUser.isToggledStaffChat()) && (other.hasPermission("base.command.staffchat"))) {
                    other.sendMessage(format);
                }
            }

            return true;
        }

        UUID uuid = target.getUniqueId();
        BaseUser baseTarget = this.plugin.getUserManager().getUser(uuid);

        boolean newStaffChat = !baseTarget.isInStaffChat() || (args.length >= 2 && Boolean.parseBoolean(args[1]));
        baseTarget.setInStaffChat(newStaffChat);

        Command.broadcastCommandMessage(sender, ChatColor.YELLOW + "Staff chat mode of " + target.getName() + " set to " + newStaffChat + ".");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return args.length == 1 ? null : Collections.emptyList();
    }
}
