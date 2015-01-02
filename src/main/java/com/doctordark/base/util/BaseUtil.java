package com.doctordark.base.util;

import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import javax.swing.text.html.parser.Entity;

/**
 * Utility class for the Base plugin.
 */
public class BaseUtil {

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
