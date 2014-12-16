package com.doctordark.base.util;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Utility class for the Base plugin.
 */
public class BaseUtil {

    public static String getDisplayName(CommandSender sender) {
        return (sender instanceof Player) ? ((Player) sender).getDisplayName() : sender.getName();
    }

    public static Location getHighestBlock(Location location) {
        Validate.notNull(location, "The location cannot be null");

        Location cloned = location.clone();
        World world = cloned.getWorld();
        int maxHeight = world.getMaxHeight();
        int x = cloned.getBlockX();
        int y = cloned.getBlockY();
        int z = cloned.getBlockZ();
        float yaw = cloned.getYaw();
        float pitch = cloned.getPitch();

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

        //Bukkit.broadcastMessage("CY:" + cloned.getY() + ", LY:" + location.getY());

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
