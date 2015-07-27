package com.doctordark.base.command.module.essential;

import com.doctordark.base.command.BaseCommand;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.Locale;
import java.util.logging.Level;

public class LagCommand extends BaseCommand {

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

        Runtime runtime = Runtime.getRuntime();
        sender.sendMessage(colour + "Server tps is currently at " + format(tps) + '.');
        sender.sendMessage(colour + "Server lag is currently at " + format(lag) + '%');

        if (sender.hasPermission(getPermission() + ".memory")) {
            sender.sendMessage(colour + "Max Memory: " + runtime.maxMemory() / 1024L / 1024L);
            sender.sendMessage(colour + "Total Memory: " + runtime.totalMemory() / 1024L / 1024L);
            sender.sendMessage(colour + "Free Memory: " + runtime.freeMemory() / 1024L / 1024L);

            Collection<World> worlds = Bukkit.getWorlds();
            for (World world : worlds) {
                World.Environment environment = world.getEnvironment();
                String environmentName = WordUtils.capitalizeFully(environment.name().replace('_', ' '));

                int tileEntities = 0;
                try {
                    for (Chunk chunk : world.getLoadedChunks()) {
                        tileEntities += chunk.getTileEntities().length;
                    }
                } catch (ClassCastException ex) {
                    Bukkit.getLogger().log(Level.SEVERE, "Corrupted chunk data on world " + world, ex);
                }

                int loadedChunks = world.getLoadedChunks().length;
                int entities = world.getEntities().size();
                sender.sendMessage(String.format(Locale.ENGLISH, ChatColor.RED + "%1$s: " + ChatColor.YELLOW + "%2$s chunks, %3$s entities, %4$s tile entities.",
                        environmentName, loadedChunks, entities, tileEntities));
            }
        }

        return true;
    }

    private String format(Double value) {
        DecimalFormat decimalFormat = new DecimalFormat();
        decimalFormat.setMaximumFractionDigits(2);
        decimalFormat.setMinimumFractionDigits(0);
        decimalFormat.setGroupingUsed(false);
        BigDecimal bigDecimal = new BigDecimal(value.toString());
        return decimalFormat.format(bigDecimal);
    }
}
