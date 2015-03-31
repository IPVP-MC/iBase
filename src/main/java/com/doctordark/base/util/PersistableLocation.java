package com.doctordark.base.util;

import com.google.common.collect.Maps;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.UUID;

public class PersistableLocation implements ConfigurationSerializable, Cloneable {

    private String worldName;
    private UUID worldUID;
    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;

    public PersistableLocation(@Nonnull Location location) {
        World world = location.getWorld();
        this.worldName = world.getName();
        this.worldUID = world.getUID();
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
        this.yaw = location.getYaw();
        this.pitch = location.getPitch();
    }

    public PersistableLocation(Map<String, Object> map) {
        this.worldName = ((String) map.get("worldName"));
        this.worldUID = UUID.fromString((String) map.get("worldUID"));
        this.x = Double.parseDouble((String) map.get("x"));
        this.y = Double.parseDouble((String) map.get("y"));
        this.z = Double.parseDouble((String) map.get("z"));
        this.yaw = Float.parseFloat((String) map.get("yaw"));
        this.pitch = Float.parseFloat((String) map.get("pitch"));
    }

    public String getWorldName() {
        return this.worldName;
    }

    public UUID getWorldUID() {
        return this.worldUID;
    }

    public World getWorld() {
        Validate.notNull(this.worldUID, "World UUID cannot be null");
        Validate.notNull(this.worldName, "World name cannot be null");

        Server server = Bukkit.getServer();
        World world = server.getWorld(this.worldUID);
        return world == null ? server.getWorld(this.worldName) : world;
    }

    public void setWorld(World world) {
        setWorldName(world.getName());
        setWorldUID(world.getUID());
    }

    private void setWorldName(String worldName) {
        this.worldName = worldName;
    }

    private void setWorldUID(UUID worldUID) {
        this.worldUID = worldUID;
    }

    public double getX() {
        return this.x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return this.y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return this.z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public float getYaw() {
        return this.yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public float getPitch() {
        return this.pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public Location getLocation() {
        return new Location(getWorld(), getX(), getY(), getZ(), getYaw(), getPitch());
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
        return "PersistableLocation [worldName=" + this.worldName + ", worldUID=" + this.worldUID + ", x=" + this.x + ", y=" + this.y + ", z=" + this.z + ", yaw=" + this.yaw + ", pitch=" + this.pitch + "]";
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
        if (worldName != null ? !worldName.equals(that.worldName) : that.worldName != null) return false;
        return !(worldUID != null ? !worldUID.equals(that.worldUID) : that.worldUID != null);

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = worldName != null ? worldName.hashCode() : 0;
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

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = Maps.newHashMap();
        map.put("worldName", worldName);
        map.put("worldUID", worldUID.toString());
        map.put("x", Double.toString(x));
        map.put("y", Double.toString(y));
        map.put("z", Double.toString(z));
        map.put("yaw", Float.toString(yaw));
        map.put("pitch", Float.toString(pitch));
        return map;
    }
}
