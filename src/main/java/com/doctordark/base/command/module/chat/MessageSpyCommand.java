package com.doctordark.base.command.module.chat;

import com.doctordark.base.BasePlugin;
import com.doctordark.base.command.BaseCommand;
import com.doctordark.base.command.CommandArgument;
import com.doctordark.base.command.CommandArgumentHandler;
import com.doctordark.base.user.BaseUser;
import com.doctordark.util.JavaUtils;
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
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class MessageSpyCommand extends BaseCommand {

    private final CommandArgumentHandler handler;

    public MessageSpyCommand(final BasePlugin plugin) {
        super("messagespy", "Spies on the PM's of a player.", "base.command.messagespy");
        setAliases(new String[]{"ms", "msgspy", "pmspy", "whisperspy", "privatemessagespy", "tellspy"});
        setUsage("/(command) <list|add|del|clear> [playerName]");

        List<CommandArgument> arguments = Lists.newArrayList();
        arguments.add(new CommandArgument("list", "Lists all players you're spying on.") {
            @Override
            public String[] getAliases() {
                return new String[0];
            }

            @Override
            public String getUsage(String label) {
                return "/" + label + " " + getName();
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
                        StringUtils.join(spyingNames, String.valueOf(ChatColor.GRAY) + ", " + ChatColor.RED) + ChatColor.GRAY + ".");
                return true;
            }

            @Override
            public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
                return Collections.emptyList();
            }
        });

        arguments.add(new CommandArgument("clear", "Clears all players you're spying on.") {
            @Override
            public String[] getAliases() {
                return new String[0];
            }

            @Override
            public String getUsage(String label) {
                return "/" + label + " " + getName();
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

                Set<String> messageSpying = baseUser.getMessageSpying();
                messageSpying.clear();

                sender.sendMessage(ChatColor.YELLOW + "You are no longer spying the PM's of anyone.");
                return true;
            }

            @Override
            public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
                return Collections.emptyList();
            }
        });

        arguments.add(new CommandArgument("add", "Adds a player to your message spy list.") {
            @Override
            public String[] getAliases() {
                return new String[0];
            }

            @Override
            public String getUsage(String label) {
                return "/" + label + " " + getName() + " <all|playerName>";
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
                Set<String> messageSpying = baseUser.getMessageSpying();

                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "Usage: /" + label + " " + args[0].toLowerCase() + " <del|playerName>");
                    return true;
                }

                if (messageSpying.contains("all") || JavaUtils.containsIgnoreCase(messageSpying, args[1])) {
                    sender.sendMessage(ChatColor.RED + "You are already spying on the PM's of " + (args[1].equalsIgnoreCase("all") ? "all players" : args[1]) + ".");
                    return true;
                }

                if (args[1].equalsIgnoreCase("all")) {
                    messageSpying.clear();
                    messageSpying.add("all");
                    sender.sendMessage(ChatColor.GREEN + "You are now spying on the PM's of all players.");
                    return true;
                }

                @SuppressWarnings("deprecation")
                OfflinePlayer offlineTarget = Bukkit.getServer().getOfflinePlayer(args[1]);

                if ((!offlineTarget.hasPlayedBefore()) && (offlineTarget.getPlayer() == null)) {
                    sender.sendMessage(ChatColor.GOLD + "Player '" + ChatColor.WHITE + args[1] + ChatColor.GOLD + "' not found.");
                    return true;
                }

                if (offlineTarget.equals(sender)) {
                    sender.sendMessage(ChatColor.RED + "You cannot spy on the messages of yourself.");
                    return true;
                }

                String name = offlineTarget.getName();
                String id = offlineTarget.getUniqueId().toString();

                if (messageSpying.contains(id)) {
                    sender.sendMessage(ChatColor.RED + "You are already spying on the PM's of " + name + ".");
                    return true;
                }

                messageSpying.add(id);

                sender.sendMessage(ChatColor.GREEN + "You are now spying on the PM's of " + name + ".");
                return true;
            }

            @Override
            public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
                return null;
            }
        });

        arguments.add(new CommandArgument("del", "Deletes a player from your message spy list.") {
            @Override
            public String[] getAliases() {
                return new String[]{"del", "remove"};
            }

            @Override
            public String getUsage(String label) {
                return "/" + label + " " + getName() + " <playerName>";
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
                Set<String> messageSpying = baseUser.getMessageSpying();

                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "Usage: /" + label + " " + args[0].toLowerCase() + " <playerName>");
                    return true;
                }

                if (args[1].equalsIgnoreCase("all")) {
                    messageSpying.remove("all");
                    sender.sendMessage(ChatColor.RED + "No longer spying on the PM's of all players.");
                    return true;
                }

                @SuppressWarnings("deprecation")
                OfflinePlayer offlineTarget = Bukkit.getServer().getOfflinePlayer(args[1]);

                if ((!offlineTarget.hasPlayedBefore()) && (!offlineTarget.isOnline())) {
                    sender.sendMessage(ChatColor.GOLD + "Player '" + ChatColor.WHITE + args[1] + ChatColor.GOLD + "' not found.");
                    return true;
                }

                String name = offlineTarget.getName();
                String id = offlineTarget.getUniqueId().toString();

                if (!messageSpying.contains(id)) {
                    sender.sendMessage(ChatColor.RED + "You are still not spying on the PM's of " + name + ".");
                    return true;
                }

                messageSpying.remove(id);

                sender.sendMessage(ChatColor.RED + "You are no longer spying on the PM's of " + name + ".");
                return true;
            }

            @Override
            public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
                return Collections.emptyList();
            }
        });

        Collections.sort(arguments, new Comparator<CommandArgument>() {
            @Override
            public int compare(CommandArgument primaryArgument, CommandArgument secondaryArgument) {
                return primaryArgument.getName().compareTo(secondaryArgument.getName());
            }
        });

        this.handler = new CommandArgumentHandler(arguments);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return handler.onCommand(sender, command, label, args);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return handler.onTabComplete(sender, command, label, args);
    }
}
