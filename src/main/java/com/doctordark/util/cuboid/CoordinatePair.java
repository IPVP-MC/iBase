package com.doctordark.util.cuboid;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;

/**
 * Represents a pair of co-ordinates on a Minecraft server storing the {@link World}, the x and the z co-ordinates.
 */
public class CoordinatePair {

    private final String worldName;
    private final int x;
    private final int z;

    /**
     * Constructs a new {@link CoordinatePair} with a given {@link Block}.
     *
     * @param block the {@link Block} to construct with
     */
    public CoordinatePair(Block block) {
        this(block.getWorld(), block.getX(), block.getZ());
    }

    /**
     * Constructs a new {@link CoordinatePair} with given {@link World}, x and z co-ordinate.
     *
     * @param world the {@link World} to construct with
     * @param x     the x to construct with
     * @param z     the z to construct with
     */
    public CoordinatePair(World world, int x, int z) {
        this.worldName = world.getName();
        this.x = x;
        this.z = z;
    }

    /**
     * Gets the name of this {@link World} for this {@link CoordinatePair}.
     *
     * @return the {@link World} name
     */
    public String getWorldName() {
        return worldName;
    }

    /**
     * Gets {@link World} of this {@link CoordinatePair}.
     *
     * @return the {@link World} instance
     */
    public World getWorld() {
        return Bukkit.getWorld(worldName);
    }

    /**
     * Gets the x co-ordinate of this {@link CoordinatePair}.
     *
     * @return the x co-ordinate
     */
    public int getX() {
        return x;
    }

    /**
     * Gets the z co-ordinate of this {@link CoordinatePair}.
     *
     * @return the z co-ordinate
     */
    public int getZ() {
        return z;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CoordinatePair)) return false;

        CoordinatePair that = (CoordinatePair) o;

        if (x != that.x) return false;
        if (z != that.z) return false;
        return !(worldName != null ? !worldName.equals(that.worldName) : that.worldName != null);
    }

    @Override
    public int hashCode() {
        int result = worldName != null ? worldName.hashCode() : 0;
        result = 31 * result + x;
        result = 31 * result + z;
        return result;
    }
}
