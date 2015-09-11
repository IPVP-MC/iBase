package com.doctordark.util;

import com.google.common.base.Preconditions;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class PersistableLocation implements ConfigurationSerializable, Cloneable {

    // Lazy loaded
    private Location location;
    private World world;

    private String worldName;
    private UUID worldUID;
    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;

    public PersistableLocation(Location location) {
        Preconditions.checkNotNull(location, "Location cannot be null");
        Preconditions.checkNotNull(location.getWorld(), "Locations' world cannot be null");

        this.world = location.getWorld();
        this.worldName = world.getName();
        this.worldUID = world.getUID();
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
        this.yaw = location.getYaw();
        this.pitch = location.getPitch();
    }

    public PersistableLocation(World world, double x, double y, double z) {
        this.worldName = world.getName();
        this.x = x;
        this.y = y;
        this.z = z;
        this.pitch = this.yaw = 0.0F;
    }

    public PersistableLocation(String worldName, double x, double y, double z) {
        this.worldName = worldName;
        this.x = x;
        this.y = y;
        this.z = z;
        this.pitch = this.yaw = 0.0F;
    }

    public PersistableLocation(Map<String, Object> map) {
        this.worldName = (String) map.get("worldName");
        this.worldUID = UUID.fromString((String) map.get("worldUID"));

        Object o = map.get("x");
        if (o instanceof String) {
            this.x = Double.parseDouble((String) o);
        } else this.x = (Double) o;

        o = map.get("y");
        if (o instanceof String) {
            this.y = Double.parseDouble((String) o);
        } else this.y = (Double) o;

        o = map.get("z");
        if (o instanceof String) {
            this.z = Double.parseDouble((String) o);
        } else this.z = (Double) o;

        this.yaw = Float.parseFloat((String) map.get("yaw"));
        this.pitch = Float.parseFloat((String) map.get("pitch"));
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("worldName", worldName);
        map.put("worldUID", worldUID.toString());
        map.put("x", x);
        map.put("y", y);
        map.put("z", z);
        map.put("yaw", Float.toString(yaw));
        map.put("pitch", Float.toString(pitch));
        return map;
    }

    /**
     * Gets the name of the {@link World}.
     *
     * @return the world name
     */
    public String getWorldName() {
        return this.worldName;
    }

    /**
     * Gets the {@link UUID} of the {@link World}.
     *
     * @return the world unique ID
     */
    public UUID getWorldUID() {
        return this.worldUID;
    }

    /**
     * Gets the {@link World} this is in.
     *
     * @return the containing world
     */
    public World getWorld() {
        Preconditions.checkNotNull(this.worldUID, "World UUID cannot be null");
        Preconditions.checkNotNull(this.worldName, "World name cannot be null");

        if (world == null) world = Bukkit.getWorld(this.worldUID);
        return world;
    }

    /**
     * Sets the {@link World} this is in.
     *
     * @param world the world to set
     */
    public void setWorld(World world) {
        this.worldName = world.getName();
        this.worldUID = world.getUID();
        this.world = world;
    }

    /**
     * Sets the name of the {@link World} this is in.
     *
     * @param worldName the name to set
     */
    private void setWorldName(String worldName) {
        this.worldName = worldName;
    }

    /**
     * Sets the UID of the {@link World} this is in.
     *
     * @param worldUID the UID to set
     */
    private void setWorldUID(UUID worldUID) {
        this.worldUID = worldUID;
    }

    /**
     * Gets the x co-ordinate of this.
     *
     * @return the x co-ordinate
     */
    public double getX() {
        return this.x;
    }

    /**
     * Sets the x co-ordinate of this.
     *
     * @param x the co-ordinate to set
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * Gets the y co-ordinate of this.
     *
     * @return the y co-ordinate
     */
    public double getY() {
        return this.y;
    }

    /**
     * Sets the y co-ordinate of this.
     *
     * @param y the co-ordinate to set
     */
    public void setY(double y) {
        this.y = y;
    }

    /**
     * Gets the z co-ordinate of this.
     *
     * @return the z co-ordinate
     */
    public double getZ() {
        return this.z;
    }

    /**
     * Sets the z co-ordinate of this.
     *
     * @param z the co-ordinate to set
     */
    public void setZ(double z) {
        this.z = z;
    }

    /**
     * Gets the yaw of this.
     *
     * @return the yaw
     */
    public float getYaw() {
        return this.yaw;
    }

    /**
     * Sets the yaw of this.
     *
     * @param yaw the yaw to set
     */
    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    /**
     * Gets the pitch of this.
     *
     * @return the pitch
     */
    public float getPitch() {
        return this.pitch;
    }

    /**
     * Sets the pitch of this.
     *
     * @param pitch the pitch to set
     */
    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    /**
     * Converts this to a {@link Location}.
     *
     * @return the location instance
     */
    public Location getLocation() {
        if (location == null) {
            location = new Location(getWorld(), this.x, this.y, this.z, this.yaw, this.pitch);
        }

        return location;
    }

    @Override
    public PersistableLocation clone() throws CloneNotSupportedException {
        try {
            return (PersistableLocation) super.clone();
        } catch (CloneNotSupportedException ex) {
            ex.printStackTrace();
            throw new RuntimeException();
        }
    }

    @Override
    public String toString() {
        return "PersistableLocation [worldName=" + this.worldName + ", worldUID=" + this.worldUID +
                ", x=" + this.x + ", y=" + this.y + ", z=" + this.z + ", yaw=" + this.yaw + ", pitch=" + this.pitch + ']';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PersistableLocation)) return false;

        PersistableLocation that = (PersistableLocation) o;

        if (Double.compare(that.x, x) != 0) return false;
        if (Double.compare(that.y, y) != 0) return false;
        if (Double.compare(that.z, z) != 0) return false;
        if (Float.compare(that.yaw, yaw) != 0) return false;
        if (Float.compare(that.pitch, pitch) != 0) return false;
        if (world != null ? !world.equals(that.world) : that.world != null) return false;
        if (worldName != null ? !worldName.equals(that.worldName) : that.worldName != null) return false;
        return !(worldUID != null ? !worldUID.equals(that.worldUID) : that.worldUID != null);
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = world != null ? world.hashCode() : 0;
        result = 31 * result + (worldName != null ? worldName.hashCode() : 0);
        result = 31 * result + (worldUID != null ? worldUID.hashCode() : 0);
        temp = Double.doubleToLongBits(x);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(y);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(z);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (yaw != +0.0f ? Float.floatToIntBits(yaw) : 0);
        result = 31 * result + (pitch != +0.0f ? Float.floatToIntBits(pitch) : 0);
        return result;
    }
}
