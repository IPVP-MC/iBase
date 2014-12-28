package com.doctordark.base.cmd.module.chat.messaging;

import com.doctordark.base.BasePlugin;
import com.doctordark.base.cmd.BaseCommand;
import com.doctordark.base.cmd.module.chat.messaging.event.PlayerMessageEvent;
import com.doctordark.base.cmd.module.chat.messaging.event.PlayerPreMessageEvent;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class MessageSpyCommand extends BaseCommand {

    public MessageSpyCommand() {
        super("messagespy", "Spies on the private messages of a player.", "base.command.messagespy");
        this.setAliases(new String[]{"r", "respond"});
        this.setUsage("/(command) <list|add|del|clear> [playerName]");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can spy on factions!");
            return true;
        }

        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();

        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Usage: " + getUsage(label));
            return true;
        }

        BasePlugin plugin = getBasePlugin();
        List<String> currentSpies = plugin.getUserManager().getMessageSpyList(uuid);

        if (args[0].equalsIgnoreCase("list")) {
            if (currentSpies.isEmpty()) {
                sender.sendMessage(ChatColor.RED + "You are not spying on the messages of any players!");
                return true;
            }

            sender.sendMessage(ChatColor.GRAY + "You are currently spying on the messages of: " + ChatColor.RED +
                    StringUtils.join(currentSpies, ChatColor.GRAY + ", " + ChatColor.RED));
            return true;
        }

        if (args[0].equalsIgnoreCase("add")) {
            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + "Usage: /" + label + " " + args[0].toLowerCase() + " <playerName>");
                return true;
            }

            if (args[1].equalsIgnoreCase("all")) {
                for (Player target : Bukkit.getServer().getOnlinePlayers()) {
                    if (target.getName().equals(sender.getName())) continue;
                    if (currentSpies.contains(target.getName())) continue;

                    currentSpies.add(target.getName());
                }

                sender.sendMessage(ChatColor.GREEN + "You are now spying on the chat of all online players.");
                return true;
            }

            Player target = Bukkit.getServer().getPlayer(args[1]);

            if ((target == null) || (!canSee(sender, target))) {
                sender.sendMessage(ChatColor.GOLD + "Player '" + ChatColor.WHITE + args[1] + ChatColor.GOLD + "' not found!");
                return true;
            }

            if (target.getName().equals(sender.getName())) {
                sender.sendMessage(ChatColor.RED + "You cannot spy on the messages of yourself!");
                return true;
            }

            if (currentSpies.contains(target.getName())) {
                sender.sendMessage(ChatColor.RED + "You are already spying on the chat of " + target.getName() + "!");
                return true;
            }

            currentSpies.add(target.getName());
            sender.sendMessage(ChatColor.GREEN + "You are now spying on the chat of " + target.getName() + ".");
            return true;
        }

        if (args[1].equalsIgnoreCase("del") || args[1].equalsIgnoreCase("delete") || args[1].equalsIgnoreCase("remove")) {
            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + "Usage: /" + label + " " + args[0].toLowerCase() + " <playerName>");
                return true;
            }

            Player target = Bukkit.getServer().getPlayer(args[1]);

            if ((target == null) || (!canSee(sender, target))) {
                sender.sendMessage(ChatColor.GOLD + "Player '" + ChatColor.WHITE + args[1] + ChatColor.GOLD + "' not found!");
                return true;
            }

            if (!currentSpies.contains(target.getName())) {
                sender.sendMessage(ChatColor.RED + "You are still not spying on the chat of " + target.getName() + "!");
                return true;
            }

            currentSpies.remove(target.getName());
            sender.sendMessage(ChatColor.RED + "You are no longer spying on the chat of " + target.getName() + ".");
            return true;
        }

        if (args[0].equalsIgnoreCase("clear")) {
            currentSpies.clear();
            sender.sendMessage(ChatColor.YELLOW + "You are no longer spying on any private messages.");
            return true;
        }

        sender.sendMessage(ChatColor.RED + "Usage: " + getUsage(label));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            List<String> results = new ArrayList<String>();
            results.add("list");
            results.add("add");
            results.add("del");
            results.add("clear");
            return getCompletions(args, results);
        } else if ((args.length == 2) && (args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("del"))) {
            return null;
        } else {
            return Collections.emptyList();
        }
    }
}
