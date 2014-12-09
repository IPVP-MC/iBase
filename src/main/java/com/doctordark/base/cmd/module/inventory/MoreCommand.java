package com.doctordark.base.cmd.module.inventory;

import com.doctordark.base.cmd.BaseCommand;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

/**
 * Command used for obtaining more of an item.
 */
public class MoreCommand extends BaseCommand {

    public MoreCommand() {
        super("more", "Sets your item to its maximum amount.", "base.command.more");
        this.setAliases(new String[]{});
        this.setUsage("/(command)");
    }

    @Override
    public boolean isPlayerOnlyCommand() {
        return true;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only executable for players!");
            return true;
        }

        Player player = (Player)sender;
        ItemStack stack = player.getItemInHand();

        if (stack == null || stack.getType() == Material.AIR) {
            sender.sendMessage(ChatColor.RED + "You are not holding any item!");
            return true;
        }

        int maxAmount = stack.getMaxStackSize();
        int curAmount = stack.getAmount();

        if (curAmount >= maxAmount) {
            sender.sendMessage(ChatColor.RED + "You already have the maximum amount: " + maxAmount + ".");
            return true;
        }

        stack.setAmount(maxAmount);
        return true;
    }
}
