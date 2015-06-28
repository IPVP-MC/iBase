package com.doctordark.util.bossbar;

import net.minecraft.server.v1_7_R4.EntityEnderDragon;
import net.minecraft.server.v1_7_R4.Packet;
import net.minecraft.server.v1_7_R4.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_7_R4.PacketPlayOutSpawnEntityLiving;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;

/**
 * Represents a {@link BossBar} to show at the top of a Minecraft client.
 */
public class BossBar {

    public static final int MAX_TITLE_LENGTH = 64; // the maximum characters the title can be
    public static final int MIN_HEALTH = 0;        // the minimum health the bar can be
    public static final int MAX_HEALTH = 200;      // the maximum health the bar can be

    public PacketPlayOutEntityDestroy destroyPacket;
    public PacketPlayOutSpawnEntityLiving spawnPacket;

    private EntityEnderDragon dragon;
    private Location location;
    private String title;
    private float health;

    /**
     * Constructs a new {@link BossBar} with a given title and full health.
     *
     * @param title the title to set
     */
    public BossBar(Location location, String title) {
        this(location, title, 100);
    }

    /**
     * Constructs a new {@link BossBar} with a given title and
     * percentage of health.
     *
     * @param title   the title to set
     * @param percent the health percent to set
     */
    public BossBar(Location location, String title, int percent) {
        this.dragon = new EntityEnderDragon(((CraftWorld) location.getWorld()).getHandle());

        this.setLocation(location);
        this.setTitle(title);
        this.setHealth((percent / 100.0F) * MAX_HEALTH);
        this.reloadPackets();
    }

    /**
     * Gets the {@link EntityEnderDragon} representing this {@link BossBar}.
     *
     * @return the {@link EntityEnderDragon}
     */
    public EntityEnderDragon getDragon() {
        return dragon;
    }

    /**
     * Gets the {@link Location} of the {@link EntityEnderDragon} representing this {@link BossBar}.
     *
     * @return the {@link Location}
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Sets the {@link Location} of the {@link EntityEnderDragon} representing this {@link BossBar}.
     *
     * @param location the {@link Location} to set at
     */
    public void setLocation(Location location) {
        Validate.notNull(location, "Location cannot be null");
        Validate.notNull(location.getWorld(), "Location world cannot be null");

        this.location = location;
        this.dragon.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }

    /**
     * Gets the title of this {@link BossBar}.
     *
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of this {@link BossBar}.
     *
     * @param title the title to set
     */
    public void setTitle(String title) {
        Validate.notNull(title, "Text cannot be null.");
        Validate.isTrue(title.length() < MAX_TITLE_LENGTH, "Text cannot be longer than " + MAX_TITLE_LENGTH + " characters.");

        this.title = title;
        this.dragon.setCustomName(title);
        this.dragon.setCustomNameVisible(true);
    }

    /**
     * Gets the health of this {@link BossBar}.
     *
     * @return the health.
     */
    public float getHealth() {
        return health;
    }

    /**
     * Sets the health of this {@link BossBar}.
     *
     * @param health the health to set
     */
    public void setHealth(float health) {
        Validate.isTrue(health >= MIN_HEALTH, "Health of " + health + " is less than minimum health: " + MIN_HEALTH);
        Validate.isTrue(health <= MAX_HEALTH, "Health of " + health + " is more than maximum health: " + MAX_HEALTH);

        this.health = health;
        this.dragon.setHealth(health);
    }

    /**
     * Reloads the {@link Packet}s for this {@link BossBar}.
     */
    private void reloadPackets() {
        this.spawnPacket = new PacketPlayOutSpawnEntityLiving(dragon);
        this.destroyPacket = new PacketPlayOutEntityDestroy(dragon.getId());
    }
}