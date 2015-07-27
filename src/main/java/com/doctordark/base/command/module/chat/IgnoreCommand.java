package com.doctordark.base.command.module.chat;

import com.doctordark.base.BasePlugin;
import com.doctordark.base.command.BaseCommand;
import com.doctordark.base.command.CommandWrapper;
import com.doctordark.base.user.BaseUser;
import com.doctordark.util.command.CommandArgument;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class IgnoreCommand extends BaseCommand {

    private final CommandWrapper handler;

    public IgnoreCommand(final BasePlugin plugin) {
        super("ignore", "Ignores a player from messages.", "base.command.ignore");
        setUsage("/(command) <list|add|del|clear> [playerName]");

        List<CommandArgument> arguments = Lists.newArrayListWithExpectedSize(4);
        arguments.add(new IgnoreClearArgument(plugin));
        arguments.add(new IgnoreListArgument(plugin));
        arguments.add(new IgnoreAddArgument(plugin));
        arguments.add(new IgnoreDeleteArgument(plugin));
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

    private static class IgnoreAddArgument extends CommandArgument {

        private final BasePlugin plugin;

        public IgnoreAddArgument(BasePlugin plugin) {
            super("add", "Starts ignoring a player.");
            this.plugin = plugin;
        }

        @Override
        public String getUsage(String label) {
            return '/' + label + ' ' + getName() + " <playerName>";
        }

        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
                return true;
            }

            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + "Usage: " + getUsage(label));
                return true;
            }

            Player player = (Player) sender;
            UUID uuid = player.getUniqueId();
            BaseUser baseUser = plugin.getUserManager().getUser(uuid);
            Set<String> ignoring = baseUser.getIgnoring();

            Player target = Bukkit.getPlayer(args[1]);

            if (target == null || !canSee(sender, target)) {
                sender.sendMessage(ChatColor.GOLD + "Player '" + ChatColor.WHITE + args[1] + ChatColor.GOLD + "' not found.");
                return true;
            }

            if (sender.equals(target)) {
                sender.sendMessage(ChatColor.RED + "You may not ignore yourself.");
                return true;
            }

            if (target.hasPermission("base.command.ignore.exempt")) {
                sender.sendMessage(ChatColor.RED + "You do not have permission to ignore this player.");
                return true;
            }

            String targetName = target.getName();
            if (ignoring.add(target.getName())) {
                sender.sendMessage(ChatColor.GOLD + "You are now ignoring " + targetName + '.');
            } else {
                sender.sendMessage(ChatColor.RED + "You are already ignoring someone named " + targetName + '.');
            }

            return true;
        }

        @Override
        public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
            return args.length == 2 ? null : Collections.emptyList();
        }
    }

    private static class IgnoreClearArgument extends CommandArgument {

        private final BasePlugin plugin;

        public IgnoreClearArgument(BasePlugin plugin) {
            super("clear", "Clears all ignored players.");
            this.plugin = plugin;
        }

        @Override
        public String getUsage(String label) {
            return '/' + label + ' ' + getName();
        }

        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
                return true;
            }

            Set<String> ignoring = plugin.getUserManager().getUser(((Player) sender).getUniqueId()).getIgnoring();

            if (ignoring.isEmpty()) {
                sender.sendMessage(ChatColor.RED + "Your ignore list is already empty.");
                return true;
            }

            ignoring.clear();
            sender.sendMessage(ChatColor.YELLOW + "Your ignore list has been cleared.");
            return true;
        }

        @Override
        public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
            return Collections.emptyList();
        }
    }

    private static class IgnoreListArgument extends CommandArgument {

        private final BasePlugin plugin;

        public IgnoreListArgument(BasePlugin plugin) {
            super("list", "Lists all ignored players.");
            this.plugin = plugin;
        }

        @Override
        public String getUsage(String label) {
            return '/' + label + ' ' + getName();
        }

        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
                return true;
            }

            Set<String> ignoring = plugin.getUserManager().getUser(((Player) sender).getUniqueId()).getIgnoring();

            if (ignoring.isEmpty()) {
                sender.sendMessage(ChatColor.YELLOW + "You are not ignoring anyone.");
                return true;
            }

            sender.sendMessage(ChatColor.YELLOW + "You are ignoring (" + ignoring.size() + ") members: " +
                    '[' + ChatColor.WHITE + StringUtils.join(ignoring, ", ") + ChatColor.YELLOW + ']');

            return true;
        }

        @Override
        public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
            return Collections.emptyList();
        }
    }

    private static class IgnoreDeleteArgument extends CommandArgument {

        private final BasePlugin plugin;

        public IgnoreDeleteArgument(BasePlugin plugin) {
            super("delete", "Un-ignores a player.");
            this.plugin = plugin;
            this.aliases = new String[]{"del", "remove", "unset"};
        }

        @Override
        public String getUsage(String label) {
            return '/' + label + ' ' + getName() + " <playerName>";
        }

        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
                return true;
            }

            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + "Usage: " + getUsage(label));
                return true;
            }

            sender.sendMessage(ChatColor.YELLOW + "You are " + (plugin.getUserManager().getUser(((Player) sender).getUniqueId()).getIgnoring().remove(args[1]) ?
                    ChatColor.RED + "not" : ChatColor.GREEN + "no longer") + ChatColor.YELLOW + " ignoring " + args[1] + '.');

            return true;
        }

        @Override
        public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
            return null;
        }
    }
}
