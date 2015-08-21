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
public class KitBypassplaytimeArgument extends CommandArgument {

    public static boolean bypassPlaytime = false;

    private final BasePlugin plugin;

    public KitBypassplaytimeArgument(BasePlugin plugin) {
        super("bypassplaytime", "Bypass minimum kit playtime");
        this.plugin = plugin;
        this.permission = "base.command.kit.argument." + getName();
    }

    @Override
    public String getUsage(String label) {
        return '/' + label + ' ' + getName() + " <kitName> <playerName>";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (bypassPlaytime) {
            Command.broadcastCommandMessage(sender, "Players can no longer use kits ignoring playtime");
        } else Command.broadcastCommandMessage(sender, "Players can now use kits ignoring playtime");

        bypassPlaytime = !bypassPlaytime;
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
