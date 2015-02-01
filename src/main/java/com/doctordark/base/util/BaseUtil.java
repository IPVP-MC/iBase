package com.doctordark.base.util;

import com.doctordark.base.BasePlugin;
import net.minecraft.server.v1_7_R4.EntityPlayer;
import net.minecraft.server.v1_7_R4.MinecraftServer;
import net.minecraft.server.v1_7_R4.PlayerConnection;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Statistic;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_7_R4.CraftServer;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;

/**
 * Utility class for the Base plugin.
 */
public final class BaseUtil {

    private BaseUtil() {

    }

    public static long getIdleTime(Player player) {
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        return (entityPlayer.x() <= 0L) ? 0L : MinecraftServer.ar() - entityPlayer.x();
    }

    public static Integer getInteger(String string) {
        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    public static boolean containsIgnoreCase(Collection collection, String string) {
        for (Object object : collection) {
            if (object instanceof String) {
                String next = (String) object;
                if (StringUtils.containsIgnoreCase(next, string)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Gets the total play time of a player.
     *
     * @param player the player to get for
     * @return the total playtime of player
     */
    public static long getTotalPlayTime(Player player) {
        return player.getStatistic(Statistic.PLAY_ONE_TICK) * 20;
    }

    /**
     * Gets the display name of a sender or their
     * regular name if they don't have a display name.
     *
     * @param sender the sender to get for
     * @return the display name of sender
     */
    public static String getDisplayName(CommandSender sender) {
        return (sender instanceof Player) ? ((Player) sender).getDisplayName() : sender.getName();
    }

    /**
     * Gets the final attacker from the damage event including
     * projectiles usage and everything else.
     *
     * @param originalEvent the original damage event
     * @return the final attacker from event
     */
    public static Player getFinalAttacker(EntityDamageEvent originalEvent) {
        if (!(originalEvent instanceof EntityDamageByEntityEvent)) {
            return null;
        }

        EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) originalEvent;
        if (event.getDamager() instanceof Player) {
            return (Player) event.getDamager();
        } else if (event.getDamager() instanceof Projectile) {
            Projectile projectile = (Projectile) event.getDamager();
            if (projectile.getShooter() instanceof Player) {
                return (Player) projectile.getShooter();
            }
        }

        return null;
    }

    /**
     * Gets the highest block at a given location.
     *
     * @param location the origin location
     * @return the highest location from an origin
     */
    public static Location getHighestBlock(Location location) {
        Validate.notNull(location, "The location cannot be null");

        Location cloned = location.clone();
        World world = cloned.getWorld();
        Location foundLoc = null;

        // Check for bedrock in nether // overworld too.
        for (int i = 256; i > 0; i--) {
            cloned.setY(i);
            Block below = cloned.getBlock().getRelative(BlockFace.DOWN);

            if (below != null && !below.isEmpty()) {
                foundLoc = cloned;
                break;
            }
        }

        if (foundLoc == null || (cloned.getY() - location.getY()) < 2) {
            return null;
        } else {
            cloned.setX(Math.round(cloned.getBlockX() - 0.25) + 0.25);
            cloned.setY(Math.round(cloned.getBlockY() - 0.25) + 0.25);
            cloned.setZ(Math.round(cloned.getBlockZ() - 0.25) + 0.25);
            return cloned;
        }
    }
}
