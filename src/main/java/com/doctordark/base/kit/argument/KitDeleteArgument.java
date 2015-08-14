package com.doctordark.base.kit.argument;

import com.doctordark.base.BasePlugin;
import com.doctordark.base.kit.Kit;
import com.doctordark.base.kit.event.KitRemoveEvent;
import com.doctordark.util.command.CommandArgument;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * An {@link CommandArgument} used to delete a {@link Kit}.
 */
public class KitDeleteArgument extends CommandArgument {

    private final BasePlugin plugin;

    public KitDeleteArgument(BasePlugin plugin) {
        super("delete", "Deletes a kit");
        this.plugin = plugin;
        this.aliases = new String[]{"del", "remove"};
        this.permission = "hcf.command.kit.argument." + getName();
    }

    @Override
    public String getUsage(String label) {
        return '/' + label + ' ' + getName() + " <kitName>";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: " + getUsage(label));
            return true;
        }

        Kit kit = plugin.getKitManager().getKit(args[1]);

        if (kit == null) {
            sender.sendMessage(ChatColor.RED + "There is not a kit named " + args[1] + '.');
            return true;
        }

        KitRemoveEvent event = new KitRemoveEvent(kit);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return true;
        }

        plugin.getKitManager().removeKit(kit);

        sender.sendMessage(ChatColor.GRAY + "Removed kit '" + args[1] + "'.");
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
