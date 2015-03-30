package com.doctordark.base.command.module.teleport.warp;

import com.doctordark.base.BasePlugin;
import com.doctordark.base.command.CommandArgument;
import com.doctordark.base.warp.Warp;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class WarpListArgument extends CommandArgument {

    private final BasePlugin plugin;

    public WarpListArgument(BasePlugin plugin) {
        super("list", "List all server warps");
        this.plugin = plugin;
    }

    @Override
    public String[] getAliases() {
        return new String[]{"delete", "remove", "unset"};
    }

    @Override
    public String getUsage(String label) {
        return "/" + label + " " + getName();
    }

    @Override
    public String getPermission() {
        return "base.command.warp.argument." + getName();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        List<String> warpNames = Lists.newArrayList();
        for (Warp warp : plugin.getWarpManager().getWarps()) {
            warpNames.add(warp.getName());
        }

        sender.sendMessage(ChatColor.DARK_AQUA + "*** Global Warps (" + warpNames.size() + ") ***");
        sender.sendMessage(ChatColor.GRAY + "[" + StringUtils.join(warpNames, ", ") + "]");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return Collections.emptyList();
    }
}
