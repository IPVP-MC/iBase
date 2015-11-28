package me.poisonex.plugins.ibasic.freeze;

import me.poisonex.plugins.ibasic.IBasic;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CmdFreezeall implements CommandExecutor {

    private final IBasic plugin;

    public CmdFreezeall(IBasic plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        boolean serverFrozen = plugin.getFreezeManager().isServerFrozen();
        plugin.getFreezeManager().setServerFrozen(!serverFrozen);
        sender.sendMessage(ChatColor.GRAY + "Server Freeze Status - " + (serverFrozen ? ChatColor.GREEN + "Active" : ChatColor.RED + "Deactivated") + ChatColor.GRAY + ".");
        return true;
    }
}
