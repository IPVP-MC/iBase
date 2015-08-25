package com.doctordark.base.kit.argument;

import com.doctordark.base.BasePlugin;
import com.doctordark.base.kit.Kit;
import com.doctordark.util.JavaUtils;
import com.doctordark.util.command.CommandArgument;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * An {@link CommandArgument} used to set the minimum playtime to use a {@link Kit}.
 */
public class KitSetminplaytimeArgument extends CommandArgument {

    private final BasePlugin plugin;

    public KitSetminplaytimeArgument(BasePlugin plugin) {
        super("setminplaytime", "Sets the minimum playtime to use a kit");
        this.aliases = new String[]{"setminimumplaytime"};
        this.plugin = plugin;
        this.permission = "base.command.kit.argument." + getName();
    }

    @Override
    public String getUsage(String label) {
        return '/' + label + ' ' + getName() + " <kitName> <time>";
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

        Long duration = JavaUtils.parse(args[2]);

        if (duration == null) {
            sender.sendMessage(ChatColor.RED + "Invalid duration, use the correct format: 10m 1s");
            return true;
        }

        kit.setMinPlaytimeMillis(duration);
        sender.sendMessage(ChatColor.YELLOW + "Set minimum playtime to use kit " + kit.getDisplayName() + " at " + kit.getMinPlaytimeWords() + '.');
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
