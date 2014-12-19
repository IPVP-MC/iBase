package com.doctordark.base.cmd.module.essential;

import com.doctordark.base.cmd.BaseCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class VanishCommand extends BaseCommand {

    public VanishCommand() {
        super("vanish", "Hide from other players.", "base.command.vanish");
        this.setAliases(new String[]{});
        this.setUsage("/(command) [playerName]");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player target;
        if (args.length < 1) {
            if (sender instanceof Player) {
                target = (Player) sender;
            } else {
                sender.sendMessage(ChatColor.RED + "Usage: /" + label + getUsage());
                return true;
            }
        } else {
            target = Bukkit.getServer().getPlayer(args[0]);
        }

        if (target == null || (sender instanceof Player && !((Player) sender).canSee(target))) {
            sender.sendMessage(ChatColor.GOLD + "Player '" + ChatColor.WHITE + args[0] + ChatColor.GOLD + "' not found!");
            return true;
        }

        UUID uuid = target.getUniqueId();

        boolean vanished = (args.length >= 2) ? Boolean.parseBoolean(args[1]) : !getBasePlugin().getUserManager().isVanished(uuid);
        getBasePlugin().getUserManager().setVanished(uuid, vanished);
        Command.broadcastCommandMessage(sender, ChatColor.YELLOW + "Vanish mode of " + target.getName() + " set to " + vanished + ".");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        return (args.length == 1) ? null : Collections.<String>emptyList();
    }
}