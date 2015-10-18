package com.doctordark.util.cuboid;

import org.bukkit.block.BlockFace;

/**
 * Represents the {@link org.bukkit.block.Block} direction of a {@link Cuboid}.
 *
 * @author desht
 */
public enum CuboidDirection {

    NORTH, EAST, SOUTH, WEST, UP, DOWN, HORIZONTAL, VERTICAL, BOTH, UNKNOWN;

    /**
     * Gets the opposite {@link CuboidDirection} for this {@link CuboidDirection}.
     *
     * @return the opposite {@link CuboidDirection}
     */
    public CuboidDirection opposite() {
        switch (this) {
            case NORTH:
                return SOUTH;
            case EAST:
                return WEST;
            case SOUTH:
                return NORTH;
            case WEST:
                return EAST;
            case HORIZONTAL:
                return VERTICAL;
            case VERTICAL:
                return HORIZONTAL;
            case UP:
                return DOWN;
            case DOWN:
                return UP;
            case BOTH:
                return BOTH;
            default:
                return UNKNOWN;
        }
    }

    /**
     * Converts this {@link CuboidDirection} to a {@link BlockFace}.
     *
     * @return the {@link BlockFace} direction
     */
    public BlockFace toBukkitDirection() {
        switch (this) {
            case NORTH:
                return BlockFace.NORTH;
            case EAST:
                return BlockFace.EAST;
            case SOUTH:
                return BlockFace.SOUTH;
            case WEST:
                return BlockFace.WEST;
            case UP:
                return BlockFace.UP;
            case DOWN:
                return BlockFace.DOWN;
            case HORIZONTAL:
            case VERTICAL:
            case BOTH:
            default:
                return null;
        }
    }
}