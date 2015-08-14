package com.doctordark.util.player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.doctordark.base.BasePlugin;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by J on 7/16/2015.
 */
public class PlayerUtil {
    private static Map<Player, Location> frozen = new HashMap<>();
    private static Map<Player, PlayerCache> playerCaches = new HashMap<>();
    static {
        Bukkit.getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onMove(PlayerMoveEvent event) {
                if(frozen.containsKey(event.getPlayer()) && (event.getTo().getBlockX() != frozen.get(event.getPlayer()).getBlockX() ||
                        event.getTo().getBlockZ() != frozen.get(event.getPlayer()).getBlockZ() ||
                        Math.abs(event.getTo().getBlockY() - frozen.get(event.getPlayer()).getBlockY()) >= 2)) {
                    event.setTo(frozen.get(event.getPlayer()));
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
        } catch (Exception ex) {
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

    public static void thaw(Player player) {
        frozen.remove(player);
    }

    public static boolean isFrozen(Player player) {
        return frozen.containsKey(player);
    }

    public static void cache(Player player) {
        playerCaches.put(player, new PlayerCache(player));
    }

    public static void restore(Player player) {
        if(!playerCaches.containsKey(player)) {
            return;
        }
        playerCaches.get(player).apply(player);
    }
}
