package com.doctordark.base.command.module.chat;

import com.doctordark.base.BasePlugin;
import com.doctordark.base.command.BaseCommand;
import com.doctordark.base.user.ServerParticipator;
import com.doctordark.util.BukkitUtils;
import com.doctordark.util.JavaUtils;
import com.doctordark.util.command.CommandArgument;
import com.doctordark.util.command.CommandWrapper;
import com.google.common.collect.Iterables;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class MessageSpyCommand extends BaseCommand {

    private final CommandWrapper handler;

    public MessageSpyCommand(final BasePlugin plugin) {
        super("messagespy", "Spies on the PM's of a player.");
        this.setAliases(new String[]{"ms", "msgspy", "pmspy", "whisperspy", "privatemessagespy", "tellspy"});
        this.setUsage("/(command) <list|add|del|clear> [playerName]");

        List<CommandArgument> arguments = new ArrayList<>(4);
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
            ServerParticipator participator = plugin.getUserManager().getParticipator(sender);

            if (participator == null) {
                sender.sendMessage(ChatColor.RED + "You are not able to message spy.");
                return true;
            }

            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + "Usage: " + getUsage(label));
                return true;
            }

            Set<String> messageSpying = participator.getMessageSpying();
            if (args[1].equalsIgnoreCase("all")) {
                messageSpying.remove("all");
                sender.sendMessage(ChatColor.RED + "You are no longer spying on the messages of all players.");
                return true;
            }

            @SuppressWarnings("deprecation")
            OfflinePlayer offlineTarget = BukkitUtils.offlinePlayerWithNameOrUUID(args[1]);

            if (!offlineTarget.hasPlayedBefore() && !offlineTarget.isOnline()) {
                sender.sendMessage(ChatColor.GOLD + "Player named or with UUID '" + ChatColor.WHITE + args[1] + ChatColor.GOLD + "' not found.");
                return true;
            }

            sender.sendMessage("You are " + (messageSpying.remove(offlineTarget.getUniqueId().toString()) ? ChatColor.GREEN + "no longer" : ChatColor.RED + "still not") +
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
            ServerParticipator participator = plugin.getUserManager().getParticipator(sender);

            if (participator == null) {
                sender.sendMessage(ChatColor.RED + "You are not able to message spy.");
                return true;
            }

            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + "Usage: " + getUsage(label));
                return true;
            }

            Set<String> messageSpying = participator.getMessageSpying();

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
            OfflinePlayer offlineTarget = Bukkit.getOfflinePlayer(args[1]); //TODO: breaking, can hang main thread, async

            if (!offlineTarget.hasPlayedBefore() && offlineTarget.getPlayer() == null) {
                sender.sendMessage(ChatColor.GOLD + "Player '" + ChatColor.WHITE + args[1] + ChatColor.GOLD + "' not found.");
                return true;
            }

            if (offlineTarget.equals(sender)) {
                sender.sendMessage(ChatColor.RED + "You cannot spy on the messages of yourself.");
                return true;
            }

            sender.sendMessage(ChatColor.YELLOW + "You are " + (messageSpying.add(offlineTarget.getUniqueId().toString()) ? ChatColor.GREEN + "now" : ChatColor.RED + "already") +
                    ChatColor.YELLOW + " spying on the messages of " + offlineTarget.getName() + '.');

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
            ServerParticipator participator = plugin.getUserManager().getParticipator(sender);

            if (participator == null) {
                sender.sendMessage(ChatColor.RED + "You are not able to message spy.");
                return true;
            }

            participator.getMessageSpying().clear();
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
            ServerParticipator participator = plugin.getUserManager().getParticipator(sender);

            if (participator == null) {
                sender.sendMessage(ChatColor.RED + "You are not able to message spy.");
                return true;
            }

            Set<String> spyingNames = new LinkedHashSet<>();
            Collection<String> messageSpying = participator.getMessageSpying();
            if (messageSpying.size() == 1 && Iterables.getOnlyElement(messageSpying).equals("all")) {
                sender.sendMessage(ChatColor.GRAY + "You are currently spying on the messages of all players.");
                return true;
            }

            for (String spyingId : messageSpying) {
                String name = Bukkit.getOfflinePlayer(UUID.fromString(spyingId)).getName(); //TODO: breaking, can hang main thread, async
                if (name == null) continue;
                spyingNames.add(name);
            }

            if (spyingNames.isEmpty()) {
                sender.sendMessage(ChatColor.RED + "You are not spying on the messages of any players.");
                return true;
            }

            sender.sendMessage(ChatColor.GRAY + "You are currently spying on the messages of (" + spyingNames.size() + " players): " + ChatColor.RED +
                    StringUtils.join(spyingNames, ChatColor.GRAY.toString() + ", " + ChatColor.RED) + ChatColor.GRAY + '.');

            return true;
        }
    }
}
