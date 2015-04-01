package com.doctordark.base.command.module.essential;

import com.doctordark.base.BasePlugin;
import com.doctordark.base.command.BaseCommand;
import com.doctordark.base.user.BaseUser;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import sun.net.util.IPAddressUtil;

import java.util.*;

public class IpHistoryCommand extends BaseCommand {

    private final BasePlugin plugin;

    public IpHistoryCommand(BasePlugin plugin) {
        super("iphistory", "Checks data about IP addresses or players.", "base.command.iphistory");
        setAliases(new String[0]);
        setUsage("/(command) <player|address>");
        this.plugin = plugin;
    }

    private List<String> getSharingPlayerNames(String ipAddress) {
        final List<String> sharingNames = Lists.newArrayList();
        for (BaseUser baseUser : plugin.getUserManager().getUsers().values()) {
            if (baseUser.getAddressHistories().contains(ipAddress)) {
                UUID uuid = baseUser.getUserUUID();
                OfflinePlayer player = Bukkit.getServer().getOfflinePlayer(uuid);
                String playerName = player.getName();
                if (playerName != null) {
                    sharingNames.add(playerName);
                }
            }
        }

        return sharingNames;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Usage: " + getUsage());
            return true;
        }

        if (args[0].equalsIgnoreCase("player")) {
            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + "Usage: /" + label + " " + args[0].toLowerCase() + " <playerName>");
                return true;
            }

            @SuppressWarnings("deprecation")
            OfflinePlayer target = Bukkit.getServer().getOfflinePlayer(args[1]);

            if (!target.hasPlayedBefore() && target.getPlayer() == null) {
                sender.sendMessage(ChatColor.GOLD + "Player '" + ChatColor.WHITE + args[1] + ChatColor.GOLD + "' not found.");
                return true;
            }

            BaseUser baseUser = plugin.getUserManager().getUser(target.getUniqueId());
            sender.sendMessage(ChatColor.GOLD + " IP Addresses used by " + target.getName() + ": ");
            Collection<String> ipList = baseUser.getAddressHistories();

            for (String address : ipList) {
                sender.sendMessage(ChatColor.GRAY + "  " + address + ChatColor.YELLOW + ": [" + StringUtils.join(getSharingPlayerNames(address), ", ") + "]");
            }

            return true;
        }

        if (args[0].equalsIgnoreCase("address")) {
            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + "Usage: /" + label + " " + args[0].toLowerCase() + " <address>");
                return true;
            }

            if (!IPAddressUtil.isIPv4LiteralAddress(args[1])) {
                sender.sendMessage(ChatColor.RED + "'" + args[1] + "' is not a valid IPv4 address.");
                return true;
            }

            List<String> sharingNames = getSharingPlayerNames(args[1]);

            if (sharingNames.isEmpty()) {
                sender.sendMessage(ChatColor.RED + "No players share the ip '" + args[1] + "'.");
                return true;
            }

            sender.sendMessage(ChatColor.YELLOW + "IP address: " + args[1] + " is shared by: " + ChatColor.GOLD + StringUtils.join(sharingNames, ", "));
            return true;
        }

        sender.sendMessage(ChatColor.RED + "Usage: " + getUsage());
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("player", "address");
        } else if ((args.length == 2) && (args[0].equalsIgnoreCase("player"))) {
            return null;
        } else {
            return Collections.emptyList();
        }
    }
}
