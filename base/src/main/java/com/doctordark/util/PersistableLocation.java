package com.doctordark.util;

import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class PersistableLocation implements ConfigurationSerializable, Cloneable {

    // Lazy loaded
    private Location location;
    private World world;
    private String worldName;

    @Setter
    private UUID worldUID;

    @Setter
    private double x;

    @Setter
    private double y;

    @Setter
    private double z;

    @Setter
    private float yaw;

    @Setter
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
     * Converts this to a {@link Location}.
     *
     * @return the location instance
     */
    public Location getLocation() {
        if (this.location == null) {
            this.location = new Location(getWorld(), this.x, this.y, this.z, this.yaw, this.pitch);
        }

        return this.location;
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
