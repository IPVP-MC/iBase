package me.poisonex.plugins.ibasic.freeze;

import me.poisonex.plugins.ibasic.IBasic;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.permissions.Permissible;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;

import java.util.UUID;

public class FreezeListener implements Listener {

    private final IBasic plugin;

    public FreezeListener(IBasic plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = false, priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.getPlayer().removePotionEffect(PotionEffectType.BLINDNESS);
    }

    @EventHandler(ignoreCancelled = false, priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        plugin.getFreezeManager().getFrozenPlayers().remove(uuid);
        if (plugin.getFreezeManager().getHaltedPlayers().remove(uuid) != null) {
            event.getPlayer().removePotionEffect(PotionEffectType.BLINDNESS);

            String message = ChatColor.RED.toString() + ChatColor.BOLD + event.getPlayer().getName() + " has logged off.";
            for (Permissible permissible : this.plugin.getServer().getPluginManager().getPermissionSubscriptions("ibasic.freeze")) {
                if (permissible instanceof CommandSender) {
                    ((CommandSender) permissible).sendMessage(message);
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = false, priority = EventPriority.HIGHEST)
    public void onPlayerKick(PlayerKickEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (StringUtils.containsIgnoreCase("Kicked for flying or related", event.getReason()) &&
                plugin.getFreezeManager().isFrozen(event.getPlayer()) || plugin.getFreezeManager().getHaltedPlayers().get(event.getPlayer().getUniqueId()) != null) {

            event.setCancelled(true);
            return;
        }

        if (plugin.getFreezeManager().getHaltedPlayers().remove(uuid) != null) {
            player.removePotionEffect(PotionEffectType.BLINDNESS);

            String message = ChatColor.RED.toString() + ChatColor.BOLD + player.getName() + " has logged off.";
            for (Permissible permissible : this.plugin.getServer().getPluginManager().getPermissionSubscriptions("ibasic.freeze")) {
                if (permissible instanceof CommandSender) {
                    ((CommandSender) permissible).sendMessage(message);
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerMove(PlayerMoveEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();

        if (to.getX() != from.getX() || to.getZ() != from.getZ() && plugin.getFreezeManager().isFrozen(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = false, priority = EventPriority.LOWEST)
    public void onTeleport(PlayerTeleportEvent event) {
        if (plugin.getFreezeManager().isFrozen(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = false, priority = EventPriority.LOWEST)
    public void onInteract(PlayerInteractEvent event) {
        if (plugin.getFreezeManager().isFrozen(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = false, priority = EventPriority.LOWEST)
    public void onEntityDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Player) {
            Player player = (Player) entity;

            if (plugin.getFreezeManager().isFrozen(player)) {
                event.setCancelled(true);

                if (event instanceof EntityDamageByEntityEvent) {
                    Entity damager = ((EntityDamageByEntityEvent) event).getDamager();
                    Player attacker = null;
                    if (damager instanceof Player) {
                        attacker = (Player) damager;
                    } else if (damager instanceof Projectile) {
                        ProjectileSource source = ((Projectile) damager).getShooter();
                        if (source instanceof Player) {
                            attacker = (Player) source;
                        }
                    }


                    if (attacker != null && plugin.getFreezeManager().getHaltedPlayers().get(player.getUniqueId()) != null) {
                        attacker.sendMessage(ChatColor.RED + "This player has been frozen by a staff member and is being checked for possible hacks.");
                    }
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = false, priority = EventPriority.LOWEST)
    public void onDamageByEntity(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        if (damager instanceof Player && plugin.getFreezeManager().isFrozen((Player) damager)) {
            event.setCancelled(true);
        }
    }
}
