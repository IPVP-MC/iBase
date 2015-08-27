package com.doctordark.util;

import net.minecraft.server.v1_7_R4.MathHelper;
import net.minecraft.server.v1_7_R4.Packet;
import net.minecraft.server.v1_7_R4.PacketPlayOutWorldParticles;
import org.apache.commons.lang3.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

/**
 * Library used for displaying {@link ParticleEffect}s.
 * <p>http://wiki.vg/Protocol</p>
 */
public enum ParticleEffect {

    HUGE_EXPLODE("hugeexplosion", 0),
    LARGE_EXPLODE("largeexplode", 1),
    FIREWORK_SPARK("fireworksSpark", 2),
    AIR_BUBBLE("bubble", 3),
    SUSPEND("suspend", 4),
    DEPTH_SUSPEND("depthSuspend", 5),
    TOWN_AURA("townaura", 6),
    CRITICAL_HIT("crit", 7),
    MAGIC_CRITICAL_HIT("magicCrit", 8),
    MOB_SPELL("mobSpell", 9),
    MOB_SPELL_AMBIENT("mobSpellAmbient", 10),
    SPELL("spell", 11),
    INSTANT_SPELL("instantSpell", 12),
    BLUE_SPARKLE("witchMagic", 13),
    NOTE_BLOCK("note", 14),
    ENDER("portal", 15),
    ENCHANTMENT_TABLE("enchantmenttable", 16),
    EXPLODE("explode", 17),
    FIRE("flame", 18),
    LAVA_SPARK("lava", 19),
    FOOTSTEP("footstep", 20),
    SPLASH("splash", 21),
    LARGE_SMOKE("largesmoke", 22),
    CLOUD("cloud", 23),
    REDSTONE_DUST("reddust", 24),
    SNOWBALL_HIT("snowballpoof", 25),
    DRIP_WATER("dripWater", 26),
    DRIP_LAVA("dripLava", 27),
    SNOW_DIG("snowshovel", 28),
    SLIME("slime", 29),
    HEART("heart", 30),
    ANGRY_VILLAGER("angryVillager", 31),
    GREEN_SPARKLE("happyVillager", 32),
    ICONCRACK("iconcrack", 33),
    TILECRACK("tilecrack", 34);

    private final String name;
    private final int id;

    ParticleEffect(String name, int id) {
        this.name = name;
        this.id = id;
    }

    /**
     * Gets the name of this {@link ParticleEffect}.
     *
     * @return the name
     */
    String getName() {
        return name;
    }

    /**
     * Gets the ID of this {@link ParticleEffect}
     *
     * @return the ID
     */
    public int getId() {
        return id;
    }

    /**
     * Displays this {@link ParticleEffect} to a {@link Player}
     *
     * @param player the {@link Player} to show
     * @param x      the x co-ordinate to show at
     * @param y      the y co-ordinate to show at
     * @param z      the z co-ordinate to show at
     * @param speed  the speed (or color depending on the effect)
     * @param amount the amount to show
     */
    public void display(Player player, float x, float y, float z, float speed, int amount) {
        display(player, x, y, z, 0.0F, 0.0F, 0.0F, speed, amount);
    }

    /**
     * Displays this {@link ParticleEffect} to a {@link Player}
     *
     * @param player  the {@link Player} to show
     * @param x       the x co-ordinate to show at
     * @param y       the y co-ordinate to show at
     * @param z       the z co-ordinate to show at
     * @param offsetX the x range of the particle effect
     * @param offsetY the y range of the particle effect
     * @param offsetZ the z range of the particle effect
     * @param speed   the speed (or color depending on the effect)
     * @param amount  the amount of particles to show
     */
    public void display(Player player, float x, float y, float z, float offsetX, float offsetY, float offsetZ, float speed, int amount) {
        Packet packet = createPacket(x, y, z, offsetX, offsetY, offsetZ, speed, amount);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

    /**
     * Displays this {@link ParticleEffect} to a {@link Player}
     *
     * @param player   the {@link Player} to show
     * @param location the {@link Location} to show at
     * @param speed    the speed (or color depending on the effect)
     * @param amount   the amount of particles to show
     */
    public void display(Player player, Location location, float speed, int amount) {
        display(player, location, 0.0F, 0.0F, 0.0F, speed, amount);
    }

    /**
     * Displays this {@link ParticleEffect} to a {@link Player}
     *
     * @param player   the {@link Player} to show
     * @param location the {@link Location} to show at
     * @param offsetX  the x range of the particle effect
     * @param offsetY  the y range of the particle effect
     * @param offsetZ  the z range of the particle effect
     * @param speed    the speed (or color depending on the effect)
     * @param amount   the amount of particles to show
     */
    public void display(Player player, Location location, float offsetX, float offsetY, float offsetZ, float speed, int amount) {
        Packet packet = createPacket(location, offsetX, offsetY, offsetZ, speed, amount);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

    /**
     * Send this {@link ParticleEffect} to all online {@link Player}s
     *
     * @param x       the x co-ordinate to show at
     * @param y       the y co-ordinate to show at
     * @param z       the z co-ordinate to show at
     * @param offsetX the x range of the particle effect
     * @param offsetY the y range of the particle effect
     * @param offsetZ the z range of the particle effect
     * @param speed   the speed (or color depending on the effect)
     * @param amount  the amount of particles to show
     */
    public void broadcast(float x, float y, float z, float offsetX, float offsetY, float offsetZ, float speed, int amount) {
        Packet packet = createPacket(x, y, z, offsetX, offsetY, offsetZ, speed, amount);
        for (Player player : Bukkit.getOnlinePlayers()) {
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
        }
    }

    /**
     * Send this {@link ParticleEffect} to all online {@link Player}s
     *
     * @param location the {@link Location} to show at
     * @param offsetX  the x range of the particle effect
     * @param offsetY  the y range of the particle effect
     * @param offsetZ  the z range of the particle effect
     * @param speed    the speed (or color depending on the effect)
     * @param amount   the amount of particles to show
     */
    public void broadcast(Location location, float offsetX, float offsetY, float offsetZ, float speed, int amount) {
        this.broadcast(location, offsetX, offsetY, offsetZ, speed, amount, null);
    }

    /**
     * Send this {@link ParticleEffect} to all online {@link Player}s
     *
     * @param location the {@link Location} to show at
     * @param offsetX  the x range of the particle effect
     * @param offsetY  the y range of the particle effect
     * @param offsetZ  the z range of the particle effect
     * @param speed    the speed (or color depending on the effect)
     * @param amount   the amount of particles to show
     * @param source   the source of this {@link ParticleEffect} or null
     */
    public void broadcast(Location location, float offsetX, float offsetY, float offsetZ, float speed, int amount, @Nullable Entity source) {
        Packet packet = createPacket(location, offsetX, offsetY, offsetZ, speed, amount);
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (source == null || player.canSee(source)) {
                ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
            }
        }
    }

    /**
     * Displays this {@link ParticleEffect} as a sphere to a {@link Player}
     *
     * @param player   the {@link Player} to show
     * @param location the {@link Location} of the center
     * @param radius   the radius of the sphere
     */
    public void sphere(@Nullable Player player, Location location, float radius) {
        sphere(player, location, radius, 20f, 2);
    }

    /**
     * Displays this {@link ParticleEffect} as a sphere to a {@link Player} or to all online
     *
     * @param player    the {@link Player} to show or null to broadcast to every player
     * @param location  the {@link Location} of the center
     * @param radius    the radius of the sphere
     * @param density   the density of particle locations
     * @param intensity the number of particles at each location
     */
    public void sphere(@Nullable Player player, Location location, float radius, float density, int intensity) {
        Validate.notNull(location, "Location cannot be null");
        Validate.isTrue(radius >= 0, "Radius must be positive");
        Validate.isTrue(density >= 0, "Density must be positive");
        Validate.isTrue(intensity >= 0, "Intensity must be positive");

        float deltaPitch = 180 / density;
        float deltaYaw = 360 / density;
        World world = location.getWorld();
        for (int i = 0; i < density; i++) {
            for (int j = 0; j < density; j++) {
                float pitch = -90 + (j * deltaPitch);
                float yaw = -180 + (i * deltaYaw);
                float x = radius * MathHelper.sin(-yaw * 0.017453292F - (float) Math.PI) * -MathHelper.cos(-pitch * 0.017453292F) + (float) location.getX();
                float y = radius * MathHelper.sin(-pitch * 0.017453292F) + (float) location.getY();
                float z = radius * MathHelper.cos(-yaw * 0.017453292F - (float) Math.PI) * -MathHelper.cos(-pitch * 0.017453292F) + (float) location.getZ();

                Location target = new Location(world, x, y, z);
                if (player == null) {
                    broadcast(target, 0f, 0f, 0f, 0f, intensity);
                } else {
                    display(player, target, 0f, 0f, 0f, 0f, intensity);
                }
            }
        }
    }

    private PacketPlayOutWorldParticles createPacket(Location location, float offsetX, float offsetY, float offsetZ, float speed, int amount) {
        return this.createPacket((float) location.getX(), (float) location.getY(), (float) location.getZ(), offsetX, offsetY, offsetZ, speed, amount);
    }

    private PacketPlayOutWorldParticles createPacket(float x, float y, float z, float offsetX, float offsetY, float offsetZ, float speed, int amount) {
        Validate.isTrue(speed >= 0, "Speed must be positive");
        Validate.isTrue(amount > 0, "Cannot use less than one particle.");
        return new PacketPlayOutWorldParticles(name, x, y, z, offsetX, offsetY, offsetZ, speed, amount);
    }
}