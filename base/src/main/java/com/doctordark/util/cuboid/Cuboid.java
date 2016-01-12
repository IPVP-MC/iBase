package com.doctordark.util.cuboid;

import com.google.common.base.Preconditions;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Represents a {@link Cuboid} used for regions.
 *
 * @author desht
 */
public class Cuboid implements Iterable<Block>, Cloneable, ConfigurationSerializable {

    protected final String worldName;
    protected int x1, y1, z1, x2, y2, z2;

    /**
     * Constructs a {@link Cuboid} from a map.
     *
     * @param map the map to construct from
     */
    public Cuboid(Map<String, Object> map) {
        this.worldName = (String) map.get("worldName");
        this.x1 = (Integer) map.get("x1");
        this.y1 = (Integer) map.get("y1");
        this.z1 = (Integer) map.get("z1");
        this.x2 = (Integer) map.get("x2");
        this.y2 = (Integer) map.get("y2");
        this.z2 = (Integer) map.get("z2");
    }

    /**
     * Construct a {@link Cuboid} in the given {@link World} and xyz co-ordinates
     *
     * @param world the {@link Cuboid} world
     * @param x1    X co-ordinate of corner 1
     * @param y1    Y co-ordinate of corner 1
     * @param z1    Z co-ordinate of corner 1
     * @param x2    X co-ordinate of corner 2
     * @param y2    Y co-ordinate of corner 2
     * @param z2    Z co-ordinate of corner 2
     */
    public Cuboid(World world, int x1, int y1, int z1, int x2, int y2, int z2) {
        this(Preconditions.checkNotNull(world).getName(), x1, y1, z1, x2, y2, z2);
    }

    /**
     * Construct a {@link Cuboid} in the given {@link World} name and xyz co-ordinates.
     *
     * @param worldName the {@link Cuboid} world name
     * @param x1        X co-ordinate of corner 1
     * @param y1        Y co-ordinate of corner 1
     * @param z1        Z co-ordinate of corner 1
     * @param x2        X co-ordinate of corner 2
     * @param y2        Y co-ordinate of corner 2
     * @param z2        Z co-ordinate of corner 2
     */
    private Cuboid(String worldName, int x1, int y1, int z1, int x2, int y2, int z2) {
        Preconditions.checkNotNull(worldName, "World name cannot be null");

        this.worldName = worldName;
        this.x1 = Math.min(x1, x2);
        this.y1 = Math.min(y1, y2);
        this.z1 = Math.min(z1, z2);
        this.x2 = Math.max(x1, x2);
        this.y2 = Math.max(y1, y2);
        this.z2 = Math.max(z1, z2);
    }

    /**
     * Constructs a {@link Cuboid} given two {@link Location}s which
     * represent any two corners of the {@link Cuboid}.
     *
     * @param first  one of the corners
     * @param second the other corner
     */
    public Cuboid(Location first, Location second) {
        Preconditions.checkNotNull(first, "Location 1 cannot be null");
        Preconditions.checkNotNull(second, "Location 2 cannot be null");
        Preconditions.checkArgument(first.getWorld().equals(second.getWorld()), "Locations must be on the same world");

        this.worldName = first.getWorld().getName();
        this.x1 = Math.min(first.getBlockX(), second.getBlockX());
        this.y1 = Math.min(first.getBlockY(), second.getBlockY());
        this.z1 = Math.min(first.getBlockZ(), second.getBlockZ());
        this.x2 = Math.max(first.getBlockX(), second.getBlockX());
        this.y2 = Math.max(first.getBlockY(), second.getBlockY());
        this.z2 = Math.max(first.getBlockZ(), second.getBlockZ());
    }

    /**
     * Construct a one-{@link Block} {@link Cuboid} at the given {@link Location} of
     * the {@link Cuboid}.
     *
     * @param location the {@link Location} of the {@link Cuboid}
     */
    public Cuboid(Location location) {
        this(location, location);
    }

    /**
     * Constructor to copy another {@link Cuboid}.
     *
     * @param other the {@link Cuboid} to copy
     */
    public Cuboid(Cuboid other) {
        this(other.getWorld().getName(), other.x1, other.y1, other.z1, other.x2, other.y2, other.z2);
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("worldName", this.worldName);
        map.put("x1", this.x1);
        map.put("y1", this.y1);
        map.put("z1", this.z1);
        map.put("x2", this.x2);
        map.put("y2", this.y2);
        map.put("z2", this.z2);
        return map;
    }

    /**
     * Checks if this {@link Cuboid} has both the minimum and maximum {@link Location}s set.
     *
     * @return true if this {@link Cuboid} has both positions set
     */
    public boolean hasBothPositionsSet() {
        return this.getMinimumPoint() != null && this.getMaximumPoint() != null;
    }

    /**
     * Gets the minimum x co-ordinate of this {@link Cuboid}.
     *
     * @return the minimum x co-ordinate
     */
    public int getMinimumX() {
        return Math.min(this.x1, this.x2);
    }

    /**
     * Gets the minimum z co-ordinate of this {@link Cuboid}.
     *
     * @return the minimum z co-ordinate
     */
    public int getMinimumZ() {
        return Math.min(this.z1, this.z2);
    }

    /**
     * Gets the maximum x co-ordinate of this {@link Cuboid}.
     *
     * @return the maximum x co-ordinate
     */
    public int getMaximumX() {
        return Math.max(this.x1, this.x2);
    }

    /**
     * Gets the maximum z co-ordinate of this {@link Cuboid}.
     *
     * @return the maximum z co-ordinate
     */
    public int getMaximumZ() {
        return Math.max(this.z1, this.z2);
    }

    /**
     * Given {@link Vector}s v1 and v2 (which must be sorted), return the
     * {@link Vector}s which represent all the points along
     * the edges of the {@link Cuboid} formed by v1 and v2.
     *
     * @return the edges of this {@link Cuboid}
     */
    public List<Vector> edges() {
        return this.edges(-1, -1, -1, -1);
    }

    /**
     * Given {@link Vector}s v1 and v2 (which must be sorted), return the
     * {@link Vector}s which represent all the points along
     * the edges of the {@link Cuboid} formed by v1 and v2.
     *
     * @return the edges of this {@link Cuboid}
     */
    public List<Vector> edges(int fixedMinX, int fixedMaxX, int fixedMinZ, int fixedMaxZ) {
        Vector v1 = getMinimumPoint();
        Vector v2 = getMaximumPoint();

        final int minX = v1.getBlockX();
        final int maxX = v2.getBlockX();
        final int minZ = v1.getBlockZ();
        final int maxZ = v2.getBlockZ();

        int capacity = ((maxX - minX) * 4) + ((maxZ - minZ) * 4);
        capacity += 4; // we do this because the second loop doesn't check '<=' but just '<'.

        List<Vector> result = new ArrayList<>(capacity);
        if (capacity <= 0) return result;

        final int minY = v1.getBlockY();
        final int maxY = v1.getBlockY();

        for (int x = minX; x <= maxX; x++) {
            result.add(new Vector(x, minY, minZ));
            result.add(new Vector(x, minY, maxZ));
            result.add(new Vector(x, maxY, minZ));
            result.add(new Vector(x, maxY, maxZ));
        }

        for (int z = minZ; z <= maxZ; z++) {
            result.add(new Vector(minX, minY, z));
            result.add(new Vector(minX, maxY, z));
            result.add(new Vector(maxX, minY, z));
            result.add(new Vector(maxX, maxY, z));
        }

        return result;
    }

    /**
     * Gets the {@link Player}s inside this {@link Cuboid}.
     *
     * @return set of {@link Player}s in {@link Cuboid}
     */
    public Set<Player> getPlayers() {
        Set<Player> players = new HashSet<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (this.contains(player)) {
                players.add(player);
            }
        }

        return players;
    }

    /**
     * Gets the {@link Location} of the lower North East
     * corner of this {@link Cuboid} (minimum XYZ co-ordinates).
     *
     * @return the {@link Location} of the lower North East corner
     */
    public Location getLowerNE() {
        return new Location(getWorld(), this.x1, this.y1, this.z1);
    }

    /**
     * Gets the {@link Location} of the upper South West
     * corner of this {@link Cuboid} (maximum XYZ co-ordinates).
     *
     * @return the {@link Location} of the upper South West corner
     */
    public Location getUpperSW() {
        return new Location(getWorld(), this.x2, this.y2, this.z2);
    }

    /**
     * Get the the centre {@link Location} of this {@link Cuboid}
     *
     * @return the {@link Location} at the centre
     */
    public Location getCenter() {
        int x1 = this.x2 + 1;
        int y1 = this.y2 + 1;
        int z1 = this.z2 + 1;
        return new Location(this.getWorld(),
                this.x1 + (x1 - this.x1) / 2.0,
                this.y1 + (y1 - this.y1) / 2.0,
                this.z1 + (z1 - this.z1) / 2.0);
    }

    /**
     * Gets the name of the {@link World} for this {@link Cuboid}.
     *
     * @return the {@link World} name of this {@link Cuboid}
     */
    public String getWorldName() {
        return this.worldName;
    }

    /**
     * Gets the {@link Cuboid} world.
     *
     * @return the {@link World}, or null if is not loaded
     */
    public World getWorld() {
        return Bukkit.getWorld(this.worldName);
    }

    /**
     * Gets the size of this {@link Cuboid} along the X axis.
     *
     * @return the size of {@link Cuboid} along the X axis
     */
    public int getSizeX() {
        return (this.x2 - this.x1) + 1;
    }

    /**
     * Gets the size of this {@link Cuboid} along the Y axis.
     *
     * @return the size of {@link Cuboid} along the Y axis
     */
    public int getSizeY() {
        return (this.y2 - this.y1) + 1;
    }

    /**
     * Gets the size of this {@link Cuboid} along the Z axis.
     *
     * @return the size of {@link Cuboid} along the Z axis
     */
    public int getSizeZ() {
        return (this.z2 - this.z1) + 1;
    }

    /**
     * Gets the minimum X co-ordinate of this {@link Cuboid}.
     *
     * @return the minimum X co-ordinate
     */
    public int getX1() {
        return this.x1;
    }

    /**
     * Sets the minimum X co-ordinate of this {@link Cuboid}.
     *
     * @param x1 the minimum X co-ordinate to set
     */
    public void setX1(int x1) {
        this.x1 = x1;
    }

    /**
     * Gets the minimum Y co-ordinate of this {@link Cuboid}.
     *
     * @return the minimum Y co-ordinate
     */
    public int getY1() {
        return this.y1;
    }

    /**
     * Sets the minimum Y co-ordinate of this {@link Cuboid}.
     *
     * @param y1 the minimum Y co-ordinate to set
     */
    public void setY1(int y1) {
        this.y1 = y1;
    }

    /**
     * Gets the minimum Z co-ordinate of this {@link Cuboid}.
     *
     * @return the minimum Z co-ordinate
     */
    public int getZ1() {
        return this.z1;
    }

    /**
     * Sets the minimum Z co-ordinate of this {@link Cuboid}.
     *
     * @param z1 the minimum Z co-ordinate to set
     */
    public void setZ1(int z1) {
        this.z1 = z1;
    }

    /**
     * Gets the maximum X co-ordinate of this {@link Cuboid}.
     *
     * @return the maximum X co-ordinate
     */
    public int getX2() {
        return this.x2;
    }

    /**
     * Gets the maximum Y co-ordinate of this {@link Cuboid}.
     *
     * @return the maximum Y co-ordinate
     */
    public int getY2() {
        return this.y2;
    }

    /**
     * Sets the maximum Y co-ordinate of this {@link Cuboid}.
     *
     * @param y2 the maximum Y co-ordinate to set
     */
    public void setY2(int y2) {
        this.y2 = y2;
    }

    /**
     * Gets the maximum Z co-ordinate of this {@link Cuboid}.
     *
     * @return the maximum Z co-ordinate
     */
    public int getZ2() {
        return this.z2;
    }

    /**
     * Gets the {@link Location}s at the eight corners of this {@link Cuboid}.
     *
     * @return array of {@link Location}s representing the {@link Cuboid} corners
     */
    public Location[] getCornerLocations() {
        Location[] result = new Location[8];
        Block[] cornerBlocks = this.getCornerBlocks();
        for (int i = 0; i < cornerBlocks.length; i++) {
            result[i] = cornerBlocks[i].getLocation();
        }

        return result;
    }

    /**
     * Gets the {@link Block}s at the eight corners of the {@link Cuboid}.
     *
     * @return array of {@link Block}s representing the {@link Cuboid} corners
     */
    public Block[] getCornerBlocks() {
        Block[] result = new Block[8];
        World world = this.getWorld();
        result[0] = world.getBlockAt(this.x1, this.y1, this.z1);
        result[1] = world.getBlockAt(this.x1, this.y1, this.z2);
        result[2] = world.getBlockAt(this.x1, this.y2, this.z1);
        result[3] = world.getBlockAt(this.x1, this.y2, this.z2);
        result[4] = world.getBlockAt(this.x2, this.y1, this.z1);
        result[5] = world.getBlockAt(this.x2, this.y1, this.z2);
        result[6] = world.getBlockAt(this.x2, this.y2, this.z1);
        result[7] = world.getBlockAt(this.x2, this.y2, this.z2);
        return result;
    }

    /**
     * Shifts the {@link Cuboid} in the given {@link CuboidDirection} by the given amount.
     *
     * @param direction the {@link CuboidDirection} in which to shift
     * @param amount    the number of {@link Block}s by which to shift
     * @return new {@link Cuboid} shifted by the given {@link CuboidDirection} and amount
     */
    public Cuboid shift(CuboidDirection direction, int amount) throws IllegalArgumentException {
        return this.expand(direction, amount).expand(direction.opposite(), -amount);
    }

    /**
     * Inset (shrink) the {@link Cuboid} in the given direction by the given amount.  Equivalent
     * to calling {code outset()} with a negative amount.
     *
     * @param direction the {@link CuboidDirection} in which to inset (must be HORIZONTAL, VERTICAL, or BOTH)
     * @param amount    the number of {@link Block}s by which to inset
     * @return a new {@link Cuboid} inset by the given {@link CuboidDirection} and amount
     */
    public Cuboid inset(CuboidDirection direction, int amount) throws IllegalArgumentException {
        return this.outset(direction, -amount);
    }

    /**
     * Expand the {@link Cuboid} in the given {@link CuboidDirection} by the given amount.
     * Negative amounts will shrink the {@link Cuboid} in the given {@link CuboidDirection}.
     * Shrinking a {@link Cuboid}s' face past the opposite face is not an error and will return a valid {@link Cuboid}.
     *
     * @param direction the {@link CuboidDirection} in which to expand
     * @param amount    the number of {@link Block}s by which to expand
     * @return new {@link Cuboid} expanded by the given {@link CuboidDirection} and amount
     */
    public Cuboid expand(CuboidDirection direction, int amount) throws IllegalArgumentException {
        switch (direction) {
            case NORTH:
                return new Cuboid(worldName, this.x1 - amount, this.y1, this.z1, this.x2, this.y2, this.z2);
            case SOUTH:
                return new Cuboid(worldName, this.x1, this.y1, this.z1, this.x2 + amount, this.y2, this.z2);
            case EAST:
                return new Cuboid(worldName, this.x1, this.y1, this.z1 - amount, this.x2, this.y2, this.z2);
            case WEST:
                return new Cuboid(worldName, this.x1, this.y1, this.z1, this.x2, this.y2, this.z2 + amount);
            case DOWN:
                return new Cuboid(worldName, this.x1, this.y1 - amount, this.z1, this.x2, this.y2, this.z2);
            case UP:
                return new Cuboid(worldName, this.x1, this.y1, this.z1, this.x2, this.y2 + amount, this.z2);
            default:
                throw new IllegalArgumentException("Invalid direction " + direction);
        }
    }

    /**
     * Outset (grow) the {@link Cuboid} in the given {@link CuboidDirection} by the given amount.
     *
     * @param direction the {@link CuboidDirection} in which to outset (must be HORIZONTAL, VERTICAL, or BOTH)
     * @param amount    the number of {@link Block}s by which to outset
     * @return a new {@link Cuboid} outset by the given {@link CuboidDirection} and amount
     */
    public Cuboid outset(CuboidDirection direction, int amount) throws IllegalArgumentException {
        switch (direction) {
            case HORIZONTAL:
                return this.expand(CuboidDirection.NORTH, amount).expand(CuboidDirection.SOUTH, amount).expand(CuboidDirection.EAST, amount).expand(CuboidDirection.WEST, amount);
            case VERTICAL:
                return this.expand(CuboidDirection.DOWN, amount).expand(CuboidDirection.UP, amount);
            case BOTH:
                return this.outset(CuboidDirection.HORIZONTAL, amount).outset(CuboidDirection.VERTICAL, amount);
            default:
                throw new IllegalArgumentException("Invalid direction " + direction);
        }
    }

    /**
     * Checks if this {@link Cuboid} contains another {@link Cuboid}.
     *
     * @param cuboid the {@link Cuboid} to check for
     * @return true if this {@link Cuboid} contains another {@link Cuboid}
     */
    public boolean contains(Cuboid cuboid) {
        return this.contains(cuboid.getMinimumPoint()) || this.contains(cuboid.getMaximumPoint());
    }

    /**
     * Checks if this {@link Cuboid} contains a {@link Player}.
     *
     * @param player the {@link Player} to check for
     * @return true if this {@link Cuboid} contains {@link Player}
     */
    public boolean contains(Player player) {
        return this.contains(player.getLocation());
    }

    /**
     * Checks if this {@link Cuboid} contains a (x, z) position in a {@link World}.
     *
     * @param world the {@link World} to check for
     * @param x     the x to check for
     * @param z     the z to check for
     * @return true if this {@link Cuboid} contains
     */
    public boolean contains(World world, int x, int z) {
        return (world == null || this.getWorld().equals(world)) && x >= this.x1 && x <= this.x2 && z >= this.z1 && z <= this.z2;
    }

    /**
     * Checks if the point at (x, y, z) is contained within this {@link Cuboid}.
     *
     * @param x the X co-ordinate
     * @param y the Y co-ordinate
     * @param z the Z co-ordinate
     * @return true if the given point is within this {@link Cuboid}, false otherwise
     */
    public boolean contains(int x, int y, int z) {
        return x >= this.x1 && x <= this.x2 && y >= this.y1 && y <= this.y2 && z >= this.z1 && z <= this.z2;
    }

    /**
     * Check if the given {@link Block} is contained within this {@link Cuboid}.
     *
     * @param block the {@link Block} to check for
     * @return true if the block is within this {@link Cuboid}, false otherwise
     */
    public boolean contains(Block block) {
        return this.contains(block.getLocation());
    }

    /**
     * Checks if the given {@link Location} is contained within this {@link Cuboid}.
     *
     * @param location the {@link Location} to check for
     * @return true if {@link Location} is within this {@link Cuboid}, false otherwise
     */
    public boolean contains(Location location) {
        if (location == null || this.worldName == null) {
            return false;
        }

        World world = location.getWorld(); // null check the World as disabling the End can usually screw this up
        return world != null && this.worldName.equals(location.getWorld().getName()) && contains(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    /**
     * Gets the volume of this {@link Cuboid}.
     *
     * @return the {@link Cuboid} volume, in blocks
     */
    public int getVolume() {
        return this.getSizeX() * this.getSizeY() * this.getSizeZ();
    }

    /**
     * Gets the area of this {@link Cuboid}.
     *
     * @return the {@link Cuboid} area
     */
    public int getArea() {
        Location min = this.getMinimumPoint();
        Location max = this.getMaximumPoint();
        return ((max.getBlockX() - min.getBlockX() + 1) *
                (max.getBlockZ() - min.getBlockZ() + 1));
    }

    /**
     * Get the average light level of all empty (AIR) {@link Block}s in the {@link Cuboid}.
     * <p>Returns 0 if there are no empty {@link Block}s.</p>
     *
     * @return the average light level of this {@link Cuboid}
     */
    public byte getAverageLightLevel() {
        long total = 0;
        int count = 0;
        for (Block block : this) {
            if (block.isEmpty()) {
                total += block.getLightLevel();
                count++;
            }
        }

        return count > 0 ? (byte) (total / count) : 0;
    }

    /**
     * Gets the minimum point of this {@link Cuboid}.
     *
     * @return the minimum {@link Cuboid} point
     */
    public Location getMinimumPoint() {
        return new Location(getWorld(),
                (double) Math.min(this.x1, this.x2),
                (double) Math.min(this.y1, this.y2),
                (double) Math.min(this.z1, this.z2));
    }

    /**
     * Gets the maximum point of this {@link Cuboid}.
     *
     * @return the maximum {@link Cuboid} point
     */
    public Location getMaximumPoint() {
        return new Location(getWorld(),
                (double) Math.max(this.x1, this.x2),
                (double) Math.max(this.y1, this.y2),
                (double) Math.max(this.z1, this.z2));
    }

    /**
     * Gets the width of this {@link Cuboid}.
     *
     * @return the {@link Cuboid} width
     */
    public int getWidth() {
        return this.getMaximumPoint().getBlockX() - this.getMinimumPoint().getBlockX();
    }

    /**
     * Gets the height of this {@link Cuboid}.
     *
     * @return the {@link Cuboid} height
     */
    public int getHeight() {
        return this.getMaximumPoint().getBlockY() - this.getMinimumPoint().getBlockY();
    }

    /**
     * Gets the length of this {@link Cuboid}.
     *
     * @return the {@link Cuboid} length
     */
    public int getLength() {
        return this.getMaximumPoint().getBlockZ() - this.getMinimumPoint().getBlockZ();
    }

    /**
     * Contracts the {@link Cuboid}, returning a {@link Cuboid} with
     * any AIR around the edges removed, just large enough
     * to include all non-air {@link Block}s.
     *
     * @return new {@link Cuboid} with no external air {@link Block}s
     */
    public Cuboid contract() {
        return this.
                contract(CuboidDirection.DOWN).
                contract(CuboidDirection.SOUTH).
                contract(CuboidDirection.EAST).
                contract(CuboidDirection.UP).
                contract(CuboidDirection.NORTH).
                contract(CuboidDirection.WEST);
    }

    /**
     * Contracts the {@link Cuboid} in the given direction,
     * returning a new {@link Cuboid} which has no exterior empty space.
     * E.g. a direction of DOWN will push the top face downwards as much as possible.
     *
     * @param direction the {@link CuboidDirection} in which to contract
     * @return new {@link Cuboid} contracted in the given {@link CuboidDirection}
     */
    public Cuboid contract(CuboidDirection direction) {
        Cuboid face = this.getFace(direction.opposite());

        switch (direction) {
            case DOWN:
                while (face.containsOnly(Material.AIR) && face.y1 > this.y1) {
                    face = face.shift(CuboidDirection.DOWN, 1);
                }

                return new Cuboid(this.worldName, this.x1, this.y1, this.z1, this.x2, face.y2, this.z2);
            case UP:
                while (face.containsOnly(Material.AIR) && face.y2 < this.y2) {
                    face = face.shift(CuboidDirection.UP, 1);
                }

                return new Cuboid(this.worldName, this.x1, face.y1, this.z1, this.x2, this.y2, this.z2);
            case NORTH:
                while (face.containsOnly(Material.AIR) && face.x1 > this.x1) {
                    face = face.shift(CuboidDirection.NORTH, 1);
                }

                return new Cuboid(this.worldName, this.x1, this.y1, this.z1, face.x2, this.y2, this.z2);
            case SOUTH:
                while (face.containsOnly(Material.AIR) && face.x2 < this.x2) {
                    face = face.shift(CuboidDirection.SOUTH, 1);
                }

                return new Cuboid(this.worldName, face.x1, this.y1, this.z1, this.x2, this.y2, this.z2);
            case EAST:
                while (face.containsOnly(Material.AIR) && face.z1 > this.z1) {
                    face = face.shift(CuboidDirection.EAST, 1);
                }

                return new Cuboid(this.worldName, this.x1, this.y1, this.z1, this.x2, this.y2, face.z2);
            case WEST:
                while (face.containsOnly(Material.AIR) && face.z2 < this.z2) {
                    face = face.shift(CuboidDirection.WEST, 1);
                }

                return new Cuboid(this.worldName, this.x1, this.y1, face.z1, this.x2, this.y2, this.z2);
            default:
                throw new IllegalArgumentException("Invalid direction " + direction);
        }
    }

    /**
     * Gets the {@link Cuboid} representing the face of this {@link Cuboid}.
     * The resulting {@link Cuboid} will be one {@link Block} thick in the axis
     * perpendicular to the requested face.
     *
     * @param direction the {@link CuboidDirection} which face of the {@link Cuboid} to get
     * @return the {@link Cuboid} representing this {@link Cuboid}s' requested face
     */
    public Cuboid getFace(CuboidDirection direction) {
        switch (direction) {
            case DOWN:
                return new Cuboid(this.worldName, this.x1, this.y1, this.z1, this.x2, this.y1, this.z2);
            case UP:
                return new Cuboid(this.worldName, this.x1, this.y2, this.z1, this.x2, this.y2, this.z2);
            case NORTH:
                return new Cuboid(this.worldName, this.x1, this.y1, this.z1, this.x1, this.y2, this.z2);
            case SOUTH:
                return new Cuboid(this.worldName, this.x2, this.y1, this.z1, this.x2, this.y2, this.z2);
            case EAST:
                return new Cuboid(this.worldName, this.x1, this.y1, this.z1, this.x2, this.y2, this.z1);
            case WEST:
                return new Cuboid(this.worldName, this.x1, this.y1, this.z2, this.x2, this.y2, this.z2);
            default:
                throw new IllegalArgumentException("Invalid direction " + direction);
        }
    }

    /**
     * Checks if the {@link Cuboid} contains only {@link Block}s of the
     * given {@link Material} type.
     *
     * @param material the {@link Material} to check for
     * @return true if this {@link Cuboid} contains
     * only {@link Block}s of the given {@link Material}
     */
    public boolean containsOnly(Material material) {
        for (Block block : this) {
            if (block.getType() != material) {
                return false;
            }
        }

        return true;
    }

    /**
     * Gets the {@link Cuboid} big enough to hold both this {@link Cuboid} and another given one.
     *
     * @param other the other {@link Cuboid} to include
     * @return new {@link Cuboid} large enough to hold this {@link Cuboid} and the given {@link Cuboid}
     */
    public Cuboid getBoundingCuboid(Cuboid other) {
        if (other == null) {
            return this;
        }

        int xMin = Math.min(this.x1, other.x1);
        int yMin = Math.min(this.y1, other.y1);
        int zMin = Math.min(this.z1, other.z1);
        int xMax = Math.max(this.x2, other.x2);
        int yMax = Math.max(this.y2, other.y2);
        int zMax = Math.max(this.z2, other.z2);
        return new Cuboid(this.worldName, xMin, yMin, zMin, xMax, yMax, zMax);
    }

    /**
     * Gets a {@link Block} relative to the lower North East point of this {@link Cuboid}.
     *
     * @param x the X co-ordinate
     * @param y the Y co-ordinate
     * @param z the Z co-ordinate
     * @return the {@link Block} at the given position
     */
    public Block getRelativeBlock(int x, int y, int z) {
        return getWorld().getBlockAt(this.x1 + x, this.y1 + y, this.z1 + z);
    }

    /**
     * Gets a {@link Block} relative to the lower North East point of the {@link Cuboid} in
     * the given {@link World}. This version of getRelativeBlock() should be used if being called many times,
     * to avoid excessive calls to {@code getWorld()}.
     *
     * @param world the {@link World}
     * @param x     the X co-ordinate
     * @param y     the Y co-ordinate
     * @param z     the Z co-ordinate
     * @return the {@link Block} at the given position
     */
    public Block getRelativeBlock(World world, int x, int y, int z) {
        return world.getBlockAt(this.x1 + x, this.y1 + y, this.z1 + z);
    }

    private static final int CHUNK_SIZE = 16;

    /**
     * Gets the {@link Chunk}s which are fully or partially contained in this {@link Cuboid}.
     *
     * @return list of {@link Chunk}s in this {@link Cuboid}
     */
    public List<Chunk> getChunks() {
        World world = this.getWorld();
        int x1 = this.x1 & ~0xf;
        int x2 = this.x2 & ~0xf;
        int z1 = this.z1 & ~0xf;
        int z2 = this.z2 & ~0xf;
        List<Chunk> result = new ArrayList<>(((x2 - x1) + CHUNK_SIZE) + ((z2 - z1) * CHUNK_SIZE));
        for (int x = x1; x <= x2; x += CHUNK_SIZE) {
            for (int z = z1; z <= z2; z += CHUNK_SIZE) {
                result.add(world.getChunkAt(x >> 4, z >> 4));
            }
        }

        return result;
    }

    /**
     * Gets the {@link CuboidBlockIterator} for this {@link Cuboid}.
     *
     * @return the {@link CuboidBlockIterator}
     */
    @Override
    public Iterator<Block> iterator() {
        return new CuboidBlockIterator(this.getWorld(), this.x1, this.y1, this.z1, this.x2, this.y2, this.z2);
    }

    public Iterator<Location> locationIterator() {
        return new CuboidLocationIterator(this.getWorld(), this.x1, this.y1, this.z1, this.x2, this.y2, this.z2);
    }

    @Override
    public Cuboid clone() {
        try {
            return (Cuboid) super.clone();
        } catch (CloneNotSupportedException ex) {
            throw new RuntimeException("This could never happen", ex);
        }
    }

    @Override
    public String toString() {
        return "Cuboid: " + this.worldName + ',' + this.x1 + ',' + this.y1 + ',' + this.z1 + "=>" + this.x2 + ',' + this.y2 + ',' + this.z2;
    }
}