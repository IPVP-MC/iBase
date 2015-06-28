package com.doctordark.util.cuboid;

import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.Iterator;

/**
 * Iterates through {@link Block}s in a {@link Cuboid}.
 *
 * @author desht
 */
public class CuboidBlockIterator implements Iterator<Block> {

    private final World world;
    private final int baseX, baseY, baseZ;
    private final int sizeX, sizeY, sizeZ;

    private int x, y, z;

    public CuboidBlockIterator(World world, int x1, int y1, int z1, int x2, int y2, int z2) {
        this.world = world;

        this.baseX = x1;
        this.baseY = y1;
        this.baseZ = z1;

        this.sizeX = Math.abs(x2 - x1) + 1;
        this.sizeY = Math.abs(y2 - y1) + 1;
        this.sizeZ = Math.abs(z2 - z1) + 1;

        x = y = z = 0;
    }

    /**
     * Checks if this {@link CuboidBlockIterator} has a new {@link Block}.
     *
     * @return true if {@link CuboidBlockIterator} has next {@link Block}
     */
    @Override
    public boolean hasNext() {
        return x < sizeX && y < sizeY && z < sizeZ;
    }

    /**
     * Gets the next {@link Block} in {@link CuboidBlockIterator}.
     *
     * @return the next {@link Block} in {@link CuboidBlockIterator}
     */
    @Override
    public Block next() {
        Block block = world.getBlockAt(baseX + x, baseY + y, baseZ + z);

        if (x++ >= sizeX) {
            x = 0;
            if (y++ >= sizeY) {
                y = 0;
                z++;
            }
        }

        return block;
    }

    /**
     * Removes this {@link Block} from {@link CuboidBlockIterator}.
     */
    @Override
    public void remove() {

    }
}