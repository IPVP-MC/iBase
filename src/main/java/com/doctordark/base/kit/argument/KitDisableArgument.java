package com.doctordark.base.kit.argument;

import com.doctordark.base.BasePlugin;
import com.doctordark.base.kit.Kit;
import com.doctordark.util.command.CommandArgument;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * An {@link CommandArgument} used to disable or enable a {@link Kit}.
 */
public class KitDisableArgument extends CommandArgument {

    private final BasePlugin plugin;

    public KitDisableArgument(BasePlugin plugin) {
        super("disable", "Disable or enable a kit");
        this.plugin = plugin;
        this.aliases = new String[]{"enable", "toggle"};
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
            sender.sendMessage(ChatColor.RED + "No kit named " + args[1] + " found.");
            return true;
        }

        boolean newEnabled = !kit.isEnabled();
        kit.setEnabled(newEnabled);

        sender.sendMessage(ChatColor.AQUA + "Kit " + kit.getName() + " has been " + (newEnabled ? ChatColor.GREEN + "enabled" : ChatColor.RED + "disabled") + ChatColor.AQUA + '.');
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
