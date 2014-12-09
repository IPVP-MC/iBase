package com.doctordark.base.cmd.module.teleport;

import com.doctordark.base.cmd.BaseCommand;
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
        this.setUsage("/(command) <worldName|list>");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only executable for players!");
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + getUsage());
            return true;
        }

        if (args[0].equalsIgnoreCase("list")) {
            Collection<World> worlds = Bukkit.getServer().getWorlds();
            List<String> worldList = new ArrayList<String>();

            for (World world : worlds) {

            }
        }

        World world = Bukkit.getServer().getWorld(args[0]);

        if (world == null) {
            sender.sendMessage(ChatColor.RED + "World '" + args[0] + "' not found!");
            return true;
        }

        Player player = (Player)sender;

        Location currentLocation = player.getLocation();
        double x = currentLocation.getX();
        double y = currentLocation.getX();
        double z = currentLocation.getX();
        float yaw = currentLocation.getYaw();
        float pitch = currentLocation.getPitch();

        Location newLocation = new Location(world, x, y, z, yaw, pitch);
        player.teleport(newLocation, PlayerTeleportEvent.TeleportCause.COMMAND);

        sender.sendMessage(ChatColor.GRAY + "Switched world to '" + world.getName() + "'.");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> results = new ArrayList<String>();
        Collection<World> worlds = Bukkit.getServer().getWorlds();

        results.add("list");

        for (World world : worlds) {
            results.add(world.getName());
        }

        return (args.length == 1) ? getCompletions(results, args) : Collections.<String>emptyList();
    }
}
