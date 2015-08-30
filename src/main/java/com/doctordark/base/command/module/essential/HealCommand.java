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
import java.util.Set;

/**
 * Command used for healing of players.
 */
public class HealCommand extends BaseCommand {

    private static final Set<PotionEffectType> HEALING_REMOVEABLE_POTION_EFFECTS = ImmutableSet.of(
            PotionEffectType.SLOW,
            PotionEffectType.SLOW_DIGGING,
            PotionEffectType.POISON,
            PotionEffectType.WEAKNESS
    );

    public HealCommand() {
        super("heal", "Heals a player.");
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

        double maxHealth;

        if (onlyTarget != null && (maxHealth = onlyTarget.getHealth()) == onlyTarget.getMaxHealth()) {
            sender.sendMessage(ChatColor.RED + onlyTarget.getName() + " already has full health (" + maxHealth + ").");
            return true;
        }

        for (Player target : targets) {
            target.setHealth(target.getMaxHealth());
            for (PotionEffectType type : HEALING_REMOVEABLE_POTION_EFFECTS) {
                target.removePotionEffect(type);
            }
        }

        Command.broadcastCommandMessage(sender, ChatColor.YELLOW + "Healed " + (onlyTarget == null ? "all online players" : "player " + onlyTarget.getName()) + '.');
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return args.length == 1 ? null : Collections.<String>emptyList();
    }
}