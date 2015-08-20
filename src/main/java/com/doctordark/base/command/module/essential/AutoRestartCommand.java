package com.doctordark.base.command.module.essential;

import com.doctordark.base.BasePlugin;
import com.doctordark.base.command.BaseCommand;
import com.doctordark.base.command.CommandWrapper;
import com.doctordark.util.JavaUtils;
import com.doctordark.util.command.CommandArgument;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class AutoRestartCommand extends BaseCommand {

    private final CommandWrapper handler;

    public AutoRestartCommand(BasePlugin plugin) {
        super("autore", "Allows management of server restarts.", "base.command.autore");
        setAliases(new String[]{"autorestart"});
        setUsage("/(command) <cancel|time|schedule>");

        List<CommandArgument> arguments = Lists.newArrayListWithCapacity(3);
        arguments.add(new AutoRestartCancelArgument(plugin));
        arguments.add(new AutoRestartScheduleArgument(plugin));
        arguments.add(new AutoRestartTimeArgument(plugin));
        Collections.sort(arguments, new CommandWrapper.ArgumentComparator());
        handler = new CommandWrapper(arguments);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return handler.onCommand(sender, command, label, args);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return handler.onTabComplete(sender, command, label, args);
    }

    private static class AutoRestartCancelArgument extends CommandArgument {

        private final BasePlugin plugin;

        public AutoRestartCancelArgument(BasePlugin plugin) {
            super("cancel", "Cancels the current automatic restart.");
            this.plugin = plugin;
        }

        @Override
        public String getUsage(String label) {
            return '/' + label + ' ' + getName();
        }

        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (!plugin.getAutoRestartHandler().isPendingRestart()) {
                sender.sendMessage(ChatColor.RED + "There is not a restart task pending.");
                return true;
            }

            plugin.getAutoRestartHandler().cancelRestart();
            sender.sendMessage(ChatColor.YELLOW + "Automatic restart task cancelled.");
            return true;
        }
    }

    private static class AutoRestartScheduleArgument extends CommandArgument {

        private final BasePlugin plugin;

        public AutoRestartScheduleArgument(BasePlugin plugin) {
            super("schedule", "Schedule an automatic restart.");
            this.plugin = plugin;
            this.aliases = new String[]{"reschedule"};
        }

        @Override
        public String getUsage(String label) {
            return '/' + label + ' ' + getName() + " <time> [reason]";
        }

        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + "Usage: /" + label + ' ' + args[0].toLowerCase() + " <time> [reason]");
                return true;
            }

            Long millis = JavaUtils.parse(args[1]);
            if (millis == null) {
                sender.sendMessage(ChatColor.RED + "Invalid duration, use the correct format: 10m1s");
                return true;
            }

            String reason = StringUtils.join(args, ' ', 2, args.length);
            plugin.getAutoRestartHandler().scheduleRestart(millis, reason);
            Command.broadcastCommandMessage(sender, ChatColor.YELLOW + "Scheduled a restart to occur in " +
                    DurationFormatUtils.formatDurationWords(millis, true, true) + (reason.isEmpty() ? "" : " for " + reason) + '.');

            return true;
        }
    }

    private static class AutoRestartTimeArgument extends CommandArgument {

        private final BasePlugin plugin;

        public AutoRestartTimeArgument(BasePlugin plugin) {
            super("time", "Gets the remaining time until next restart.");
            this.plugin = plugin;
            this.aliases = new String[]{"remaining", "time"};
        }

        @Override
        public String getUsage(String label) {
            return '/' + label + ' ' + getName();
        }

        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (!plugin.getAutoRestartHandler().isPendingRestart()) {
                sender.sendMessage(ChatColor.RED + "There is not a restart task pending.");
                return true;
            }

            String reason = plugin.getAutoRestartHandler().getReason();
            sender.sendMessage(ChatColor.YELLOW + "Automatic restart task occurring in " +
                    DurationFormatUtils.formatDurationWords(plugin.getAutoRestartHandler().getRemainingMilliseconds(), true, true) +
                    (Strings.nullToEmpty(reason).isEmpty() ? "" : " for " + reason) + '.');

            return true;
        }
    }
}
