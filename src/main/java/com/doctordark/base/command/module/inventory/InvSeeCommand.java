package com.doctordark.base.command.module.inventory;

import com.doctordark.base.BaseConstants;
import com.doctordark.base.BasePlugin;
import com.doctordark.base.command.BaseCommand;
import com.doctordark.util.BukkitUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class InvSeeCommand extends BaseCommand implements Listener {

    private final InventoryType[] types = {
            InventoryType.BREWING, InventoryType.CHEST, InventoryType.DISPENSER,
            InventoryType.ENCHANTING, InventoryType.FURNACE, InventoryType.HOPPER,
            InventoryType.PLAYER, InventoryType.WORKBENCH
    };

    private final Map<InventoryType, Inventory> inventories = new EnumMap<>(InventoryType.class);

    public InvSeeCommand() {
        super("invsee", "View the inventory of a player.", "base.command.invsee");
        setAliases(new String[]{"inventorysee", "inventory", "inv"});
        setUsage("/(command) <inventoryType|playerName>");
        Bukkit.getPluginManager().registerEvents(this, BasePlugin.getPlugin());
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

        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Usage: " + getUsage(label));
            return true;
        }

        Player player = (Player) sender;
        Inventory inventory = null;
        for (InventoryType type : this.types) {
            if (type.name().equalsIgnoreCase(args[0])) {
                inventories.putIfAbsent(type, inventory = Bukkit.createInventory(player, type));
                break;
            }
        }

        if (inventory == null) {
            Player target = BukkitUtils.playerWithNameOrUUID(args[0]);

            if (sender.equals(target)) {
                sender.sendMessage(ChatColor.RED + "You cannot check the inventory of yourself.");
                return true;
            }

            if (target == null || !canSee(sender, target)) {
                sender.sendMessage(String.format(BaseConstants.PLAYER_WITH_NAME_OR_UUID_NOT_FOUND, args[0]));
                return true;
            }

            inventory = target.getInventory();
        }

        player.openInventory(inventory);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 1) {
            return Collections.emptyList();
        }

        InventoryType[] values = InventoryType.values();
        List<String> results = new ArrayList<>(values.length);
        for (InventoryType type : values) {
            results.add(type.name());
        }

        Player senderPlayer = sender instanceof Player ? (Player) sender : null;
        for (Player target : Bukkit.getOnlinePlayers()) {
            if (senderPlayer == null || senderPlayer.canSee(target)) {
                results.add(target.getName());
            }
        }

        return getCompletions(args, results);
    }
}
