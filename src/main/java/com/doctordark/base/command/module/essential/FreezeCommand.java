package com.doctordark.base.command.module.essential;

import com.doctordark.base.BasePlugin;
import com.doctordark.base.command.BaseCommand;
import com.doctordark.base.event.PlayerFreezeEvent;
import com.doctordark.util.Utils;
import com.google.common.collect.Maps;
import net.minecraft.util.org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Command that freezes players from moving.
 */
public class FreezeCommand extends BaseCommand implements Listener {

    private static final String FREEZE_BYPASS = "base.freeze.bypass";

    private final Map<UUID, Long> frozenPlayers;
    private long defaultFreezeDuration;
    private long serverFrozenMillis;

    public FreezeCommand(BasePlugin plugin) {
        super("freeze", "Freezes a player from moving", "base.command.freeze");
        setAliases(new String[]{});
        setUsage("/(command) <all|playerName>");

        this.frozenPlayers = Maps.newHashMap();
        this.defaultFreezeDuration = TimeUnit.MINUTES.toMillis(5L);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Usage: " + getUsage());
            return true;
        }

        long freezeTicks;
        if (args.length < 2) {
            freezeTicks = defaultFreezeDuration;
        } else {
            StringBuilder builder = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                builder.append(args[i]).append(" ");
            }

            freezeTicks = Utils.parse(builder.toString());
        }

        final long millis = System.currentTimeMillis();

        if (args[0].equalsIgnoreCase("all") && sender.hasPermission(command.getPermission() + ".all")) {
            long oldTicks = getRemainingServerFrozenMillis();
            if (oldTicks > 0L) {
                freezeTicks = 0L;
            }

            this.serverFrozenMillis = millis + freezeTicks;
            Bukkit.getServer().broadcastMessage(ChatColor.YELLOW + "The server is " + (freezeTicks > 0L ?
                    "now frozen for " + DurationFormatUtils.formatDurationWords(freezeTicks, true, true) :
                    "no longer frozen") + ".");
            return true;
        }

        Player target = Bukkit.getServer().getPlayer(args[0]);

        if (target == null || !canSee(sender, target)) {
            sender.sendMessage(ChatColor.GOLD + "Player '" + ChatColor.WHITE + args[0] + ChatColor.GOLD + "' not found.");
            return true;
        }

        UUID targetUUID = target.getUniqueId();
        boolean shouldFreeze = getRemainingPlayerFrozenMillis(targetUUID) > 0;

        PlayerFreezeEvent playerFreezeEvent = new PlayerFreezeEvent(target, shouldFreeze);
        Bukkit.getServer().getPluginManager().callEvent(playerFreezeEvent);
        if (playerFreezeEvent.isCancelled()) {
            sender.sendMessage(ChatColor.RED + "Unable to freeze " + target.getName() + ".");
            return false;
        }

        if (shouldFreeze) {
            frozenPlayers.remove(targetUUID);
            target.sendMessage(ChatColor.GREEN + "You have been un-frozen.");
            Command.broadcastCommandMessage(sender, ChatColor.YELLOW + target.getName() + " is no longer frozen.");
        } else {
            frozenPlayers.put(targetUUID, millis + freezeTicks);
            target.sendMessage(ChatColor.RED + ChatColor.BOLD.toString() + "You have been frozen.");
            Command.broadcastCommandMessage(sender, ChatColor.YELLOW + target.getName() + " is now frozen for " +
                    DurationFormatUtils.formatDurationWords(freezeTicks, true, true) + ".");
        }

        return true;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Player) {
            Player player = (Player) entity;
            if ((getRemainingServerFrozenMillis() > 0L || getRemainingPlayerFrozenMillis(player.getUniqueId()) > 0L) && (!player.hasPermission(FREEZE_BYPASS))) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "You cannot use commands whilst frozen.");
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPreCommandProcess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if ((getRemainingServerFrozenMillis() > 0L || getRemainingPlayerFrozenMillis(player.getUniqueId()) > 0L) && (!player.hasPermission(FREEZE_BYPASS))) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You cannot use commands whilst frozen.");
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerMove(PlayerMoveEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();

        // The player didn't move a block.
        if (from.getBlockX() == to.getBlockX() && from.getBlockZ() == to.getBlockZ()) {
            return;
        }

        Player player = event.getPlayer();
        if ((getRemainingServerFrozenMillis() > 0L || getRemainingPlayerFrozenMillis(player.getUniqueId()) > 0L) && (!player.hasPermission(FREEZE_BYPASS))) {
            event.setTo(event.getFrom());
        }
    }

    /**
     * Gets the remaining time the server is frozen.
     *
     * @return the remaining time in milliseconds
     */
    public long getRemainingServerFrozenMillis() {
        return serverFrozenMillis - System.currentTimeMillis();
    }

    /**
     * Gets the remaining time a player is frozen.
     *
     * @param uuid the uuid of player to get for
     * @return the remaining time in milliseconds
     */
    public long getRemainingPlayerFrozenMillis(UUID uuid) {
        return frozenPlayers.containsKey(uuid) ? frozenPlayers.get(uuid) - System.currentTimeMillis() : 0L;
    }
}
