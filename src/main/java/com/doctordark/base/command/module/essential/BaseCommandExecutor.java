package com.doctordark.base.command.module.essential;

import com.doctordark.base.BasePlugin;
import com.doctordark.base.command.BaseCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class BaseCommandExecutor extends BaseCommand {

    private final BasePlugin plugin;

    public BaseCommandExecutor(BasePlugin plugin) {
        super("base", "Reloads the base plugin.", "base.command.base");
        setAliases(new String[]{"baseplugin"});
        setUsage("/(command) <reload|toggleprotocollib>");
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        boolean useProtocolLib = BasePlugin.getPlugin().getServerHandler().useProtocolLib;
        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Usage: " + getUsage());
            sender.sendMessage(ChatColor.RED + " - " + ChatColor.YELLOW + "ProtocolLib: " + useProtocolLib);
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            plugin.getServerHandler().reloadServerData();
            plugin.getUserManager().reloadParticipatorData();
            plugin.reloadSchedulers();
            sender.sendMessage(ChatColor.GOLD + "Reloaded the schedulers, server and user data of " + ChatColor.RED + plugin.getDescription().getFullName() + ChatColor.GOLD + "..");
        }

        if (args[0].equalsIgnoreCase("toggleprotocollib")) {
            boolean newUseProtocolLib = !useProtocolLib;
            BasePlugin.getPlugin().getServerHandler().useProtocolLib = newUseProtocolLib;
            Command.broadcastCommandMessage(sender, ChatColor.GOLD + plugin.getDescription().getFullName() + " is " + (newUseProtocolLib ? "now" : "no longer") + " using ProtocolLib.");
            return true;
        }

        sender.sendMessage(ChatColor.RED + "Usage: " + getUsage());
        sender.sendMessage(ChatColor.RED + " - " + ChatColor.YELLOW + "ProtocolLib: " + useProtocolLib);
        return true;
    }
}
