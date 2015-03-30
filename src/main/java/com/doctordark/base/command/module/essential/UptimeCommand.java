package com.doctordark.base.command.module.essential;

import com.doctordark.base.command.BaseCommand;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.lang.management.ManagementFactory;

public class UptimeCommand extends BaseCommand {

    public UptimeCommand() {
        super("uptime", "Check the uptime of the server.", "base.command.uptime");
        setAliases(new String[0]);
        setUsage("/(command)");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        long begin = ManagementFactory.getRuntimeMXBean().getStartTime();
        long after = System.currentTimeMillis();

        String upTime = DurationFormatUtils.formatDurationWords(after - begin, true, true);
        sender.sendMessage(ChatColor.GREEN + "Server up-time: " + ChatColor.LIGHT_PURPLE + upTime + ChatColor.GREEN + ".");
        return true;
    }
}