package com.doctordark.base.command.module.chat;

import com.doctordark.base.BasePlugin;
import com.doctordark.base.command.BaseCommand;
import com.doctordark.base.command.CommandWrapper;
import com.doctordark.base.user.BaseUser;
import com.doctordark.util.JavaUtils;
import com.doctordark.util.command.CommandArgument;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class MessageSpyCommand extends BaseCommand {

    private final CommandWrapper handler;

    public MessageSpyCommand(final BasePlugin plugin) {
        super("messagespy", "Spies on the PM's of a player.", "base.command.messagespy");
        this.setAliases(new String[]{"ms", "msgspy", "pmspy", "whisperspy", "privatemessagespy", "tellspy"});
        this.setUsage("/(command) <list|add|del|clear> [playerName]");

        List<CommandArgument> arguments = Lists.newArrayListWithExpectedSize(4);
        arguments.add(new MessageSpyListArgument(plugin));
        arguments.add(new IgnoreClearArgument(plugin));
        arguments.add(new MessageSpyAddArgument(plugin));
        arguments.add(new MessageSpyDeleteArgument(plugin));
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

    private static class MessageSpyDeleteArgument extends CommandArgument {

        private final BasePlugin plugin;

        public MessageSpyDeleteArgument(BasePlugin plugin) {
            super("delete", "Deletes a player from your message spy list.");
            this.plugin = plugin;
            this.aliases = new String[]{"del", "remove"};
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

            Player player = (Player) sender;
            UUID uuid = player.getUniqueId();
            BaseUser baseUser = plugin.getUserManager().getUser(uuid);

            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + "Usage: " + getUsage(label));
                return true;
            }

            Set<String> messageSpying = baseUser.getMessageSpying();
            if (args[1].equalsIgnoreCase("all")) {
                messageSpying.remove("all");
                sender.sendMessage(ChatColor.RED + "No longer spying on the messages of all players.");
                return true;
            }

            @SuppressWarnings("deprecation")
            OfflinePlayer offlineTarget = Bukkit.getOfflinePlayer(args[1]);

            if (!offlineTarget.hasPlayedBefore() && !offlineTarget.isOnline()) {
                sender.sendMessage(ChatColor.GOLD + "Player '" + ChatColor.WHITE + args[1] + ChatColor.GOLD + "' not found.");
                return true;
            }

            sender.sendMessage("You are " + (messageSpying.remove(offlineTarget.getUniqueId().toString()) ? ChatColor.GREEN + "now" : ChatColor.RED + "still not") +
                    ChatColor.YELLOW + " spying on the messages of " + offlineTarget.getName() + '.');

            return true;
        }

        @Override
        public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
            return args.length == 2 ? null : Collections.emptyList();
        }
    }

    private static class MessageSpyAddArgument extends CommandArgument {

        private final BasePlugin plugin;

        public MessageSpyAddArgument(BasePlugin plugin) {
            super("add", "Adds a player to your message spy list.");
            this.plugin = plugin;
        }

        @Override
        public String getUsage(String label) {
            return '/' + label + ' ' + getName() + " <all|playerName>";
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

            BaseUser baseUser = plugin.getUserManager().getUser(((Player) sender).getUniqueId());
            Set<String> messageSpying = baseUser.getMessageSpying();

            boolean all;
            if ((all = messageSpying.contains("all")) || JavaUtils.containsIgnoreCase(messageSpying, args[1])) {
                sender.sendMessage(ChatColor.RED + "You are already spying on the messages of " + (all ? "all players" : args[1]) + '.');
                return true;
            }

            if (args[1].equalsIgnoreCase("all")) {
                messageSpying.clear();
                messageSpying.add("all");
                sender.sendMessage(ChatColor.GREEN + "You are now spying on the messages of all players.");
                return true;
            }

            @SuppressWarnings("deprecation")
            OfflinePlayer offlineTarget = Bukkit.getServer().getOfflinePlayer(args[1]);

            if (!offlineTarget.hasPlayedBefore() && offlineTarget.getPlayer() == null) {
                sender.sendMessage(ChatColor.GOLD + "Player '" + ChatColor.WHITE + args[1] + ChatColor.GOLD + "' not found.");
                return true;
            }

            if (offlineTarget.equals(sender)) {
                sender.sendMessage(ChatColor.RED + "You cannot spy on the messages of yourself.");
                return true;
            }

            sender.sendMessage(ChatColor.YELLOW + "You are " + (messageSpying.add(offlineTarget.getUniqueId().toString()) ? ChatColor.GREEN + "now" : ChatColor.RED + "no longer") +
                    ChatColor.YELLOW + " spying on the messages of " + offlineTarget.getName() + ".");

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
            super("clear", "Clears your current spy list.");
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

            Set<String> messageSpying = plugin.getUserManager().getUser(((Player) sender).getUniqueId()).getMessageSpying();
            messageSpying.clear();
            sender.sendMessage(ChatColor.YELLOW + "You are no longer spying the messages of anyone.");
            return true;
        }
    }

    private static class MessageSpyListArgument extends CommandArgument {

        private final BasePlugin plugin;

        public MessageSpyListArgument(BasePlugin plugin) {
            super("list", "Lists all players you're spying on.");
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

            Player player = (Player) sender;
            UUID uuid = player.getUniqueId();
            BaseUser baseUser = plugin.getUserManager().getUser(uuid);

            Set<String> spyingNames = Sets.newHashSet();
            for (String spyingId : baseUser.getMessageSpying()) {
                OfflinePlayer target = Bukkit.getServer().getOfflinePlayer(UUID.fromString(spyingId));
                spyingNames.add(target.getName());
            }

            if (spyingNames.isEmpty()) {
                sender.sendMessage(ChatColor.RED + "You are not spying on the PM's of any players.");
                return true;
            }

            sender.sendMessage(ChatColor.GRAY + "You are currently spying on the PM's of (" + spyingNames.size() + "): " + ChatColor.RED +
                    StringUtils.join(spyingNames, ChatColor.GRAY.toString() + ", " + ChatColor.RED) + ChatColor.GRAY + '.');

            return true;
        }
    }
}
