package com.doctordark.util.bossbar;

import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_7_R4.EntityEnderDragon;
import net.minecraft.server.v1_7_R4.EntityInsentient;
import net.minecraft.server.v1_7_R4.EntityLiving;
import net.minecraft.server.v1_7_R4.EntityPlayer;
import net.minecraft.server.v1_7_R4.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_7_R4.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_7_R4.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_7_R4.World;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.LinkedHashSet;
import java.util.UUID;

/**
 * Represents a boss bar HUD.
 */
public abstract class BossBar {

    private static final int MAX_TITLE_LENGTH = 64;   // the maximum characters the title can be
    private static final int MIN_HEALTH = 0;          // the minimum health the bar can be

    /**
     * The maximum health for this boss type.
     *
     * @return the maximum health value the bar can be
     */
    abstract int getMaxHealth();

    /**
     * Creates the boss entity to represent this bar.
     *
     * @param world the world to construct in
     * @return the constructed entity
     */
    abstract EntityLiving constructBossEntity(World world);

    @Getter
    protected final LinkedHashSet<UUID> viewers = new LinkedHashSet<>();

    @Getter
    protected final PacketPlayOutSpawnEntityLiving spawnPacket;

    @Getter
    protected final PacketPlayOutEntityDestroy destroyPacket;

    @Getter
    protected final PacketPlayOutEntityMetadata metadataPacket;

    @Getter
    protected EntityLiving entity;

    @Getter
    protected Location location;

    @Getter
    protected String title;

    @Getter
    protected float health;

    @Getter
    @Setter
    protected boolean reshowOnLogin;

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
        this.setLocation(location);
        this.setTitle(title);
        this.setHealth((percent / 100.0F) * this.getMaxHealth());

        this.entity = this.constructBossEntity(((CraftWorld) this.location.getWorld()).getHandle());
        this.entity.setInvisible(true);
        this.spawnPacket = new PacketPlayOutSpawnEntityLiving(this.entity);
        this.destroyPacket = new PacketPlayOutEntityDestroy(this.entity.getId());
        this.metadataPacket = new PacketPlayOutEntityMetadata(this.entity.getId(), this.entity.getDataWatcher(), true);
    }

    /**
     * Sets the {@link Location} of the {@link EntityEnderDragon} representing this {@link BossBar}.
     *
     * @param location the {@link Location} to set at
     */
    public void setLocation(Location location) throws IllegalArgumentException {
        Preconditions.checkNotNull(location, "Location cannot be null");
        Preconditions.checkNotNull(location.getWorld(), "Location world cannot be null");

        this.location = location;
        this.entity.setLocation(this.location.getX(), this.location.getY(), this.location.getZ(), this.location.getYaw(), this.location.getPitch());
    }

    /**
     * Sets the title of this {@link BossBar}.
     *
     * @param title the title to set
     */
    public void setTitle(String title) throws IllegalArgumentException {
        Preconditions.checkNotNull(title, "Text cannot be null");
        Preconditions.checkArgument(title.length() < MAX_TITLE_LENGTH, "Text cannot be longer than ", MAX_TITLE_LENGTH, " characters");

        this.title = title;
        if (this.entity instanceof EntityInsentient) {
            ((EntityInsentient) this.entity).setCustomName(this.title);
        }
    }

    /**
     * Sets the health of this {@link BossBar}.
     *
     * @param health the health to set
     */
    public void setHealth(float health) {
        Preconditions.checkArgument(health >= MIN_HEALTH, "Health of ", health, " is less than minimum health ", MIN_HEALTH);

        int maxHealth = this.getMaxHealth();
        Preconditions.checkArgument(health <= maxHealth, "Health of ", health, " is more than maximum health ", maxHealth);

        this.entity.setHealth(this.health = health);
    }

    /**
     * Checks if a {@link Player} is a viewer of this {@link BossBar}.
     *
     * @param player the {@link Player} to check
     * @return true if is a viewer
     */
    public boolean hasViewer(Player player) {
        return this.viewers.contains(player.getUniqueId());
    }

    protected void show(Player player) {
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        entityPlayer.playerConnection.sendPacket(spawnPacket);
        entityPlayer.playerConnection.sendPacket(metadataPacket);
    }

    protected void hide(Player player) {
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        entityPlayer.playerConnection.sendPacket(destroyPacket);
    }

    /**
     * Adds a viewer to this {@link BossBar}.
     *
     * @param player the {@link Player} to add
     * @return true if was added
     */
    protected boolean addViewer(Player player) {
        Preconditions.checkNotNull(player, "Player cannot be null");

        boolean result = this.viewers.add(player.getUniqueId());
        if (result) {
            this.show(player);
        }

        return result;
    }

    /**
     * Removes a viewer from this {@link BossBar}.
     *
     * @param player the {@link Player} to remove
     * @return true if was removed
     */
    protected boolean removeViewer(Player player) {
        Preconditions.checkNotNull(player, "Player cannot be null");

        boolean result = this.viewers.remove(player.getUniqueId());
        if (result) {
            this.hide(player);
        }

        return result;
    }
}
