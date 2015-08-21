package com.doctordark.base.kit.argument;

import com.doctordark.base.BasePlugin;
import com.doctordark.base.kit.Kit;
import com.doctordark.util.command.CommandArgument;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * An {@link CommandArgument} used to set the description of a {@link Kit}.
 */
public class KitDescriptionArgument extends CommandArgument {

    private final BasePlugin plugin;

    public KitDescriptionArgument(BasePlugin plugin) {
        super("setdesc", "Sets the description of a kit");
        this.plugin = plugin;
        this.aliases = new String[]{"setdescription"};
        this.permission = "base.command.kit.argument." + getName();
    }

    @Override
    public String getUsage(String label) {
        return '/' + label + ' ' + getName() + " <kitName> <none|description>";
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

        if (args[2].equalsIgnoreCase("none") || args[2].equalsIgnoreCase("null")) {
            kit.setDescription(null);
            sender.sendMessage(ChatColor.YELLOW + "Removed description of kit " + kit.getName() + '.');
            return true;
        }

        String description = StringUtils.join(args, ' ', 2, args.length);
        kit.setDescription(description);

        sender.sendMessage(ChatColor.YELLOW + "Set description of kit " + kit.getName() + " to " + description + '.');
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
