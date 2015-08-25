package com.doctordark.base.command.module.warp;

import com.doctordark.base.BasePlugin;
import com.doctordark.base.warp.Warp;
import com.doctordark.util.command.CommandArgument;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class WarpSetArgument extends CommandArgument {

    private final BasePlugin plugin;

    public WarpSetArgument(BasePlugin plugin) {
        super("set", "Sets a new server warps", "base.command.warp.argument.set");
        this.plugin = plugin;
        this.aliases = new String[]{"create", "make"};
        this.permission = "base.command.warp.argument." + getName();
    }

    @Override
    public String getUsage(String label) {
        return '/' + label + ' ' + getName();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /" + label + ' ' + getName() + " <warpName>");
            return true;
        }

        if (plugin.getWarpManager().getWarp(args[1]) != null) {
            sender.sendMessage(ChatColor.RED + "There is already a warp named " + args[1] + '.');
            return true;
        }

        Collection<Warp> warps = plugin.getWarpManager().getWarps();

        Player player = (Player) sender;
        Location location = player.getLocation();

        Warp warp = new Warp(args[1], location);
        warps.add(warp);

        sender.sendMessage(ChatColor.GRAY + "Created a global warp named " + warp.getName() + '.');
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return Collections.emptyList();
    }
}
