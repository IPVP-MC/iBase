package me.poisonex.plugins.ibasic.freeze;

import me.poisonex.plugins.ibasic.IBasic;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class CmdFreeze implements CommandExecutor, Listener {
    private IBasic plugin;

    public CmdFreeze(IBasic plugin) {
        this.plugin = plugin;
        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length != 1) {
            sender.sendMessage(ChatColor.RED + "Correct Usage: /" + cmd.getName() + " <player>");
            return true;
        }

        Player targetPlayer = this.plugin.getServer().getPlayer(args[0]);

        if (targetPlayer == null) {
            sender.sendMessage(ChatColor.RED + "That player is not online.");
            return true;
        }

        if (plugin.getFreezeManager().getFrozenPlayers().remove(targetPlayer.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + targetPlayer.getName() + " is now unfrozen.");
            targetPlayer.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "You are un-frozen.");
        } else {
            plugin.getFreezeManager().getFrozenPlayers().add(targetPlayer.getUniqueId());
            sender.sendMessage(ChatColor.GREEN + targetPlayer.getName() + " is now frozen.");
            targetPlayer.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "You are frozen.");
        }

        return true;
    }
}
