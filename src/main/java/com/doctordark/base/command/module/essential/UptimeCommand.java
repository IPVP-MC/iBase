package com.doctordark.base.command.module.essential;

import com.doctordark.base.command.BaseCommand;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.lang.management.ManagementFactory;

public class UptimeCommand extends BaseCommand {

    public UptimeCommand() {
        super("uptime", "Check the uptime of the server.");
        setUsage("/(command)");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String upTime = DurationFormatUtils.formatDurationWords(System.currentTimeMillis() - ManagementFactory.getRuntimeMXBean().getStartTime(), true, true);
        sender.sendMessage(ChatColor.GREEN + "Server up-time: " + ChatColor.LIGHT_PURPLE + upTime + ChatColor.GREEN + '.');
        return true;
    }
}
