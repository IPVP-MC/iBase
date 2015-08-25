package com.doctordark.base.command.module.warp;

import com.doctordark.base.BasePlugin;
import com.doctordark.base.command.BaseCommand;
import com.doctordark.base.warp.Warp;
import com.doctordark.util.BukkitUtils;
import com.doctordark.util.command.CommandArgument;
import com.doctordark.util.command.CommandWrapper;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class WarpExecutor extends BaseCommand {

    private final List<CommandArgument> arguments = new ArrayList<>(3);
    private final Map<UUID, BukkitRunnable> taskMap = new HashMap<>();
    private final BasePlugin plugin;

    public WarpExecutor(BasePlugin plugin) {
        super("globalwarp", "Teleport to locations on the server.");
        setAliases(new String[]{"gw"});
        setUsage("/(command)");

        this.plugin = plugin;
        this.arguments.add(new WarpListArgument(plugin));
        this.arguments.add(new WarpRemoveArgument(plugin));
        this.arguments.add(new WarpSetArgument(plugin));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            CommandWrapper.printUsage(sender, label, arguments);
            sender.sendMessage(ChatColor.GRAY + "/" + label + " <warpName> - Teleport to a server warp.");
            return true;
        }

        CommandArgument argument = CommandWrapper.matchArgument(args[0], sender, arguments);

        if (argument == null) {
            handleWarp(sender, args);
            return true;
        }

        return argument.onCommand(sender, command, label, args);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return Collections.emptyList();
        }

        List<String> results;
        if (args.length == 1) {
            results = CommandWrapper.getAccessibleArgumentNames(sender, arguments);
            for (Warp warp : plugin.getWarpManager().getWarps()) {
                results.add(warp.getName());
            }
        } else {
            CommandArgument argument = CommandWrapper.matchArgument(args[0], sender, arguments);
            if (argument == null) {
                return Collections.emptyList();
            } else {
                results = argument.onTabComplete(sender, command, label, args);
            }
        }

        if (results == null) return null;
        return BukkitUtils.getCompletions(args, results);
    }

    private boolean handleWarp(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can teleport to warps.");
            return true;
        }

        final Warp warp = plugin.getWarpManager().getWarp(args[0]);

        if (warp == null) {
            sender.sendMessage(ChatColor.RED + "Server warp " + args[0] + " not found.");
            return true;
        }

        if (warp.getLocation().getWorld() == null) {
            sender.sendMessage(ChatColor.RED + "That warp has an invalid world!");
            return true;
        }

        final Player player = (Player) sender;
        int radius = plugin.getWarpManager().getNearbyPlayerRadiusCancel();

        int count = 0;
        for (Entity entity : player.getNearbyEntities(radius, radius, radius)) {
            if (entity instanceof Player) {
                count++;
            }
        }

        int delay = plugin.getWarpManager().getWarpDelaySeconds();

        if (delay <= 0 || count <= 0) {
            warpPlayer(player, warp);
            return false;
        }

        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                warpPlayer(player, warp);
            }
        };

        runnable.runTaskLater(plugin, 20L * delay);
        taskMap.put(player.getUniqueId(), runnable);

        sender.sendMessage(ChatColor.GRAY + "Players are nearby. Please wait " + DurationFormatUtils.formatDurationWords(delay * 1000, true, true) + '.');
        return true;
    }

    private void warpPlayer(Player player, Warp warp) {
        BukkitRunnable runnable = taskMap.remove(player.getUniqueId());
        if (runnable != null) runnable.cancel();

        Location location = warp.getLocation();
        player.teleport(location, PlayerTeleportEvent.TeleportCause.COMMAND);
        player.sendMessage(ChatColor.GRAY + "Warped to " + warp.getName() + '.');
    }
}
