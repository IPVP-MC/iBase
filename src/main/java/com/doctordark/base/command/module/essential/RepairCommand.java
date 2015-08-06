package com.doctordark.base.command.module.essential;

import com.doctordark.base.BaseConstants;
import com.doctordark.base.command.BaseCommand;
import com.doctordark.util.BukkitUtils;
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
 * Command used for repairing items of players.
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
