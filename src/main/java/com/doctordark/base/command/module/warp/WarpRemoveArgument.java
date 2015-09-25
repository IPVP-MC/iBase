package com.doctordark.base.command.module.warp;

import com.doctordark.base.BasePlugin;
import com.doctordark.base.warp.Warp;
import com.doctordark.util.BukkitUtils;
import com.doctordark.util.command.CommandArgument;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class WarpRemoveArgument extends CommandArgument {

    private final BasePlugin plugin;

    public WarpRemoveArgument(BasePlugin plugin) {
        super("del", "Deletes a new server warp");
        this.plugin = plugin;
        this.aliases = new String[]{"delete", "remove", "unset"};
        this.permission = "base.command.warp.argument." + getName();
    }

    @Override
    public String getUsage(String label) {
        return '/' + label + ' ' + getName();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can delete warps.");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /" + label + ' ' + getName() + " <warpName>");
            return true;
        }

        Warp warp = plugin.getWarpManager().removeWarp(args[1]);

        if (warp == null) {
            sender.sendMessage(ChatColor.RED + "There is not a warp named " + args[1] + '.');
            return true;
        }

        Collection<Warp> warps = plugin.getWarpManager().getWarps();
        warps.remove(warp);

        sender.sendMessage(ChatColor.GRAY + "Removed global warp named " + warp.getName() + '.');
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 2) {
            return Collections.emptyList();
        }

        Collection<Warp> warps = plugin.getWarpManager().getWarps();
        List<String> warpNames = new ArrayList<>(warps.size());
        for (Warp warp : warps) {
            warpNames.add(warp.getName());
        }

        return BukkitUtils.getCompletions(args, warpNames);
    }
}
