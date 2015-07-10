package com.doctordark.base.command.module.chat;

import com.doctordark.base.BasePlugin;
import com.doctordark.base.command.BaseCommand;
import com.doctordark.util.JavaUtils;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.concurrent.TimeUnit;

public class SlowChatCommand extends BaseCommand {

    private static final long DEFAULT_DELAY = TimeUnit.MINUTES.toMillis(5L);
    private final BasePlugin plugin;

    public SlowChatCommand(BasePlugin plugin) {
        super("slowchat", "Slows the chat down for non-staff.", "base.command.slowchat");
        setAliases(new String[]{"slow"});
        setUsage("/(command)");
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        long oldTicks = plugin.getServerHandler().getRemainingChatSlowedMillis();
        Long newTicks;
        if (oldTicks > 0L) {
            newTicks = 0L;
        } else if (args.length < 1) {
            newTicks = oldTicks > 0L ? 0L : DEFAULT_DELAY;
        } else {
            newTicks = JavaUtils.parse(args[0]);
            if (newTicks == null) {
                sender.sendMessage(ChatColor.RED + "'" + args[0] + "' is an invalid time format.");
                return true;
            }
        }

        plugin.getServerHandler().setChatSlowedMillis(newTicks);
        Bukkit.getServer().broadcastMessage(ChatColor.YELLOW + "Global chat is " + (newTicks > 0L ?
                ChatColor.GOLD + "now slowed for " + DurationFormatUtils.formatDurationWords(newTicks, true, true) :
                ChatColor.RED + "no longer slowed") + ChatColor.YELLOW + '.');

        return true;
    }
}
