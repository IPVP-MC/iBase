package com.doctordark.base.command.module.essential;

import com.doctordark.base.command.BaseCommand;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.lang.management.ManagementFactory;
import java.util.Locale;
import java.util.TimeZone;

public class UptimeCommand extends BaseCommand {

    private static final FastDateFormat TIME_FORMATTER = FastDateFormat.getInstance("dd/MM HH:mm:ss", TimeZone.getTimeZone("GMT+1"), Locale.ENGLISH);

    public UptimeCommand() {
        super("uptime", "Check the uptime of the server.");
        setUsage("/(command)");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        long startTime = ManagementFactory.getRuntimeMXBean().getStartTime();
        String upTime = DurationFormatUtils.formatDurationWords(System.currentTimeMillis() - startTime, true, true);
        sender.sendMessage(ChatColor.BLUE + "Server up-time: " + ChatColor.GOLD + upTime + ChatColor.BLUE + ", started at " + TIME_FORMATTER.format(startTime) + ".");
        return true;
    }
}
