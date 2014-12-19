package com.doctordark.base.cmd.module.essential;

import com.doctordark.base.cmd.BaseCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

/**
 * Command used for feeding of players.
 */
public class FeedCommand extends BaseCommand {

    private static final int MAX_HUNGER = 20;

    public FeedCommand() {
        super("feed", "Feeds a player.", "base.command.feed");
        this.setAliases(new String[]{});
        this.setUsage("/(command) <playerName>");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player target;
        if (args.length < 1) {
            if (sender instanceof Player) {
                target = (Player)sender;
            } else {
                sender.sendMessage(ChatColor.RED + "Usage: " + getUsage());
                return true;
            }
        } else {
            target = Bukkit.getServer().getPlayer(args[0]);
        }

        if ((target == null) || (!canSee(sender, target))) {
            sender.sendMessage(ChatColor.GOLD + "Player '" + ChatColor.WHITE + args[0] + ChatColor.GOLD + "' not found!");
            return true;
        }

        if (target.getFoodLevel() == MAX_HUNGER) {
            sender.sendMessage(ChatColor.RED + target.getName() + " already has full hunger!");
            return true;
        }

        target.setFoodLevel(MAX_HUNGER);
        
        Command.broadcastCommandMessage(sender, ChatColor.YELLOW + "Fed player " + target.getName() + ".");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        return (args.length == 1) ? null : Collections.<String>emptyList();
    }
}