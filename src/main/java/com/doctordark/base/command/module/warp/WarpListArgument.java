package com.doctordark.base.command.module.warp;

import com.doctordark.base.BasePlugin;
import com.doctordark.base.warp.Warp;
import com.doctordark.util.command.CommandArgument;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class WarpListArgument extends CommandArgument {

    private final BasePlugin plugin;

    public WarpListArgument(BasePlugin plugin) {
        super("list", "List all server warps");
        this.plugin = plugin;
        this.permission = "base.command.warp.argument." + getName();
        this.aliases = new String[]{"delete", "remove", "unset"};
    }

    @Override
    public String getUsage(String label) {
        return '/' + label + ' ' + getName();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Collection<Warp> warps = plugin.getWarpManager().getWarps();
        List<String> warpNames = new ArrayList<>(warps.size());
        for (Warp warp : warps) {
            warpNames.add(warp.getName());
        }

        sender.sendMessage(ChatColor.DARK_AQUA + "*** Global Warps (" + warpNames.size() + ") ***");
        sender.sendMessage(ChatColor.GRAY + "[" + StringUtils.join(warpNames, ", ") + ']');
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return Collections.emptyList();
    }
}
