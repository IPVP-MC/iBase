package com.doctordark.base.command.module.essential;

import com.doctordark.base.command.BaseCommand;
import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class EnchantCommand extends BaseCommand {

    public EnchantCommand() {
        super("enchant", "Unsafely enchant an item.", "base.command.enchant");
        this.setUsage("/(command) <enchantment> <level> [playerName]");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: " + getUsage());
            return true;
        }

        final Player target;
        if (args.length > 2 && sender.hasPermission(command.getPermission() + ".others")) {
            target = Bukkit.getPlayer(args[2]);
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

        Enchantment enchantment = Enchantment.getByName(args[1]);

        if (enchantment == null) {
            sender.sendMessage(ChatColor.RED + "No enchantment named '" + args[1] + "' found.");
            return true;
        }

        ItemStack stack = target.getItemInHand();

        if (stack == null || stack.getType() == Material.AIR) {
            sender.sendMessage(ChatColor.RED + target.getName() + " is not holding an item.");
            return true;
        }

        Integer level = Ints.tryParse(args[2]);

        if (level == null) {
            sender.sendMessage(ChatColor.RED + "'" + args[2] + "' is not a number.");
            return true;
        }

        stack.addUnsafeEnchantment(enchantment, level);

        String itemName;
        try {
            itemName = CraftItemStack.asNMSCopy(stack).getName();
        } catch (Error ex) {
            itemName = stack.getType().name();
        }

        Command.broadcastCommandMessage(sender, "Applied " + enchantment.getName() + " at level " + level + " onto " + itemName + " of " + target.getName() + '.');
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            return null;
        } else if (args.length == 2) {
            Enchantment[] enchantments = Enchantment.values();
            List<String> results = Lists.newArrayListWithExpectedSize(enchantments.length);
            for (Enchantment enchantment : enchantments) {
                results.add(enchantment.getName());
            }

            return getCompletions(args, results);
        } else {
            return super.onTabComplete(sender, command, label, args);
        }
    }
}
