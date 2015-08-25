package com.doctordark.base.command.module.essential;

import com.doctordark.base.command.BaseCommand;
import com.doctordark.util.BukkitUtils;
import com.google.common.collect.Lists;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Command used to set a players game-mode.
 */
public class GamemodeCommand extends BaseCommand {

    public GamemodeCommand() {
        super("gamemode", "Sets a gamemode for a player.");
        this.setAliases(new String[]{"gm"});
        this.setUsage("/(command) <modeName> [playerName]");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Usage: " + getUsage(label));
            return true;
        }

        GameMode mode = getGameModeByName(args[0]);

        if (mode == null) {
            sender.sendMessage(ChatColor.RED + "Gamemode '" + args[0] + "' not found.");
            return true;
        }

        final Player target;
        if (args.length > 1) {
            target = BukkitUtils.playerWithNameOrUUID(args[1]);
        } else if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Usage: " + getUsage(label));
            return true;
        } else {
            target = (Player) sender;
        }

        if (target == null || !canSee(sender, target)) {
            sender.sendMessage(ChatColor.GOLD + "Player named or with UUID '" + ChatColor.WHITE + args[1] + ChatColor.GOLD + "' not found.");
            return true;
        }

        if (target.getGameMode() == mode) {
            sender.sendMessage(ChatColor.RED + "Gamemode of " + target.getName() + " is already " + mode.name() + '.');
            return true;
        }

        target.setGameMode(mode);

        Command.broadcastCommandMessage(sender, ChatColor.GOLD + "Set gamemode of " + target.getName() + ChatColor.GOLD + " to " + ChatColor.WHITE + mode.name() + ChatColor.GOLD + '.');
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 1) {
            return Collections.emptyList();
        }

        GameMode[] gameModes = GameMode.values();
        List<String> results = Lists.newArrayListWithCapacity(gameModes.length);
        for (GameMode mode : gameModes) {
            results.add(mode.name());
        }

        return BukkitUtils.getCompletions(args, results);
    }

    /**
     * Gets a game-mode by a given name.
     *
     * @param id the id to search
     * @return the game-mode from name
     */
    private GameMode getGameModeByName(String id) {
        id = id.toLowerCase(Locale.ENGLISH);
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
}
