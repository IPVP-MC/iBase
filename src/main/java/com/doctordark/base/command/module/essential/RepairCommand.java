package com.doctordark.base.command.module.essential;

import com.doctordark.base.BaseConstants;
import com.doctordark.base.command.BaseCommand;
import com.doctordark.util.BukkitUtils;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
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

/**
 * Command used for repairing items of players.
 */
public class RepairCommand extends BaseCommand {

    private static final short FULLY_REPAIRED_DURABILITY = (short) 0;

    public RepairCommand() {
        super("repair", "Allows repairing of damaged tools for a player.");
        this.setUsage("/(command) <playerName> [all]");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        final Player target;
        if (args.length > 0) {
            target = BukkitUtils.playerWithNameOrUUID(args[0]);
        } else if (sender instanceof Player) {
            target = (Player) sender;
        } else {
            sender.sendMessage(ChatColor.RED + "Usage: " + getUsage(label));
            return true;
        }

        if (target == null || !canSee(sender, target)) {
            sender.sendMessage(String.format(BaseConstants.PLAYER_WITH_NAME_OR_UUID_NOT_FOUND, args[0]));
            return true;
        }

        final Iterable<ItemStack> toRepair;
        if (args.length > 1 && args[1].equalsIgnoreCase("all")) {
            PlayerInventory targetInventory = target.getInventory();
            toRepair = Iterables.concat(Arrays.asList(targetInventory.getContents()), Arrays.asList(targetInventory.getArmorContents()));
        } else {
            toRepair = ImmutableSet.of(target.getItemInHand());
        }

        for (ItemStack stack : toRepair) {
            if (stack != null && stack.getType() != Material.AIR) {
                stack.setDurability(FULLY_REPAIRED_DURABILITY);
            }
        }

        Command.broadcastCommandMessage(sender, ChatColor.YELLOW + "Repaired " + (toRepair.size() > 1 ? "inventory" : "held item") + " of " + target.getName() + '.');
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return args.length == 1 ? null : Collections.<String>emptyList();
    }
}
