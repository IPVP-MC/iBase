package com.doctordark.base.cmd.module.chat.messaging;

import com.doctordark.base.BasePlugin;
import com.doctordark.base.cmd.BaseCommand;
import com.doctordark.base.util.BaseUtil;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class MessageSpyCommand extends BaseCommand {

    private final BasePlugin plugin;

    public MessageSpyCommand(BasePlugin plugin) {
        super("messagespy", "Spies on the private messages of a player.", "base.command.messagespy");
        this.setAliases(new String[]{"msspy", "pmspy"});
        this.setUsage("/(command) <list|add|del|clear> [playerName]");
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can spy on PM chat.");
            return true;
        }

        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();

        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Usage: " + getUsage(label));
            return true;
        }

        List<String> currentSpies = plugin.getUserManager().getMessageSpying(uuid);

        if (args[0].equalsIgnoreCase("list")) {
            if (currentSpies.isEmpty()) {
                sender.sendMessage(ChatColor.RED + "You are not spying on the PM's of any players.");
                return true;
            }

            sender.sendMessage(ChatColor.GRAY + "You are currently spying on the PM's of: " + ChatColor.RED +
                    StringUtils.join(currentSpies, ChatColor.GRAY + ", " + ChatColor.RED) + ChatColor.GRAY + ".");
            return true;
        }

        if (args[0].equalsIgnoreCase("add")) {
            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + "Usage: /" + label + " " + args[0].toLowerCase() + " <playerName>");
                return true;
            }

            if (BaseUtil.containsIgnoreCase(currentSpies, args[1])) {
                sender.sendMessage(ChatColor.RED + "You are already spying on the PM's of " + (args[1].equalsIgnoreCase("all") ? "all players" : args[1]) + ".");
                return true;
            }

            if (args[1].equalsIgnoreCase("all")) {
                currentSpies.add("all");
                sender.sendMessage(ChatColor.GREEN + "You are now spying on the PM's of all players.");
                return true;
            }

            OfflinePlayer offlineTarget = Bukkit.getServer().getOfflinePlayer(args[1]);

            if (!offlineTarget.hasPlayedBefore()) {
                sender.sendMessage(ChatColor.GOLD + "Player '" + ChatColor.WHITE + args[1] + ChatColor.GOLD + "' not found.");
                return true;
            }

            if (offlineTarget.getName().equals(sender.getName())) {
                sender.sendMessage(ChatColor.RED + "You cannot spy on the messages of yourself.");
                return true;
            }

            if (currentSpies.contains(offlineTarget.getName())) {
                sender.sendMessage(ChatColor.RED + "You are already spying on the PM's of " + offlineTarget.getName() + ".");
                return true;
            }

            currentSpies.add(offlineTarget.getName());

            sender.sendMessage(ChatColor.GREEN + "You are now spying on the PM's of " + offlineTarget.getName() + ".");
            return true;
        }

        if (args[0].equalsIgnoreCase("del") || args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("remove")) {
            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + "Usage: /" + label + " " + args[0].toLowerCase() + " <playerName>");
                return true;
            }

            if (args[1].equalsIgnoreCase("all")) {
                currentSpies.remove("all");
                sender.sendMessage(ChatColor.RED + "No longer spying on the PM's of all players.");
                return true;
            }

            OfflinePlayer offlineTarget = Bukkit.getServer().getOfflinePlayer(args[1]);

            if (!offlineTarget.hasPlayedBefore()) {
                sender.sendMessage(ChatColor.GOLD + "Player '" + ChatColor.WHITE + args[1] + ChatColor.GOLD + "' not found.");
                return true;
            }

            if (!currentSpies.contains(offlineTarget.getName())) {
                sender.sendMessage(ChatColor.RED + "You are still not spying on the PM's of " + offlineTarget.getName() + ".");
                return true;
            }

            currentSpies.remove(offlineTarget.getName());

            sender.sendMessage(ChatColor.RED + "You are no longer spying on the PM's of " + offlineTarget.getName() + ".");
            return true;
        }

        if (args[0].equalsIgnoreCase("clear")) {
            currentSpies.clear();
            sender.sendMessage(ChatColor.YELLOW + "You are no longer spying the private messages of anyone.");
            return true;
        }

        sender.sendMessage(ChatColor.RED + "Usage: " + getUsage(label));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return Collections.emptyList();
        }

        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();
        List<String> results = new ArrayList<String>();

        if (args.length == 1) {
            results.add("list");
            results.add("add");
            results.add("del");
            results.add("clear");
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("add")) {
                results.add("all");
            } else if (args[0].equalsIgnoreCase("del") || args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("remove")) {
                results.add("all");
                List<String> currentSpies = plugin.getUserManager().getMessageSpying(uuid);
                results.addAll(currentSpies);
            }
        }

        return getCompletions(args, results);
    }
}
