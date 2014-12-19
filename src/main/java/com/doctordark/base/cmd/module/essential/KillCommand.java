package com.doctordark.base.cmd.module.essential;

import com.doctordark.base.cmd.BaseCommand;
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
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player target;
        if (args.length < 1) {
            if (sender instanceof Player) {
                target = (Player)sender;
            } else {
                sender.sendMessage(ChatColor.RED + "Usage: " + getUsage());
                return true;
            }
        } else if (sender.hasPermission(getPermission() + ".others")) {
            target = Bukkit.getServer().getPlayer(args[0]);
        } else {
            target = (Player)sender;
        }

        if ((target == null) || (!canSee(sender, target))) {
            sender.sendMessage(ChatColor.GOLD + "Player '" + ChatColor.WHITE + args[0] + ChatColor.GOLD + "' not found!");
            return true;
        }

        EntityDamageEvent event = new EntityDamageEvent(target, DamageCause.SUICIDE, 10000);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            sender.sendMessage(ChatColor.RED + "You cannot kill " + target.getName() + "!");
            return true;
        }

        if (target.isDead()) {
            sender.sendMessage(ChatColor.RED + target.getName() + " is already dead!");
            return true;
        }

        target.setLastDamageCause(event);
        target.setHealth(0);

        if (sender.getName().equals(target.getName())) {
            sender.sendMessage(ChatColor.GOLD + "You have been killed.");
            return true;
        }

        Command.broadcastCommandMessage(sender, ChatColor.YELLOW + "Slain player " + target.getName() + ".");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        return (args.length == 1) ? null : Collections.<String>emptyList();
    }
}
