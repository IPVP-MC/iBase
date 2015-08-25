package com.doctordark.base.command.module.essential;

import com.doctordark.base.BaseConstants;
import com.doctordark.base.command.BaseCommand;
import com.doctordark.util.BukkitUtils;
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
        super("kill", "Kills a player.");
        this.setAliases(new String[]{"slay"});
        this.setUsage("/(command) <playerName>");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        final Player target;
        if (args.length > 0 && sender.hasPermission(command.getPermission() + ".others")) {
            target = BukkitUtils.playerWithNameOrUUID(args[0]);
        } else if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Usage: " + getUsage(label));
            return true;
        } else {
            target = (Player) sender;
        }

        if (target == null || !canSee(sender, target)) {
            sender.sendMessage(String.format(BaseConstants.PLAYER_WITH_NAME_OR_UUID_NOT_FOUND, args[0]));
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
