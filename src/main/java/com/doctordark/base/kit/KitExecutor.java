package com.doctordark.base.kit;

import com.doctordark.base.BasePlugin;
import com.doctordark.base.kit.argument.KitApplyArgument;
import com.doctordark.base.kit.argument.KitCreateArgument;
import com.doctordark.base.kit.argument.KitDeleteArgument;
import com.doctordark.base.kit.argument.KitDescriptionArgument;
import com.doctordark.base.kit.argument.KitDisableArgument;
import com.doctordark.base.kit.argument.KitGuiArgument;
import com.doctordark.base.kit.argument.KitListArgument;
import com.doctordark.base.kit.argument.KitPreviewArgument;
import com.doctordark.base.kit.argument.KitRenameArgument;
import com.doctordark.base.kit.argument.KitSetDelayArgument;
import com.doctordark.base.kit.argument.KitSetImageArgument;
import com.doctordark.base.kit.argument.KitSetIndexArgument;
import com.doctordark.base.kit.argument.KitSetItemsArgument;
import com.doctordark.base.kit.argument.KitSetMaxUsesArgument;
import com.doctordark.util.BukkitUtils;
import com.doctordark.util.command.ArgumentExecutor;
import com.doctordark.util.command.CommandArgument;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles the execution and tab completion of the Kit command.
 */
public class KitExecutor extends ArgumentExecutor {

    private final BasePlugin plugin;

    public KitExecutor(BasePlugin plugin) {
        super("kit");
        this.plugin = plugin;

        addArgument(new KitApplyArgument(plugin));
        addArgument(new KitBypassplaytimeArgument(plugin));
        addArgument(new KitCreateArgument(plugin));
        addArgument(new KitDeleteArgument(plugin));
        addArgument(new KitDescriptionArgument(plugin));
        addArgument(new KitDisableArgument(plugin));
        addArgument(new KitGuiArgument(plugin));
        addArgument(new KitListArgument(plugin));
        addArgument(new KitPreviewArgument(plugin));
        addArgument(new KitRenameArgument(plugin));
        addArgument(new KitSetDelayArgument(plugin));
        addArgument(new KitSetImageArgument(plugin));
        addArgument(new KitSetIndexArgument(plugin));
        addArgument(new KitSetItemsArgument(plugin));
        addArgument(new KitSetMaxUsesArgument(plugin));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(ChatColor.AQUA + "*** Kit Help ***");

            for (CommandArgument argument : arguments) {
                String permission = argument.getPermission();
                if (permission == null || sender.hasPermission(permission)) {
                    sender.sendMessage(ChatColor.GRAY + argument.getUsage(label) + " - " + argument.getDescription() + '.');
                }
            }

            sender.sendMessage(ChatColor.GRAY + "/" + label + " <kitName> - Applies a kit.");
            return true;
        }

        CommandArgument argument = getArgument(args[0]);
        String permission = argument == null ? null : argument.getPermission();
        if (argument == null || (permission != null && !sender.hasPermission(permission))) {
            Kit kit = plugin.getKitManager().getKit(args[0]);
            if (sender instanceof Player && kit != null) {
                String kitPermission = kit.getPermission();
                if (kitPermission == null || sender.hasPermission(kitPermission)) {
                    Player player = (Player) sender;
                    kit.applyTo(player, false, true);
                    return true;
                }
            }

            sender.sendMessage(ChatColor.RED + "Kit sub-command or kit " + args[0] + " not found.");
            return true;
        }

        argument.onCommand(sender, command, label, args);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 1) {
            return super.onTabComplete(sender, command, label, args);
        }

        List<String> previous = super.onTabComplete(sender, command, label, args);
        List<String> kitNames = new ArrayList<>();
        for (Kit kit : plugin.getKitManager().getKits()) {
            String permission = kit.getPermission();
            if (permission == null || sender.hasPermission(permission)) {
                kitNames.add(kit.getName());
            }
        }

        if (previous == null || previous.isEmpty()) {
            previous = kitNames;
        } else {
            previous = new ArrayList<>(previous);
            previous.addAll(0, kitNames);
        }

        return BukkitUtils.getCompletions(args, previous);
    }
}