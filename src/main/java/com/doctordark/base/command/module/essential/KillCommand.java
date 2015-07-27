package com.doctordark.base.command.module.essential;

import com.doctordark.base.command.BaseCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import java.util.Collections;
import java.util.List;

/**
 * Command used to slay players out.
 */
public class KillCommand extends BaseCommand {

    public KillCommand() {
        super("kill", "Kills a player.", "base.command.kill");
        this.setAliases(new String[]{"slay"});
        this.setUsage("/(command) <playerName>");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        final Player target;
        if (args.length > 0 && sender.hasPermission(command.getPermission() + ".others")) {
            target = Bukkit.getPlayer(args[0]);
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

        if (target.isDead()) {
            sender.sendMessage(ChatColor.RED + target.getName() + " is already dead.");
            return true;
        }

        @SuppressWarnings("deprecation")
        EntityDamageEvent event = new EntityDamageEvent(target, DamageCause.SUICIDE, 10000);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            sender.sendMessage(ChatColor.RED + "You cannot kill " + target.getName() + '.');
            return true;
        }

        target.setLastDamageCause(event);
        target.setHealth(0);

        if (sender.equals(target)) {
            sender.sendMessage(ChatColor.GOLD + "You have been killed.");
            return true;
        }

        Command.broadcastCommandMessage(sender, ChatColor.YELLOW + "Slain player " + target.getName() + '.');
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return (args.length == 1 && sender.hasPermission(command.getPermission() + ".others")) ? null : Collections.<String>emptyList();
    }
}
