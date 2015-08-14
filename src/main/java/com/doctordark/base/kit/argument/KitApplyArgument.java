package com.doctordark.base.kit.argument;

import com.doctordark.base.BasePlugin;
import com.doctordark.base.kit.Kit;
import com.doctordark.util.command.CommandArgument;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * An {@link CommandArgument} used for applying {@link Kit}s to {@link Player}s.
 */
public class KitApplyArgument extends CommandArgument {

    private final BasePlugin plugin;

    public KitApplyArgument(BasePlugin plugin) {
        super("apply", "Applies a kit to player");
        this.plugin = plugin;
        this.aliases = new String[]{"give"};
        this.permission = "hcf.command.kit.argument." + getName();
    }

    @Override
    public String getUsage(String label) {
        return '/' + label + ' ' + getName() + " <kitName> <playerName>";
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

        Player target = Bukkit.getPlayer(args[2]);

        if (target == null || (sender instanceof Player && !((Player) sender).canSee(target))) {
            sender.sendMessage(ChatColor.GOLD + "Player '" + ChatColor.WHITE + args[2] + ChatColor.GOLD + "' not found.");
            return true;
        }

        if (kit.applyTo(target, true, true)) {
            sender.sendMessage(ChatColor.GRAY + "Applied kit '" + kit.getName() + "' to '" + target.getName() + "'.");
        } else {
            sender.sendMessage(ChatColor.RED + "Failed to apply kit " + kit.getName() + " to " + target.getName() + '.');
            return true;
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 2) {
            return plugin.getKitManager().getKits().stream().map(Kit::getName).collect(Collectors.toList());
        } else if (args.length == 3) {
            return null;
        } else {
            return Collections.emptyList();
        }
    }
}
