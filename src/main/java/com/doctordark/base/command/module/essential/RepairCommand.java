package com.doctordark.base.command.module.essential;

import com.doctordark.base.command.BaseCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Command used for flight toggling of players.
 */
public class RepairCommand extends BaseCommand {

    public RepairCommand() {
        super("repair", "Allows repairing of damaged tools for a player.", "base.command.repair");
        this.setUsage("/(command) <playerName> [all]");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        final Player target;
        if (args.length > 0) {
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

        Set<ItemStack> toRepair = new HashSet<>();
        if (args.length >= 2 && args[1].equalsIgnoreCase("all")) {
            PlayerInventory targetInventory = target.getInventory();
            toRepair.addAll(Arrays.asList(targetInventory.getContents()));
            toRepair.addAll(Arrays.asList(targetInventory.getArmorContents()));
        } else {
            toRepair.add(target.getItemInHand());
        }

        toRepair.stream().filter(stack -> stack != null && stack.getType() != Material.AIR).forEach(stack -> {
            stack.setDurability((short) 0);
        });

        Command.broadcastCommandMessage(sender, ChatColor.YELLOW + "Repaired " + (toRepair.size() > 1 ? "inventory" : "held item") + " of " + target.getName() + '.');
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return (args.length == 1) ? null : Collections.<String>emptyList();
    }
}
