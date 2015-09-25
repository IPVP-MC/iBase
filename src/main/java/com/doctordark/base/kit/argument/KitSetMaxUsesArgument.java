package com.doctordark.base.kit.argument;

import com.doctordark.base.BasePlugin;
import com.doctordark.base.kit.FlatFileKitManager;
import com.doctordark.base.kit.Kit;
import com.doctordark.util.command.CommandArgument;
import com.google.common.collect.ImmutableList;
import com.google.common.primitives.Ints;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * An {@link CommandArgument} used to set the maximum uses of a {@link Kit}.
 */
public class KitSetMaxUsesArgument extends CommandArgument {

    private final BasePlugin plugin;

    public KitSetMaxUsesArgument(BasePlugin plugin) {
        super("setmaxuses", "Sets the maximum uses for a kit");
        this.plugin = plugin;
        this.aliases = new String[]{"setmaximumuses"};
        this.permission = "base.command.kit.argument." + getName();
    }

    @Override
    public String getUsage(String label) {
        return '/' + label + ' ' + getName() + " <kitName> <amount|unlimited>";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Usage: " + getUsage(label));
            return true;
        }

        Kit kit = plugin.getKitManager().getKit(args[1]);

        if (kit == null) {
            sender.sendMessage(ChatColor.RED + "There is not a kit named " + args[1] + '.');
            return true;
        }

        Integer amount;
        if (args[2].equalsIgnoreCase("unlimited")) {
            amount = FlatFileKitManager.UNLIMITED_USES;
        } else {
            amount = Ints.tryParse(args[2]);

            if (amount == null) {
                sender.sendMessage(ChatColor.RED + "'" + args[2] + "' is not a number.");
                return true;
            }
        }

        kit.setMaximumUses(amount);
        sender.sendMessage(ChatColor.GRAY + "Set maximum uses of kit " + kit.getDisplayName() + " to " +
                (amount == FlatFileKitManager.UNLIMITED_USES ? "unlimited" : amount) + '.');

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 2) {
            Collection<Kit> kits = plugin.getKitManager().getKits();
            List<String> results = new ArrayList<>(kits.size());
            for (Kit kit : kits) {
                results.add(kit.getName());
            }

            return results;
        } else if (args.length == 3) {
            return COMPLETIONS_THIRD;
        } else {
            return Collections.emptyList();
        }
    }

    private static final List<String> COMPLETIONS_THIRD = ImmutableList.of("UNLIMITED");
}
