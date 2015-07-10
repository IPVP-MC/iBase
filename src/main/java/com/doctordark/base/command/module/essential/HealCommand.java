package com.doctordark.base.command.module.essential;

import com.doctordark.base.command.BaseCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.Collections;
import java.util.List;

/**
 * Command used for healing of players.
 */
public class HealCommand extends BaseCommand {

    public HealCommand() {
        super("heal", "Heals a player.", "base.command.heal");
        this.setUsage("/(command) <playerName>");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        final Player target;
        if (args.length > 0 && sender.hasPermission(command.getPermission() + ".others")) {
            target = Bukkit.getServer().getPlayer(args[0]);
        } else if (args.length > 0 && args[0].equalsIgnoreCase("all") && sender.hasPermission(command.getPermission() + ".all")) {
            for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                if (player.hasPotionEffect(PotionEffectType.HUNGER)) {
                    player.removePotionEffect(PotionEffectType.HUNGER);
                }

                player.setHealth(player.getMaxHealth());
            }

            Command.broadcastCommandMessage(sender, ChatColor.YELLOW + "Healed all online players.");
            return true;
        } else if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Usage: " + getUsage(label));
            return true;
        } else {
            target = (Player) sender;
        }

        if (target == null || !canSee(sender, target)) {
            sender.sendMessage(ChatColor.GOLD + "Player '" + ChatColor.WHITE + args[0] + ChatColor.GOLD + "' not found.");
            return true;
        }

        double maxHealth = target.getMaxHealth();

        if (target.getHealth() == maxHealth) {
            sender.sendMessage(ChatColor.RED + target.getName() + " already has full health.");
            return true;
        }

        target.setHealth(maxHealth);

        Command.broadcastCommandMessage(sender, ChatColor.YELLOW + "Healed player " + target.getName() + '.');
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return (args.length == 1) ? null : Collections.<String>emptyList();
    }
}