package org.ipvp.ibasic.freeze;

import org.ipvp.ibasic.IBasic;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandFreezeAll implements CommandExecutor {

    private final IBasic plugin;

    public CommandFreezeAll(IBasic plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        boolean newState = !plugin.getFreezeListener().isServerFrozen();
        plugin.getFreezeListener().setServerFrozen(newState);
        sender.sendMessage(ChatColor.GRAY + "Server Freeze Status - " + (newState ? ChatColor.GREEN + "Active" : ChatColor.RED + "Deactivated") + ChatColor.GRAY + ".");
        return true;
    }
}
