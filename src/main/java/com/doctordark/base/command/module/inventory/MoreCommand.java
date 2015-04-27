package com.doctordark.base.command.module.inventory;

import com.doctordark.base.command.BaseCommand;
import com.doctordark.util.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MoreCommand extends BaseCommand {

    public MoreCommand() {
        super("more", "Sets your item to its maximum amount.", "base.command.more");
        setAliases(new String[0]);
        setUsage("/(command)");
    }

    @Override
    public boolean isPlayerOnlyCommand() {
        return true;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only executable for players.");
            return true;
        }

        Player player = (Player) sender;
        ItemStack stack = player.getItemInHand();

        if (stack == null || stack.getType() == Material.AIR) {
            sender.sendMessage(ChatColor.RED + "You are not holding any item.");
            return true;
        }

        Integer amount;
        if (args.length > 0) {
            amount = Utils.getInteger(args[0]);
            if (amount == null) {
                sender.sendMessage(ChatColor.RED + "'" + args[0] + "' is not a number.");
                return true;
            }
        } else {
            int curAmount = stack.getAmount();
            amount = stack.getMaxStackSize();
            if (curAmount >= amount) {
                sender.sendMessage(ChatColor.RED + "You already have the maximum amount: " + amount + ".");
                return true;
            }
        }

        stack.setAmount(amount);
        return true;
    }
}
