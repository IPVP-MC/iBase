package com.doctordark.base.cmd.module.essential;

import com.doctordark.base.BasePlugin;
import com.doctordark.base.cmd.BaseCommand;
import com.doctordark.base.util.BaseUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * Command used to a set the maximum players of the server.
 */
public class SetMaxPlayersCommand extends BaseCommand {

    private final BasePlugin plugin;

    public SetMaxPlayersCommand(BasePlugin plugin) {
        super("setmaxplayers", "Sets the max player cap.", "base.command.setmaxplayers");
        this.setAliases(new String[]{"setplayercap"});
        this.setUsage("/(command) <amount>");
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Usage: " + getUsage(label));
            return true;
        }

        Integer amount = BaseUtil.getInteger(args[0]);

        if (amount == null) {
            sender.sendMessage(ChatColor.RED + "'" + args[0] + "' is not a number.");
            return true;
        }

        plugin.getServerManager().setMaxPlayers(amount);

        sender.sendMessage(ChatColor.YELLOW + "Set maximum players to " + amount + ".");
        return true;
    }
}
