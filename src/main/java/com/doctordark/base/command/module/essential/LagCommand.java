package com.doctordark.base.command.module.essential;

import com.doctordark.base.command.BaseCommand;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Collection;

public class LagCommand extends BaseCommand {

    private static final long MEGABYTE = 1024L * 1024L;
    private static final double MAXIMUM_TPS = 20.0D;

    public LagCommand() {
        super("lag", "Checks the lag of the server.", "base.command.lag");
        setUsage("/(command)");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        double tps = Bukkit.spigot().getTPS()[0];
        double lag = Math.round((1.0D - tps / MAXIMUM_TPS) * 100.0D);

        ChatColor colour;
        if (tps >= 18.0D) {
            colour = ChatColor.GREEN;
        } else if (tps >= 15.0D) {
            colour = ChatColor.YELLOW;
        } else {
            colour = ChatColor.RED;
        }

        sender.sendMessage(colour + "Server TPS is currently at " + (Math.round(tps * 10000) / 10000.0D) + '.');
        sender.sendMessage(colour + "Server lag is currently at " + (Math.round(lag * 10000) / 10000.0D) + '%');

        if (sender.hasPermission(getPermission() + ".memory")) {
            Runtime runtime = Runtime.getRuntime();
            sender.sendMessage(colour + "Available Processors: " + runtime.availableProcessors());
            sender.sendMessage(colour + "Max Memory: " + (runtime.maxMemory() / MEGABYTE) + "MB");
            sender.sendMessage(colour + "Total Memory: " + (runtime.totalMemory() / MEGABYTE) + "MB");
            sender.sendMessage(colour + "Free Memory: " + (runtime.freeMemory() / MEGABYTE) + "MB");

            Collection<World> worlds = Bukkit.getWorlds();
            for (World world : worlds) {
                World.Environment environment = world.getEnvironment();
                String environmentName = WordUtils.capitalizeFully(environment.name().replace('_', ' '));

                int tileEntities = 0;
                Chunk[] loadedChunks = world.getLoadedChunks();
                for (Chunk chunk : loadedChunks) {
                    tileEntities += chunk.getTileEntities().length;
                }

                sender.sendMessage(ChatColor.RED + world.getName() + '(' + environmentName + "): " +
                        ChatColor.YELLOW + loadedChunks.length + " chunks, " + world.getEntities().size() + " entities, " + tileEntities + "tile entities.");
            }
        }

        return true;
    }
}
