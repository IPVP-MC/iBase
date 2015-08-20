package com.doctordark.base.command.module.essential;

import com.doctordark.base.BasePlugin;
import com.doctordark.base.command.BaseCommand;
import com.doctordark.base.user.BaseUser;
import com.doctordark.base.user.NameHistory;
import com.doctordark.base.user.ServerParticipator;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.time.FastDateFormat;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class NameHistoryCommand extends BaseCommand {

    private static final FastDateFormat FORMAT = FastDateFormat.getInstance("EEE, MMM d yy, hh:mmaaa", Locale.ENGLISH);
    private final BasePlugin plugin;

    public NameHistoryCommand(BasePlugin plugin) {
        super("namehistory", "Checks name change histories of players.", "base.command.namehistory");
        setUsage("/(command) <player>");
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Usage: " + getUsage(label));
            return true;
        }

        BaseUser targetUser = null;
        List<String> messages = Lists.newArrayList();
        for (ServerParticipator participator : plugin.getUserManager().getParticipators().values()) {
            if (participator instanceof BaseUser) {
                BaseUser baseUser = (BaseUser) participator;
                List<NameHistory> nameHistories = baseUser.getNameHistories();
                for (NameHistory nameHistory : nameHistories) {
                    if (nameHistory.getName().equalsIgnoreCase(args[0])) {
                        messages.add(ChatColor.GRAY + nameHistory.getName() + " (" + FORMAT.format(nameHistory.getMillis()) + ')');
                        targetUser = baseUser;
                        break;
                    }
                }
            }
        }

        if (targetUser == null) {
            sender.sendMessage(ChatColor.GOLD + "Player '" + ChatColor.WHITE + args[0] + ChatColor.GOLD + "' not found.");
            return true;
        }

        sender.sendMessage(messages.toArray(new String[messages.size()]));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return args.length == 1 ? null : Collections.emptyList();
    }
}
