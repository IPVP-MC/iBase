package com.doctordark.base.command.module.essential;

import com.doctordark.base.command.BaseCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class BiomeCommand extends BaseCommand {

    public BiomeCommand() {
        super("biome", "Checks a players biome.", "base.command.biome");
        setAliases(new String[0]);
        setUsage("/(command) [playerName]");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        final Player target;
        if (args.length > 0) {
            target = Bukkit.getServer().getPlayer(args[0]);
        } else if (!(sender instanceof Player))  {
            sender.sendMessage(ChatColor.RED + "Usage: " + getUsage(label));
            return true;
        } else {
            target = (Player) sender;
        }

        if ((target == null) || (!canSee(sender, target))) {
            sender.sendMessage(ChatColor.GOLD + "Player '" + ChatColor.WHITE + args[0] + ChatColor.GOLD + "' not found.");
            return true;
        }

        Location location = target.getLocation();
        World world = location.getWorld();
        Biome biome = world.getBiome(location.getBlockX(), location.getBlockZ());

        sender.sendMessage(ChatColor.YELLOW + target.getName() + " is in the " + biome.name() + " biome.");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return args.length == 1 ? null : Collections.emptyList();
    }
}
