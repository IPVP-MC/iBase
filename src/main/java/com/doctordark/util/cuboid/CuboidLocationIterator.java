package com.doctordark.util.cuboid;

import org.bukkit.Location;
import org.bukkit.World;

import java.util.Iterator;

/**
 * Iterates through {@link Location}s in a {@link Cuboid}.
 *
 * @author DoctorDark
 */
public class CuboidLocationIterator implements Iterator<Location> {

    private final World world;
    private final int baseX, baseY, baseZ;
    private final int sizeX, sizeY, sizeZ;

    private int x, y, z;

    public CuboidLocationIterator(World world, int x1, int y1, int z1, int x2, int y2, int z2) {
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
     * Checks if this {@link CuboidLocationIterator} has a new {@link Location}.
     *
     * @return true if {@link CuboidLocationIterator} has next {@link Location}
     */
    @Override
    public boolean hasNext() {
        return x < sizeX && y < sizeY && z < sizeZ;
    }

    /**
     * Gets the next {@link Location} in {@link CuboidLocationIterator}.
     *
     * @return the next {@link Location} in {@link CuboidLocationIterator}
     */
    @Override
    public Location next() {
        Location location = new Location(world, baseX + x, baseY + y, baseZ + z);

        if (++x >= sizeX) {
            x = 0;
            if (++y >= sizeY) {
                y = 0;
                ++z;
            }
        }

        return location;
    }

    /**
     * Removes this {@link Location} from this {@link CuboidLocationIterator}.
     */
    @Override
    public void remove() {

    }
}