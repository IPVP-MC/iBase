package com.doctordark.base.command.module.essential;

import com.doctordark.base.BasePlugin;
import com.doctordark.base.command.BaseCommand;
import me.confuser.banmanager.BanManager;
import me.confuser.banmanager.bukkitutil.Message;
import me.confuser.banmanager.data.PlayerData;
import me.confuser.banmanager.util.IPUtils;
import me.confuser.banmanager.util.UUIDUtils;
import org.apache.commons.lang.time.FastDateFormat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.SQLException;
import java.util.Locale;

public class SeenCommand extends BaseCommand {

    private final FastDateFormat format;
    private final BasePlugin plugin;

    public SeenCommand(BasePlugin plugin) {
        super("seen", "Check when a player was last seen.", "base.command.seen");
        setUsage("/(command)");
        this.plugin = plugin;
        this.format = FastDateFormat.getInstance("dd/MM/yyyy HH:mm", Locale.ENGLISH);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        final OfflinePlayer target;
        if (args.length > 0 && sender.hasPermission(command.getPermission() + ".others")) {
            target = Bukkit.getOfflinePlayer(args[0]);
        } else if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Usage: " + getUsage(label));
            return true;
        } else {
            target = (Player) sender;
        }

        if (!target.hasPlayedBefore()) {
            sender.sendMessage(ChatColor.GOLD + "Player '" + ChatColor.WHITE + args[0] + ChatColor.GOLD + "' not found.");
            return true;
        }

        if (target.isOnline()) {
            sender.sendMessage(ChatColor.GREEN + target.getName() + " is online now.");
            return true;
        }

        Plugin banManagerPlugin = plugin.getServer().getPluginManager().getPlugin("BanManager");
        BanManager banManager = (BanManager) banManagerPlugin;

        new BukkitRunnable() {
            @Override
            public void run() {
                PlayerData playerData;
                try {
                    playerData = banManager.getPlayerStorage().queryForId(UUIDUtils.toBytes(target.getUniqueId()));
                } catch (SQLException ex) {
                    sender.sendMessage(Message.get("sender.error.exception").toString());
                    return;
                }

                if (playerData == null) {
                    sender.sendMessage(ChatColor.GOLD + "Player '" + ChatColor.WHITE + args[0] + ChatColor.GOLD + "' not found.");
                    return;
                }

                String lastSeen = format.format(playerData.getLastSeen() * 1000L);
                sender.sendMessage(ChatColor.GOLD + target.getName() + ChatColor.GREEN + " was last seen at " + ChatColor.GOLD + lastSeen +
                        ChatColor.GREEN + (sender.hasPermission(command.getPermission() + ".ip") ? " using the address " +
                        ChatColor.GOLD + IPUtils.toString(playerData.getIp()) + ChatColor.GREEN : "") + '.');
            }
        }.runTaskAsynchronously(plugin);
        return true;
    }
}
