package com.doctordark.base.command.module.inventory;

import com.doctordark.base.command.BaseCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Collections;
import java.util.List;

/**
 * Command used to clear a players inventory.
 */
public class ClearInvCommand extends BaseCommand {

    public ClearInvCommand() {
        super("ci", "Clears a players inventory.", "base.command.clearinv");
        this.setAliases(new String[]{"inventorysee", "inventory"});
        this.setUsage("/(command) <playerName>");
    }

    @Override
    public boolean isPlayerOnlyCommand() {
        return true;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player target;
        if (args.length < 1) {
            if (sender instanceof Player) {
                target = (Player)sender;
            } else {
                sender.sendMessage(ChatColor.RED + "Usage: " + getUsage(label));
                return true;
            }
        } else {
            target = Bukkit.getServer().getPlayer(args[0]);
        }

        if ((target == null) || (!canSee(sender, target))) {
            sender.sendMessage(ChatColor.GOLD + "Player '" + ChatColor.WHITE + args[0] + ChatColor.GOLD + "' not found.");
            return true;
        }

        PlayerInventory targetInventory = target.getInventory();

        targetInventory.clear();
        targetInventory.setArmorContents(new ItemStack[]{
                new ItemStack(Material.AIR, 1),
                new ItemStack(Material.AIR, 1),
                new ItemStack(Material.AIR, 1),
                new ItemStack(Material.AIR, 1)
        });

        Command.broadcastCommandMessage(sender, ChatColor.YELLOW + "Cleared inventory of player " + target.getName() + ".");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return (args.length == 1) ? null : Collections.<String>emptyList();
    }
}
