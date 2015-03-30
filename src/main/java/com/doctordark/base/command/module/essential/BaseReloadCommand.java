package com.doctordark.base.command.module.essential;

import com.doctordark.base.BasePlugin;
import com.doctordark.base.command.BaseCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class BaseReloadCommand extends BaseCommand {

    private final BasePlugin plugin;

    public BaseReloadCommand(BasePlugin plugin) {
        super("basereload", "Reloads the base plugin.", "base.command.basereload");
        setAliases(new String[]{"breload"});
        setUsage("/(command)");
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        plugin.getServerHandler().reloadServerData();
        plugin.getUserManager().reloadUserData();
        plugin.reloadSchedulers();

        sender.sendMessage(ChatColor.GOLD + "Reloaded the schedulers, server and user data of " + ChatColor.RED + plugin.getDescription().getFullName() + ChatColor.YELLOW + "..");
        return true;
    }
}
