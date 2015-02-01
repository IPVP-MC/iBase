package com.doctordark.base.cmd.module.chat;

import com.doctordark.base.BasePlugin;
import com.doctordark.base.cmd.BaseCommand;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

/**
 * Command used to restrict the chat.
 */
public class AnnouncementsCommand extends BaseCommand {

    private final BasePlugin plugin;

    public AnnouncementsCommand(BasePlugin plugin) {
        super("announcements", "Manage automatic server announcements.", "base.command.announcements");
        this.setAliases(new String[]{});
        this.setUsage("/(command) <list|reload|setdelay>");
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Usage: " + getUsage());
            return true;
        }

        List<String> announcements = plugin.getServerManager().getAnnouncements();

        if (args[0].equalsIgnoreCase("reload")) {
            plugin.getServerManager().reloadServerData();
            sender.sendMessage("Reloaded announcements");
            return true;
        }

        if (args[0].equalsIgnoreCase("list")) {
            sender.sendMessage(ChatColor.LIGHT_PURPLE + "*** Announcements (" + announcements.size() + ") ***");

            List<String> colouredAnnouncements = new ArrayList<String>();
            for (String announcement : announcements) {
                colouredAnnouncements.add(ChatColor.translateAlternateColorCodes('&', announcement));
            }

            sender.sendMessage(ChatColor.GRAY + "[" + StringUtils.join(colouredAnnouncements, ", ") + "]");
            return true;
        }

        if (args[0].equalsIgnoreCase("setdelay")) {
            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + "Usage: /" + label + " " + args[0].toLowerCase() + " <delay>");
                return true;
            }

            Integer delay;
            try {
                delay = Integer.parseInt(args[1]);
            } catch (Exception ex) {
                sender.sendMessage(ChatColor.RED + "'" + args[1] + "' is not a number.");
                return true;
            }

            plugin.getServerManager().setAnnouncementDelay(delay);
            plugin.reloadSchedulers();

            sender.sendMessage(ChatColor.YELLOW + "Announcement delay is now " + delay + ".");
            return true;
        }

        sender.sendMessage(ChatColor.RED + "Usage: " + getUsage());
        return true;
    }
}
