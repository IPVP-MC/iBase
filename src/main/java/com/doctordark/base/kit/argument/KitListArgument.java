package com.doctordark.base.kit.argument;

import com.doctordark.base.BasePlugin;
import com.doctordark.base.kit.Kit;
import com.doctordark.util.command.CommandArgument;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * An {@link CommandArgument} used to list all available {@link Kit}s.
 */
public class KitListArgument extends CommandArgument {

    private final BasePlugin plugin;

    public KitListArgument(BasePlugin plugin) {
        super("list", "Lists all current kits");
        this.plugin = plugin;
        this.permission = "base.command.kit.argument." + getName();
    }

    @Override
    public String getUsage(String label) {
        return '/' + label + ' ' + getName();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        List<Kit> kits = plugin.getKitManager().getKits();

        if (kits.isEmpty()) {
            sender.sendMessage(ChatColor.RED + "No kits have been defined.");
            return true;
        }

        List<String> kitNames = new ArrayList<>();

        for (Kit kit : kits) {
            String permission = kit.getPermission();
            if (permission == null || sender.hasPermission(permission)) {
                kitNames.add(ChatColor.GREEN + kit.getName());
            }
        }

        String kitList = StringUtils.join(kitNames, ChatColor.GRAY + ", ");

        sender.sendMessage(ChatColor.GRAY + "*** Kits (" + kitNames.size() + '/' + kits.size() + ") ***");
        sender.sendMessage(ChatColor.GRAY + "[" + ChatColor.WHITE + kitList + ChatColor.GRAY + ']');
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return Collections.emptyList();
    }
}
