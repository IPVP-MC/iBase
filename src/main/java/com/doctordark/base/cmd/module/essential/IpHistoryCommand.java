package com.doctordark.base.cmd.module.essential;

import com.doctordark.base.BasePlugin;
import com.doctordark.base.cmd.BaseCommand;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import sun.net.util.IPAddressUtil;

import java.util.*;

public class IpHistoryCommand extends BaseCommand implements Listener {

    private final BasePlugin plugin;

    public IpHistoryCommand(BasePlugin plugin) {
        super("iphistory", "Checks data about IP addresses or players.", "base.command.ipcheck");
        this.setAliases(new String[]{});
        this.setUsage("/(command) <player|address>");

        this.plugin = plugin;
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        String ip = player.getAddress().getHostString();
        plugin.getUserManager().saveAddress(uuid, ip);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Usage: " + getUsage(label));
            return true;
        }

        if (args[0].equalsIgnoreCase("player")) {
            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + "Usage: /" + label + " " + args[0].toLowerCase() + " <playerName>");
                return true;
            }

            OfflinePlayer target = Bukkit.getServer().getOfflinePlayer(args[1]);

            if (!target.hasPlayedBefore()) {
                sender.sendMessage(ChatColor.GOLD + "Player '" + ChatColor.WHITE + args[1] + ChatColor.GOLD + "' not found.");
                return true;
            }

            sender.sendMessage(ChatColor.GOLD + " IP Addresses used by " + target.getName() + ": ");
            Collection<String> ipList = plugin.getUserManager().getAddresses(target.getUniqueId());

            for (String address : ipList) {
                sender.sendMessage(ChatColor.GRAY + "  " + address);
            }

            return true;
        }

        if (args[0].equalsIgnoreCase("address")) {
            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + "Usage: /" + label + " " + args[0].toLowerCase() + " <address>");
                return true;
            }

            Set<String> sharingNames = new HashSet<String>();
            boolean isIp = IPAddressUtil.isIPv4LiteralAddress(args[1]);

            if (!isIp) {
                sender.sendMessage(ChatColor.RED + "'" + args[1] + "' is not a valid IPv4 address.");
                return true;
            }

            Map<String, List<String>> ipHistories = plugin.getUserManager().getAddressMap();
            for (String id : ipHistories.keySet()) {
                List<String> addresses = ipHistories.get(id);

                if (addresses.contains(args[1])) {
                    UUID uuid = UUID.fromString(id);
                    OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
                    sharingNames.add(player.getName());
                }
            }

            if (sharingNames.isEmpty()) {
                sender.sendMessage(ChatColor.RED + "No players share the ip '" + args[1] + "'.");
                return true;
            }

            sender.sendMessage(ChatColor.YELLOW + "IP address: " + args[1] + " is shared by: " + ChatColor.GOLD + StringUtils.join(sharingNames, ", "));
            return true;
        }

        sender.sendMessage(ChatColor.RED + "Usage: " + getUsage(label));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("player", "address");
        } else if (args.length == 2 && args[0].equalsIgnoreCase("player")) {
            return null;
        } else {
            return Collections.emptyList();
        }
    }
}
