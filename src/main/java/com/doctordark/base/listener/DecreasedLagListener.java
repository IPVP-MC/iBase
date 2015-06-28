package com.doctordark.base.listener;

import com.doctordark.base.BasePlugin;
import com.doctordark.base.command.BaseCommand;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class DecreasedLagListener implements Listener {

    private final BasePlugin plugin;

    public DecreasedLagListener(BasePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (plugin.getServerHandler().isDecreasedLagMode()) {
            Player player = event.getPlayer();
            BaseCommand baseCommand = plugin.getCommandManager().getCommand("stoplag");
            if (player.hasPermission(baseCommand.getPermission())) {
                event.getPlayer().sendMessage(ChatColor.YELLOW + "Intensive server activity is currently prevented. Use /" + baseCommand.getName() + " to toggle.");
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockBurn(BlockBurnEvent event) {
        if (plugin.getServerHandler().isDecreasedLagMode()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockPhysics(BlockPhysicsEvent event) {
        if (plugin.getServerHandler().isDecreasedLagMode()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onLeavesDecay(LeavesDecayEvent event) {
        if (plugin.getServerHandler().isDecreasedLagMode()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockForm(BlockFormEvent event) {
        if (plugin.getServerHandler().isDecreasedLagMode()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockSpread(BlockSpreadEvent event) {
        if (plugin.getServerHandler().isDecreasedLagMode()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockIgnite(BlockIgniteEvent event) {
        if (plugin.getServerHandler().isDecreasedLagMode()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockFromTo(BlockFromToEvent event) {
        if (plugin.getServerHandler().isDecreasedLagMode()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityExplode(EntityExplodeEvent event) {
        if (plugin.getServerHandler().isDecreasedLagMode()) {
            event.getEntity().remove();
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onExplosionPrime(ExplosionPrimeEvent event) {
        if (plugin.getServerHandler().isDecreasedLagMode()) {
            event.getEntity().remove();
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (plugin.getServerHandler().isDecreasedLagMode()) {
            switch (event.getSpawnReason()) {
                case SPAWNER:
                case SPAWNER_EGG:
                case BUILD_SNOWMAN:
                case BUILD_IRONGOLEM:
                case BUILD_WITHER:
                case DISPENSE_EGG:
                    break;
                default:
                    event.setCancelled(true);
                    break;
            }
        }
    }
}
