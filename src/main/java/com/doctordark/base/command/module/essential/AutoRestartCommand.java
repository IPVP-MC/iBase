package com.doctordark.base.command.module.essential;

import com.doctordark.base.BasePlugin;
import com.doctordark.base.command.BaseCommand;
import com.doctordark.base.util.BaseUtil;
import net.minecraft.util.org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class AutoRestartCommand extends BaseCommand {

    private final BasePlugin plugin;

    public AutoRestartCommand(BasePlugin plugin) {
        super("autore", "Allows management of server restarts.", "base.command.autore");
        setAliases(new String[]{"autorestart"});
        setUsage("/(command) <cancel|time|schedule>");
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Usage: " + getUsage(label));
            return true;
        }

        if (args[0].equalsIgnoreCase("cancel")) {
            if (!plugin.getAutoRestartHandler().isPendingRestart()) {
                sender.sendMessage(ChatColor.RED + "There is not a restart task pending.");
                return true;
            }

            plugin.getAutoRestartHandler().cancelRestart();
            sender.sendMessage(ChatColor.YELLOW + "Automatic restart task cancelled.");
            return true;
        }

        if (args[0].equalsIgnoreCase("time")) {
            if (!plugin.getAutoRestartHandler().isPendingRestart()) {
                sender.sendMessage(ChatColor.RED + "There is not a restart task pending.");
                return true;
            }

            long remaining = plugin.getAutoRestartHandler().getRemainingMillis();
            String reason = plugin.getAutoRestartHandler().getReason();

            sender.sendMessage(ChatColor.YELLOW + "Automatic restart task occurring in " +
                    DurationFormatUtils.formatDurationWords(remaining, true, true) + ((reason == null) || (reason.isEmpty()) ? "" : " for " + reason) + ".");
            return true;
        }

        if (args[0].equalsIgnoreCase("schedule")) {
            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + "Usage: /" + label + " " + args[0].toLowerCase() + " <time> [reason]");
                return true;
            }

            long ticks = BaseUtil.parse(args[1]);
            if (ticks <= 0L) {
                sender.sendMessage(ChatColor.RED + "Invalid duration, use the correct format: 10m1s");
                return true;
            }

            StringBuilder builder = new StringBuilder();
            for (int i = 2; i < args.length; i++) {
                builder.append(args[i]).append(" ");
            }

            String reason = builder.toString().trim();
            plugin.getAutoRestartHandler().scheduleRestart((int) (ticks / 1000L), reason);

            Command.broadcastCommandMessage(sender, ChatColor.YELLOW + "Scheduled a restart to occur in " +
                    DurationFormatUtils.formatDurationWords(ticks, true, true) + (reason.isEmpty() ? "" : " for " + reason) + ".");
            return true;
        }

        sender.sendMessage(ChatColor.RED + "Usage: " + getUsage(label));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return Collections.emptyList();
    }
}
