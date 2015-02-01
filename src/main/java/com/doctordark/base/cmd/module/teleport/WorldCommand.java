package com.doctordark.base.cmd.module.teleport;

import com.doctordark.base.cmd.BaseCommand;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Command used for changing worlds.
 */
public class WorldCommand extends BaseCommand {

    public WorldCommand() {
        super("world", "Change current world.", "base.command.world");
        this.setAliases(new String[]{"changeworld", "switchworld"});
        this.setUsage("/(command) <worldName>");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only executable for players.");
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + getUsage());
            return true;
        }

        World world = Bukkit.getServer().getWorld(args[0]);

        if (world == null) {
            sender.sendMessage(ChatColor.RED + "World '" + args[0] + "' not found.");
            return true;
        }

        Player player = (Player)sender;
        World.Environment environment = world.getEnvironment();

        if (player.getWorld().equals(world)) {
            sender.sendMessage(ChatColor.RED + "You are already in that world.");
            return true;
        }

        Location playerLocation = player.getLocation();
        double x = playerLocation.getX();
        double y = playerLocation.getY();
        double z = playerLocation.getZ();
        float yaw = playerLocation.getYaw();
        float pitch = playerLocation.getPitch();
        Location location = new Location(world, x, y, z, yaw, pitch);

        player.teleport(location, PlayerTeleportEvent.TeleportCause.COMMAND);

        String worldName = world.getName();
        String environmentName = WordUtils.capitalizeFully(world.getEnvironment().name().replace('_', ' '));

        sender.sendMessage(ChatColor.AQUA + "Switched world to '" + worldName + ChatColor.YELLOW + " [" + environmentName + "]" + ChatColor.AQUA + "'.");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> results = new ArrayList<String>();
        Collection<World> worlds = Bukkit.getServer().getWorlds();

        for (World world : worlds) {
            results.add(world.getName());
        }

        return (args.length == 1) ? getCompletions(args, results) : Collections.<String>emptyList();
    }
}
