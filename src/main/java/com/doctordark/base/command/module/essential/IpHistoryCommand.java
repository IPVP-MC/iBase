package com.doctordark.base.command.module.essential;

import com.doctordark.base.BasePlugin;
import com.doctordark.base.command.BaseCommand;
import com.doctordark.base.user.BaseUser;
import com.doctordark.base.user.ServerParticipator;
import com.doctordark.util.BukkitUtils;
import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import sun.net.util.IPAddressUtil;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class IpHistoryCommand extends BaseCommand {

    private final BasePlugin plugin;

    public IpHistoryCommand(BasePlugin plugin) {
        super("iphistory", "Checks data about IP addresses or players.");
        setUsage("/(command) <player|address>");
        this.plugin = plugin;
    }

    /**
     * @deprecated use Mojang API instead in an async task.
     */
    @Deprecated
    private Set<String> getSharingPlayerNames(String ipAddress) {
        //TODO: Use Mojang API instead
        Set<String> sharingNames = new HashSet<>();
        for (ServerParticipator participator : plugin.getUserManager().getParticipators().values()) {
            if (participator instanceof BaseUser) {
                BaseUser baseUser = (BaseUser) participator;
                if (baseUser.getAddressHistories().contains(ipAddress)) {
                    UUID uuid = baseUser.getUniqueId();
                    OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
                    String playerName = player.getName();
                    if (playerName != null) {
                        sharingNames.add(playerName);
                    }
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
                sender.sendMessage(ChatColor.RED + "Usage: /" + label + ' ' + args[0].toLowerCase() + " <playerName>");
                return true;
            }

            @SuppressWarnings("deprecation")
            OfflinePlayer target = BukkitUtils.offlinePlayerWithNameOrUUID(args[1]);

            if (!target.hasPlayedBefore() && target.getPlayer() == null) {
                sender.sendMessage(ChatColor.GOLD + "Player named or with UUID '" + ChatColor.WHITE + args[1] + ChatColor.GOLD + "' not found.");
                return true;
            }

            BaseUser baseUser = plugin.getUserManager().getUser(target.getUniqueId());
            sender.sendMessage(ChatColor.GOLD + " IP Addresses used by " + target.getName() + ": ");
            Collection<String> ipList = baseUser.getAddressHistories();

            for (String address : ipList) {
                sender.sendMessage(ChatColor.GRAY + "  " + address + ChatColor.YELLOW + ": [" + StringUtils.join(getSharingPlayerNames(address), ", ") + ']');
            }

            return true;
        }

        if (args[0].equalsIgnoreCase("address")) {
            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + "Usage: /" + label + ' ' + args[0].toLowerCase() + " <address>");
                return true;
            }

            if (!IPAddressUtil.isIPv4LiteralAddress(args[1])) {
                sender.sendMessage(ChatColor.RED + "'" + args[1] + "' is not a valid IPv4 address.");
                return true;
            }

            Set<String> sharingNames = getSharingPlayerNames(args[1]);

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
            return COMPLETIONS_FIRST;
        } else if (args.length == 2 && args[0].equalsIgnoreCase("player")) {
            return null;
        } else {
            return Collections.emptyList();
        }
    }

    private static final List<String> COMPLETIONS_FIRST = ImmutableList.of("player", "address");
}
