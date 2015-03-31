package com.doctordark.base.command.module.chat;

import com.doctordark.base.BasePlugin;
import com.doctordark.base.command.BaseCommand;
import com.doctordark.base.command.CommandArgument;
import com.doctordark.base.command.CommandArgumentHandler;
import com.doctordark.base.user.BaseUser;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class IgnoreCommand extends BaseCommand {

    private final CommandArgumentHandler handler;

    public IgnoreCommand(final BasePlugin plugin) {
        super("ignore", "Ignores a player from messages.", "base.command.ignore");
        setAliases(new String[0]);
        setUsage("/(command) <list|add|del|clear> [playerName]");

        List<CommandArgument> arguments = Lists.newArrayList();
        arguments.add(new CommandArgument("clear", "Clears all ignored players.") {
            @Override public String[] getAliases() {
                return new String[0];
            }

            @Override public String getUsage(String label) {
                return "/" + label + " " + getName();
            }

            @Override public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
                    return true;
                }

                Player player = (Player) sender;
                UUID uuid = player.getUniqueId();
                BaseUser baseUser = plugin.getUserManager().getUser(uuid);
                Set<String> ignoring = baseUser.getIgnoring();
                if (ignoring.isEmpty()) {
                    sender.sendMessage(ChatColor.RED + "Your ignore list is already empty.");
                    return true;
                }
                ignoring.clear();
                sender.sendMessage(ChatColor.YELLOW + "Your ignore list has been cleared.");
                return true;
            }

            @Override public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
                return Collections.emptyList();
            }
        });

        arguments.add(new CommandArgument("list", "Lists all ignored players.") {
            @Override public String[] getAliases() {
                return new String[0];
            }

            @Override public String getUsage(String label) {
                return "/" + label + " " + getName();
            }

            @Override public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
                    return true;
                }

                Player player = (Player) sender;
                UUID uuid = player.getUniqueId();
                BaseUser baseUser = plugin.getUserManager().getUser(uuid);
                Set<String> ignoring = baseUser.getIgnoring();

                if (ignoring.isEmpty()) {
                    sender.sendMessage(ChatColor.YELLOW + "You are not ignoring anyone.");
                    return true;
                }

                sender.sendMessage(ChatColor.YELLOW + "You are ignoring (" + ignoring.size() + "): [" + StringUtils.join(ignoring, ", ") + "]");
                return true;
            }

            @Override public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
                return Collections.emptyList();
            }
        });

        arguments.add(new CommandArgument("add", "Ignores a player.") {
            @Override public String[] getAliases() {
                return new String[]{"set"};
            }

            @Override public String getUsage(String label) {
                return "/" + label + " " + getName() + " <playerName>";
            }

            @Override public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
                    return true;
                }

                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "Usage: /" + label + " " + args[0].toLowerCase() + " <playerName>");
                    return true;
                }

                Player player = (Player) sender;
                UUID uuid = player.getUniqueId();
                BaseUser baseUser = plugin.getUserManager().getUser(uuid);
                Set<String> ignoring = baseUser.getIgnoring();

                Player target = Bukkit.getServer().getPlayer(args[1]);

                if ((target == null) || (!canSee(sender, target))) {
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
                if (ignoring.contains(targetName)) {
                    sender.sendMessage(ChatColor.RED + "You are already ignoring someone named " + targetName + ".");
                } else {
                    sender.sendMessage(ChatColor.GOLD + "You are now ignoring " + targetName + ".");
                    ignoring.add(targetName);
                }

                return true;
            }

            public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
                return null;
            }
        });

        arguments.add(new CommandArgument("del", "Un-ignores a player.") {
            @Override public String[] getAliases() {
                return new String[]{"delete", "remove", "unset"};
            }

            @Override public String getUsage(String label) {
                return "/" + label + " " + getName() + " <playerName>";
            }

            @Override public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
                    return true;
                }

                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "Usage: /" + label + " " + args[0].toLowerCase() + " <playerName>");
                    return true;
                }

                Player player = (Player) sender;
                UUID uuid = player.getUniqueId();
                BaseUser baseUser = plugin.getUserManager().getUser(uuid);

                Set<String> ignoring = baseUser.getIgnoring();
                if (!ignoring.contains(args[1])) {
                    sender.sendMessage(ChatColor.RED + "You are not ignoring anyone named " + args[1] + ".");
                } else {
                    ignoring.remove(args[1]);
                    sender.sendMessage(ChatColor.GOLD + "You are no longer ignoring " + args[1] + ".");
                }

                return true;
            }

            public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
                return null;
            }
        });

        Collections.sort(arguments, new Comparator<CommandArgument>() {
            @Override
            public int compare(CommandArgument primaryArgument, CommandArgument secondaryArgument) {
                return secondaryArgument.getName().compareTo(primaryArgument.getName());
            }
        });

        handler = new CommandArgumentHandler(arguments);
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
