package org.ipvp.ibasic.freeze;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.libs.joptsimple.internal.Strings;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.ipvp.ibasic.IBasic;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FreezeListener implements Listener {

    private static final UUID CONSOLE_UUID = UUID.fromString("deebf69f-0240-4db5-b862-181063d4c827");
    private static final float DEFAULT_WALK_SPEED = 0.2F;

    private static final PotionEffect SLOW_EFFECT = new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 128);
    private static final PotionEffect JUMP_EFFECT = new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 128);
    private static final PotionEffect BLIND_EFFECT = new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 128);

    @Setter
    @Getter
    private boolean serverFrozen = false;

    private final Map<Player, FreezeData> freezeDataMap = new HashMap<>();
    private final IBasic plugin;

    public FreezeListener(IBasic plugin) {
        this.plugin = plugin;
    }

    public boolean isFrozen(Player player) {
        return this.getFreezeType(player) != null && !player.hasPermission("freeze.bypass");
    }

    public FreezeType getFreezeType(Player player) {
        if (this.serverFrozen) {
            return FreezeType.FREEZE;
        }

        FreezeData freezeData = this.freezeDataMap.get(player);
        if (freezeData != null) {
            return freezeData.getFreezeType();
        }

        return null;
    }

    public boolean freeze(Player player, CommandSender freezer, FreezeType freezeType) {
        if (FreezeType.HALT != freezeType // allow halt to override previous freeze type.
                && this.freezeDataMap.containsKey(player)) {
            return false;
        }

        player.setWalkSpeed(0.0F);
        player.addPotionEffect(JUMP_EFFECT);
        player.addPotionEffect(SLOW_EFFECT);
        if (true/*Needed to stop jump sprint moving   freezeType == FreezeType.HALT*/) {
            player.addPotionEffect(BLIND_EFFECT);
        }

        HaltMessageRunnable haltMessageRunnable;
        if (freezeType == FreezeType.HALT) {
            haltMessageRunnable = new HaltMessageRunnable(player, freezer.getName());
            haltMessageRunnable.runTaskTimer(plugin, 1L, 160L);
        } else haltMessageRunnable = null;

        UUID freezerUUID = freezer instanceof Player ? ((Player) freezer).getUniqueId() : CONSOLE_UUID;
        this.freezeDataMap.put(player, new FreezeData(freezer.getName(), freezerUUID, freezeType, haltMessageRunnable));
        return true;
    }

    public FreezeData unfreeze(Player player) {
        return this.unfreeze(player, null);
    }

    public FreezeData unfreeze(Player player, @Nullable FreezeType freezeType) {
        FreezeData freezeData = this.freezeDataMap.get(player);
        if (freezeData != null) {
            if (freezeType == null || (freezeData.getFreezeType() == freezeType)) {
                player.setWalkSpeed(DEFAULT_WALK_SPEED);
                player.removePotionEffect(JUMP_EFFECT.getType());
                player.removePotionEffect(SLOW_EFFECT.getType());
                if (true/*Needed to stop jump sprint moving   freezeData.getFreezeType() == FreezeType.HALT*/) {
                    player.removePotionEffect(BLIND_EFFECT.getType());
                }

                freezeData.cancel();
                this.freezeDataMap.remove(player);
            }
        }

        return freezeData;
    }

    private void checkHasDisconnected(Player player) {
        FreezeData freezeData = this.unfreeze(player);
        if (freezeData != null && freezeData.getFreezeType() == FreezeType.HALT) {
            for (Player target : Bukkit.getOnlinePlayers()) {
                if (target.hasPermission("ibasic.freeze")) {
                    target.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + player.getName() + " has logged off.");
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerKickMonitor(PlayerKickEvent event) {
        this.checkHasDisconnected(event.getPlayer());
    }

    @EventHandler(ignoreCancelled = false, priority = EventPriority.HIGH)
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.checkHasDisconnected(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (this.isFrozen(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInteract(PlayerInteractEvent event) {
        if (this.isFrozen(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityDamage(EntityDamageEvent event) {
        Player player = null;
        Entity entity = event.getEntity();
        if (entity instanceof Player) {
            player = (Player) entity;
            if (this.isFrozen(player)) {
                event.setCancelled(true);
            }
        }

        if (event instanceof EntityDamageByEntityEvent) {
            Entity damager = ((EntityDamageByEntityEvent) event).getDamager();
            if (damager instanceof Player && this.isFrozen((Player) damager)) {
                event.setCancelled(true);
            }

            if (damager instanceof Player && player != null) {
                Player attacker = (Player) damager;
                FreezeType freezeType = this.getFreezeType(player);
                if (freezeType == FreezeType.HALT) {
                    attacker.sendMessage(ChatColor.RED + "This player has been frozen by a staff member and is being checked for possible hacks.");
                }
            }
        }
    }

    @Data
    public static class FreezeData {

        private final String freezingPlayerName;
        private final UUID freezingPlayerUUID;
        private final FreezeType freezeType;
        private final
        @Nullable
        HaltMessageRunnable haltMessageRunnable;

        public void cancel() {
            if (this.haltMessageRunnable != null) {
                this.haltMessageRunnable.cancel();
            }
        }
    }

    public static class HaltMessageRunnable extends BukkitRunnable {

        private static final String HEADER = ChatColor.STRIKETHROUGH + Strings.repeat('=', 45);

        private final Player player;
        private final String freezingPlayerName;
        private String[] messages; // lazy-load

        public HaltMessageRunnable(Player player, String freezingPlayerName) {
            this.player = player;
            this.freezingPlayerName = freezingPlayerName;
        }

        @Override
        public void run() {
            if (this.messages == null) {
                this.messages = Iterables.toArray(Lists.newArrayList(
                        ChatColor.GOLD.toString() + ChatColor.BOLD + HEADER,
                        ChatColor.RED.toString() + ChatColor.BOLD + "You have been frozen by " + freezingPlayerName + ".",
                        ChatColor.GREEN.toString() + ChatColor.BOLD + "You have 3 minutes to join the Teamspeak server.",
                        ChatColor.YELLOW.toString() + ChatColor.BOLD + "Teamspeak IP: ts.ipvp.org",
                        ChatColor.AQUA.toString() + ChatColor.BOLD + "Do not log off or you will be banned.",
                        ChatColor.GOLD.toString() + ChatColor.BOLD + HEADER
                ), String.class);
            }
            this.player.sendMessage(this.messages);
            this.player.playSound(this.player.getLocation(), Sound.NOTE_PLING, 10F, 10F);
        }
    }
}
