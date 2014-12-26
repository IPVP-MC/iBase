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
            sender.sendMessage(ChatColor.RED + "Usage: " + getUsage(label));
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
                target = (Player) sender;
            } else {
                sender.sendMessage(ChatColor.RED + "Usage: " + getUsage(label));
                return true;
            }
        } else {
            target = Bukkit.getServer().getPlayer(args[1]);
        }

        if ((target == null) || (!canSee(sender, target))) {
            sender.sendMessage(ChatColor.GOLD + "Player '" + ChatColor.WHITE + args[1] + ChatColor.GOLD + "' not found!");
            return true;
        }

        if (target.getGameMode() == mode) {
            sender.sendMessage(ChatColor.RED + "Gamemode of " + target.getName() + " is already " + mode.name() + "!");
            return true;
        }

        target.setGameMode(mode);

        Command.broadcastCommandMessage(sender, ChatColor.GOLD + "Set gamemode of " + target.getName() + ChatColor.GOLD + " to " + ChatColor.WHITE + mode.name() + ChatColor.GOLD + ".");
        return true;
    }

    private GameMode getGamemodeByName(String id) {
        if (id.equalsIgnoreCase("gmc") || id.contains("creat") || id.equalsIgnoreCase("1") || id.equalsIgnoreCase("c")) {
            return GameMode.CREATIVE;
        } else if (id.equalsIgnoreCase("gms") || id.contains("survi") || id.equalsIgnoreCase("0") || id.equalsIgnoreCase("s")) {
            return GameMode.SURVIVAL;
        } else if (id.equalsIgnoreCase("gma") || id.contains("advent") || id.equalsIgnoreCase("2") || id.equalsIgnoreCase("a")) {
            return GameMode.ADVENTURE;
        } else if (id.equalsIgnoreCase("gmt") || id.contains("toggle") || id.contains("cycle") || id.equalsIgnoreCase("t")) {
            return null;
        } else {
            return null;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> results = new ArrayList<String>();

        if (args.length == 1) {
            GameMode[] modes = GameMode.values();

            for (GameMode mode : modes) {
                results.add(mode.name());
            }
        } else if (args.length == 2) {
            return null;
        }

        return getCompletions(results, args);
    }
}
