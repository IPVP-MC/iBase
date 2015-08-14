package com.doctordark.util.player;

import com.doctordark.base.BasePlugin;
import net.minecraft.server.v1_7_R4.EnumClientCommand;
import net.minecraft.server.v1_7_R4.PacketPlayInClientCommand;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
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
                Player player = event.getPlayer();
                if(frozen.containsKey(player)) {
                    Location location = frozen.get(player);
                    Location to = event.getTo();
                    if (to.getBlockX() != location.getBlockX() || to.getBlockZ() != location.getBlockZ() ||
                            Math.abs(to.getBlockY() - location.getBlockY()) >= 2) {
                        event.setTo(location);
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
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayInClientCommand(EnumClientCommand.PERFORM_RESPAWN));
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
