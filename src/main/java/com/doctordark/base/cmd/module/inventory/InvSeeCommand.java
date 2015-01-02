package com.doctordark.base.cmd.module.inventory;

import com.doctordark.base.cmd.BaseCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Command used for checking inventory of players.
 */
public class InvSeeCommand extends BaseCommand {

    private InventoryType[] types = new InventoryType[]{
            InventoryType.BREWING,
            InventoryType.CHEST,
            InventoryType.CRAFTING,
            InventoryType.DISPENSER,
            InventoryType.ENCHANTING,
            InventoryType.FURNACE,
            InventoryType.HOPPER,
            InventoryType.PLAYER,
            InventoryType.WORKBENCH
    };

    public InvSeeCommand() {
        super("invsee", "View the inventory of a player.", "base.command.invsee");
        this.setAliases(new String[]{"inventorysee", "inventory", "inv"});
        this.setUsage("/(command) <inventoryType|playerName>");
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

        Player player = (Player) sender;

        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Usage: " + getUsage(label));
            return true;
        }

        Inventory inventory = null;

        for (InventoryType type : types) {
            if (type.name().equalsIgnoreCase(args[0])) {
                inventory = Bukkit.createInventory(player, type);
            }
        }

        if (inventory == null) {
            Player target = Bukkit.getServer().getPlayer(args[0]);

            if (target == null || !player.canSee(target)) {
                sender.sendMessage(ChatColor.GOLD + "Player '" + ChatColor.WHITE + args[0] + ChatColor.GOLD + "' not found!");
                return true;
            }

            inventory = target.getInventory();
        }

        player.openInventory(inventory);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 1) {
            List<String> results = new ArrayList<String>();

            for (InventoryType type : types) {
                results.add(type.name());
            }

            for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                if (sender instanceof Player && !((Player) sender).canSee(player)) {
                    continue;
                }

                results.add(player.getName());
            }

            return getCompletions(args, results);
        } else {
            return Collections.emptyList();
        }
    }
}
