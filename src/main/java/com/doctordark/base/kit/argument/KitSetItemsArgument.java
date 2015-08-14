package com.doctordark.base.kit.argument;

import com.doctordark.base.BasePlugin;
import com.doctordark.base.kit.Kit;
import com.doctordark.util.command.CommandArgument;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * An {@link CommandArgument} used to setting the inventory load-out of a {@link Kit}.
 */
public class KitSetItemsArgument extends CommandArgument {

    private final BasePlugin plugin;

    public KitSetItemsArgument(BasePlugin plugin) {
        super("setitems", "Sets the items of a kit");
        this.plugin = plugin;
        this.permission = "hcf.command.kit.argument." + getName();
    }

    @Override
    public String getUsage(String label) {
        return '/' + label + ' ' + getName() + " <kitName>";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can set kit items.");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: " + getUsage(label));
            return true;
        }

        Kit kit = plugin.getKitManager().getKit(args[1]);

        if (kit == null) {
            sender.sendMessage(ChatColor.RED + "Kit '" + args[1] + "' not found.");
            return true;
        }

        Player player = (Player) sender;
        PlayerInventory inventory = player.getInventory();

        kit.setItems(inventory.getContents());
        kit.setArmour(inventory.getArmorContents());
        kit.setEffects(player.getActivePotionEffects());

        sender.sendMessage(ChatColor.AQUA + "Set the items of kit " + kit.getName() + " as your current inventory.");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 2) {
            return Collections.emptyList();
        }

        return plugin.getKitManager().getKits().stream().map(Kit::getName).collect(Collectors.toList());
    }
}
