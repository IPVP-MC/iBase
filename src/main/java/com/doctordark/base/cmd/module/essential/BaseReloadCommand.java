package com.doctordark.base.cmd.module.essential;

import com.doctordark.base.BasePlugin;
import com.doctordark.base.cmd.BaseCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

/**
 * Command used to check a players biome.
 */
public class BaseReloadCommand extends BaseCommand {

    private final BasePlugin plugin;

    public BaseReloadCommand(BasePlugin plugin) {
        super("basereload", "Reloads the 'Base' plugin.", "base.command.basereload");
        this.setAliases(new String[]{"breload"});
        this.setUsage("/(command)");
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        plugin.getServerManager().reloadServerData();
        plugin.getUserManager().reloadUserData();

        Command.broadcastCommandMessage(sender, "Reloaded configuration of Base plugin.");
        return true;
    }
}