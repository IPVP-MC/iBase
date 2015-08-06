package com.doctordark.base.command.module.essential;

import com.doctordark.base.BaseConstants;
import com.doctordark.base.BasePlugin;
import com.doctordark.base.command.BaseCommand;
import com.doctordark.util.BukkitUtils;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

/**
 * Command used to track the play-time of a {@link Player}.
 */
public class PlayTimeCommand extends BaseCommand {

    private final BasePlugin plugin;

    public PlayTimeCommand(BasePlugin plugin) {
        super("playtime", "Check the playtime of another player.", "base.command.playtime");
        this.setAliases(new String[]{"pt", "bb"});
        this.setUsage("/(command) [playerName]");
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        final OfflinePlayer target;
        if (args.length >= 1) {
            target = BukkitUtils.offlinePlayerWithNameOrUUID(args[0]);
        } else if (sender instanceof Player) {
            target = (Player) sender;
        } else {
            sender.sendMessage(ChatColor.RED + "Usage: /" + label + " [playerName]");
            return true;
        }

        Player onlineTarget;
        if ((!target.hasPlayedBefore() || !target.isOnline()) || (sender instanceof Player && (onlineTarget = target.getPlayer()) != null && !((Player) sender).canSee(onlineTarget))) {
            sender.sendMessage(String.format(BaseConstants.PLAYER_WITH_NAME_OR_UUID_NOT_FOUND, args[0]));
            return true;
        }

        sender.sendMessage(ChatColor.YELLOW + target.getName() + " has been playing for " + ChatColor.GREEN +
                DurationFormatUtils.formatDurationWords(plugin.getPlayTimeManager().getTotalPlayTime(target.getUniqueId()), true, true) + ChatColor.YELLOW + " this map.");

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return (args.length == 1) ? null : Collections.<String>emptyList();
    }
}