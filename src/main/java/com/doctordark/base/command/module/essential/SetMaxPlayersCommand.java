package com.doctordark.base.command.module.essential;

import com.doctordark.base.BasePlugin;
import com.doctordark.base.command.BaseCommand;
import com.doctordark.util.Utils;
import com.google.common.primitives.Ints;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class SetMaxPlayersCommand extends BaseCommand {

    private final BasePlugin plugin;

    public SetMaxPlayersCommand(BasePlugin plugin) {
        super("setmaxplayers", "Sets the max player cap.", "base.command.setmaxplayers");
        setAliases(new String[]{"setplayercap"});
        setUsage("/(command) <amount>");
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Usage: " + getUsage(label));
            return true;
        }

        Integer amount = Ints.tryParse(args[0]);

        if (amount == null) {
            sender.sendMessage(ChatColor.RED + "'" + args[0] + "' is not a number.");
            return true;
        }

        plugin.getServerHandler().setMaxPlayers(amount);

        Command.broadcastCommandMessage(sender, ChatColor.YELLOW + "Set the maximum players to " + amount + ".");
        return true;
    }
}
