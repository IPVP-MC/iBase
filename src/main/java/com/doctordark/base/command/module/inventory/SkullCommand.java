package com.doctordark.base.command.module.inventory;

import com.doctordark.base.command.BaseCommand;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Collections;
import java.util.List;

/**
 * Command used to spawn skulls of players.
 */
public class SkullCommand extends BaseCommand {

    public SkullCommand() {
        super("skull", "Spawns a player head skull item.", "base.command.skull");
        this.setAliases(new String[]{"head", "playerhead"});
        this.setUsage("/(command) <playerName>");
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

        Player player = (Player)sender;
        String ownerName = (args.length > 0) ? args[0] : sender.getName();

        ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short)3);
        SkullMeta meta = (SkullMeta)skull.getItemMeta();
        meta.setOwner(ownerName);
        meta.setDisplayName(ChatColor.GREEN + ownerName);
        skull.setItemMeta(meta);

        player.getInventory().addItem(skull);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return (args.length == 1) ? null : Collections.<String>emptyList();
    }
}
