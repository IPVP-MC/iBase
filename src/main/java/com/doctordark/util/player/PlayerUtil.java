package com.doctordark.util.player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.doctordark.base.BasePlugin;
import com.google.common.collect.Maps;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by J on 7/16/2015.
 */
public class PlayerUtil {
    private static final Map<Player, Location> frozen = Maps.newHashMap();
    private static final Map<Player, PlayerCache> playerCaches = Maps.newHashMap();
    private static final Map<Player, Long> lastSent = Maps.newHashMap();
    static {
        Bukkit.getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onMove(PlayerMoveEvent event) {
                Location from = event.getFrom();
                Location to = event.getTo();
                if (from.getBlockX() != to.getBlockX() || from.getBlockZ() != to.getBlockZ()) {
                    Player player = event.getPlayer();
                    Location location = frozen.get(player);
                    if (location != null) {
                        if (to.getBlockX() != location.getBlockX() || to.getBlockZ() != location.getBlockZ() ||
                                Math.abs(to.getBlockY() - location.getBlockY()) >= 2) {
                            location.setYaw(to.getYaw());
                            location.setPitch(to.getPitch());
                            event.setTo(location);
                            if(lastSent.containsKey(player) && new Date().getTime() - lastSent.get(player) <= 3000) {
                                return;
                            }
                            player.sendMessage(ChatColor.YELLOW + "You are currently " + ChatColor.AQUA + "frozen" +
                                    ChatColor.YELLOW + "!");
                        }
                    }
                }
            }

            @EventHandler
            public void onQuit(PlayerQuitEvent event) {
                frozen.remove(event.getPlayer());
            }
        }, BasePlugin.getPlugin());
    }

    public static void respawn(Player player) {
        PacketContainer packet = new PacketContainer(PacketType.Play.Client.CLIENT_COMMAND);
        packet.getClientCommands().writeSafely(0, EnumWrappers.ClientCommand.PERFORM_RESPAWN);
        try {
            ProtocolLibrary.getProtocolManager().recieveClientPacket(player, packet);
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void wipe(Player player) {
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.setExp(0);
        player.setLevel(0);
        player.setHealth(player.getMaxHealth());
        player.setFoodLevel(20);
        player.setRemainingAir(player.getMaximumAir());
        player.setFireTicks(0);
        player.setGameMode(GameMode.SURVIVAL);
        player.setAllowFlight(false);
        player.setFlying(false);
        for(PotionEffect pe : player.getActivePotionEffects()) {
            player.removePotionEffect(pe.getType());
        }
    }

    public static void freeze(Player player) {
        frozen.put(player, player.getLocation());
    }

    public static boolean thaw(Player player) {
        return frozen.remove(player) != null;
    }

    public static boolean isFrozen(Player player) {
        return frozen.containsKey(player);
    }

    public static void cache(Player player) {
        playerCaches.put(player, new PlayerCache(player));
    }

    public static void restore(Player player) {
        PlayerCache playerCache = playerCaches.get(player);
        if (playerCache != null) {
            playerCache.apply(player);
        }
    }

    public static PlayerCache getCache(Player player) {
        return playerCaches.get(player);
    }
}