package com.doctordark.base.cmd.module.essential;

import com.doctordark.base.cmd.BaseCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Command used to set a players game-mode.
 */
public class GamemodeCommand extends BaseCommand {

    public GamemodeCommand() {
        super("gamemode", "Sets a gamemode for a player.", "base.command.gamemode");
        this.setAliases(new String[]{"gm"});
        this.setUsage("/(command) <modeName> [playerName]");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Usage: " + getUsage());
            return true;
        }

        GameMode mode = getGamemodeByName(args[0]);

        if (mode == null) {
            sender.sendMessage(ChatColor.RED + "Gamemode '" + args[0] + "' not found!");
            return true;
        }

        Player target;
        if (args.length < 2) {
            if (sender instanceof Player) {
                target = (Player)sender;
            } else {
                sender.sendMessage(ChatColor.RED + "Usage: " + getUsage());
                return true;
            }
        } else {
            target = Bukkit.getServer().getPlayer(args[1]);
        }

        if ((target == null) || (sender instanceof Player && !((Player)sender).canSee(target))) {
            sender.sendMessage(ChatColor.GOLD + "Player '" + ChatColor.WHITE + args[1] + ChatColor.GOLD + "' not found!");
            return true;
        }

        if (target.getGameMode() == mode) {
            sender.sendMessage(ChatColor.RED + "Gamemode of " + target.getName() + " is already " + mode.name() + "!");
            return true;
        }

        target.setGameMode(mode);

        Command.broadcastCommandMessage(sender, ChatColor.GOLD + "Set gamemode of " + target.getDisplayName() + ChatColor.GOLD + " to " +
                ChatColor.WHITE + mode.name() + ChatColor.GOLD + ".");
        return true;
    }

    private GameMode getGamemodeByName(String id) {
        try {
            return GameMode.valueOf(id);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> list = new ArrayList<String>();

        if (args.length == 1) {
            for (GameMode mode : GameMode.values()) {
                list.add(mode.name());
            }
        }

        return getCompletions(list, args);
    }
}
