package com.doctordark.base.kit.argument;

import com.doctordark.base.BasePlugin;
import com.doctordark.base.kit.Kit;
import com.doctordark.base.kit.event.KitCreateEvent;
import com.doctordark.util.JavaUtils;
import com.doctordark.util.command.CommandArgument;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

/**
 * An {@link CommandArgument} used to create a {@link Kit}.
 */
public class KitCreateArgument extends CommandArgument {

    private final BasePlugin plugin;

    public KitCreateArgument(BasePlugin plugin) {
        super("create", "Creates a kit");
        this.plugin = plugin;
        this.aliases = new String[]{"make", "build"};
        this.permission = "base.command.kit.argument." + getName();
    }

    @Override
    public String getUsage(String label) {
        return '/' + label + ' ' + getName() + " <kitName> [kitDescription]";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players may create kits.");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: " + getUsage(label));
            return true;
        }

        if (!JavaUtils.isAlphanumeric(args[1])) {
            sender.sendMessage(ChatColor.GRAY + "Kit names may only be alphanumeric.");
            return true;
        }

        Kit kit = plugin.getKitManager().getKit(args[1]);

        if (kit != null) {
            sender.sendMessage(ChatColor.RED + "There is already a kit named " + args[1] + '.');
            return true;
        }

        Player player = (Player) sender;
        PlayerInventory inventory = player.getInventory();

        kit = new Kit(args[1], (args.length >= 3 ? args[2] : null), inventory, player.getActivePotionEffects());

        KitCreateEvent event = new KitCreateEvent(kit);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return true;
        }

        plugin.getKitManager().createKit(kit);

        sender.sendMessage(ChatColor.GRAY + "Created kit '" + kit.getName() + "'.");
        return true;
    }
}
