package com.doctordark.base.command.module.essential;

import com.doctordark.base.BaseConstants;
import com.doctordark.base.command.BaseCommand;
import com.doctordark.util.BukkitUtils;
import com.google.common.collect.ImmutableSet;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Command used for feeding of players.
 */
public class FeedCommand extends BaseCommand {

    private static final int MAX_HUNGER = 20;

    public FeedCommand() {
        super("feed", "Feeds a player.");
        this.setUsage("/(command) <playerName>");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player onlyTarget = null;
        final Collection<Player> targets;
        if (args.length > 0 && sender.hasPermission(command.getPermission() + ".others")) {
            if (args[0].equalsIgnoreCase("all") && sender.hasPermission(command.getPermission() + ".all")) {
                targets = ImmutableSet.copyOf(Bukkit.getOnlinePlayers());
            } else {
                if ((onlyTarget = BukkitUtils.playerWithNameOrUUID(args[0])) == null || !canSee(sender, onlyTarget)) {
                    sender.sendMessage(String.format(BaseConstants.PLAYER_WITH_NAME_OR_UUID_NOT_FOUND, args[0]));
                    return true;
                }

                targets = ImmutableSet.of(onlyTarget);
            }
        } else if (sender instanceof Player) {
            targets = ImmutableSet.of((Player) sender);
        } else {
            sender.sendMessage(ChatColor.RED + "Usage: " + getUsage(label));
            return true;
        }

        if (onlyTarget != null && onlyTarget.getFoodLevel() == MAX_HUNGER) {
            sender.sendMessage(ChatColor.RED + onlyTarget.getName() + " already has full hunger.");
            return true;
        }

        for (Player target : targets) {
            target.removePotionEffect(PotionEffectType.HUNGER);
            target.setFoodLevel(MAX_HUNGER);
        }

        Command.broadcastCommandMessage(sender, ChatColor.YELLOW + "Fed " + (onlyTarget == null ? "all online players" : "player " + onlyTarget.getName()) + '.');
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return args.length == 1 ? null : Collections.<String>emptyList();
    }
}