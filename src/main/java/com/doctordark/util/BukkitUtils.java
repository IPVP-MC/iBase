package com.doctordark.util;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.server.v1_7_R4.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.Metadatable;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.ChatPaginator;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Utility class for simplifying tasks in the Bukkit API.
 */
public final class BukkitUtils {

    private BukkitUtils() {
    }

    private static final ImmutableMap<ChatColor, DyeColor> CHAT_DYE_COLOUR_MAP;
    private static final ImmutableSet<PotionEffectType> DEBUFF_TYPES;

    /**
     * The default amount of tab completion entries to limit to.
     */
    private static final int DEFAULT_COMPLETION_LIMIT = 80;

    /**
     * Internal use only
     */
    private static final String STRAIGHT_LINE_TEMPLATE;

    /**
     * The default straight line string wrapped across the Minecraft font width.
     */
    public static final String STRAIGHT_LINE_DEFAULT;

    static {
        STRAIGHT_LINE_TEMPLATE = ChatColor.STRIKETHROUGH.toString() + Strings.repeat("-", 256);
        STRAIGHT_LINE_DEFAULT = STRAIGHT_LINE_TEMPLATE.substring(0, ChatPaginator.GUARANTEED_NO_WRAP_CHAT_PAGE_WIDTH);

        CHAT_DYE_COLOUR_MAP = Maps.immutableEnumMap(ImmutableMap.<ChatColor, DyeColor>builder().
                put(ChatColor.AQUA, DyeColor.LIGHT_BLUE).
                put(ChatColor.BLACK, DyeColor.BLACK).
                put(ChatColor.BLUE, DyeColor.LIGHT_BLUE).
                put(ChatColor.DARK_AQUA, DyeColor.CYAN).
                put(ChatColor.DARK_BLUE, DyeColor.BLUE).
                put(ChatColor.DARK_GRAY, DyeColor.GRAY).
                put(ChatColor.DARK_GREEN, DyeColor.GREEN).
                put(ChatColor.DARK_PURPLE, DyeColor.PURPLE).
                put(ChatColor.DARK_RED, DyeColor.RED).
                put(ChatColor.GOLD, DyeColor.ORANGE).
                put(ChatColor.GRAY, DyeColor.SILVER).
                put(ChatColor.GREEN, DyeColor.LIME).
                put(ChatColor.LIGHT_PURPLE, DyeColor.MAGENTA).
                put(ChatColor.RED, DyeColor.RED).
                put(ChatColor.WHITE, DyeColor.WHITE).
                put(ChatColor.YELLOW, DyeColor.YELLOW).build());

        DEBUFF_TYPES = ImmutableSet.<PotionEffectType>builder().
                add(PotionEffectType.BLINDNESS).
                add(PotionEffectType.CONFUSION).
                add(PotionEffectType.HARM).
                add(PotionEffectType.HUNGER).
                add(PotionEffectType.POISON).
                add(PotionEffectType.SATURATION).
                add(PotionEffectType.SLOW).
                add(PotionEffectType.SLOW_DIGGING).
                add(PotionEffectType.WEAKNESS).
                add(PotionEffectType.WITHER).build();
    }

    public static int countColoursUsed(String id, boolean ignoreDuplicates) {
        ChatColor[] values = ChatColor.values();
        List<Character> charList = Lists.newArrayListWithCapacity(values.length);
        for (ChatColor colour : values) {
            charList.add(colour.getChar());
        }

        int count = 0;
        Set<ChatColor> found = new HashSet<>();
        for (int i = 1; i < id.length(); i++) {
            if (charList.contains(id.charAt(i)) && id.charAt(i - 1) == '&') {
                ChatColor colour = ChatColor.getByChar(id.charAt(i));
                if (found.add(colour) || ignoreDuplicates) {
                    count++;
                }
            }
        }

        return count;
    }

    public static List<String> getCompletions(String[] args, List<String> input) {
        return getCompletions(args, input, DEFAULT_COMPLETION_LIMIT);
    }

    public static List<String> getCompletions(String[] args, List<String> input, int limit) {
        Preconditions.checkNotNull(args);
        Preconditions.checkArgument(args.length != 0);

        String argument = args[(args.length - 1)];
        return input.stream().filter(string ->
                string.regionMatches(true, 0, argument, 0, argument.length())).
                limit(limit).collect(Collectors.toList());
    }

    /**
     * Gets the display name of a {@link CommandSender} or their
     * regular name if they don't have a display name.
     *
     * @param sender the {@link CommandSender} to get for
     * @return the resulted display name
     */
    public static String getDisplayName(CommandSender sender) {
        Preconditions.checkNotNull(sender);
        return sender instanceof Player ? ((Player) sender).getDisplayName() : sender.getName();
    }

    /**
     * Gets the time in milliseconds a {@link Player} has been idle for
     *
     * @param player the {@link Player} to get for
     * @return the time in milliseconds
     */
    public static long getIdleTime(Player player) {
        Preconditions.checkNotNull(player);
        long idleTime = ((CraftPlayer) player).getHandle().x();
        return idleTime > 0L ? MinecraftServer.ar() - idleTime : 0L;
    }

    /**
     * Converts an {@link ChatColor} to a {@link DyeColor}.
     *
     * @param colour the {@link ChatColor} to be converted
     * @return the converted colour.
     */
    public static DyeColor toDyeColor(ChatColor colour) {
        return CHAT_DYE_COLOUR_MAP.get(colour);
    }

    /**
     * Checks if an {@link Metadatable} has stored metadata.
     *
     * @param metadatable the {@link Metadatable}
     * @param input       the input to check for
     * @param plugin      the {@link Plugin} that owns the metadata
     * @return true if the {@link Metadatable} has the metadata
     */
    public static boolean hasMetaData(Metadatable metadatable, String input, Plugin plugin) {
        return getMetaData(metadatable, input, plugin) != null;
    }

    /**
     * Gets the {@link MetadataValue} from a {@link Metadatable}.
     *
     * @param metadatable the {@link Metadatable} to get for
     * @param input       the string to check for
     * @param plugin      the {@link Plugin} that owns the metadata
     * @return the {@link MetadataValue}
     */
    public static MetadataValue getMetaData(Metadatable metadatable, String input, Plugin plugin) {
        return metadatable.getMetadata(input, plugin);
    }

    /**
     * Gets the final {@link Player} attacker from the {@link EntityDamageEvent} including
     * {@link org.bukkit.projectiles.ProjectileSource} usage and everything else.
     *
     * @param ede        the {@link EntityDamageEvent} to get for
     * @param ignoreSelf if should ignore if the {@link Player} attacked self
     * @return the {@link Player} attacker of the event
     */
    public static Player getFinalAttacker(EntityDamageEvent ede, boolean ignoreSelf) {
        Player attacker = null;
        if (ede instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) ede;
            Entity damager = event.getDamager();
            if (event.getDamager() instanceof Player) {
                attacker = (Player) damager;
            } else if (event.getDamager() instanceof Projectile) {
                Projectile projectile = (Projectile) damager;
                ProjectileSource shooter = projectile.getShooter();
                if (shooter instanceof Player) {
                    attacker = (Player) shooter;
                }
            }

            if (attacker != null && ignoreSelf && event.getEntity().equals(attacker)) {
                attacker = null;
            }
        }

        return attacker;
    }

    /**
     * Gets a {@link Player} with the name or {@link java.util.UUID} of given String.
     *
     * @param string a string reference to search for
     * @return the found {@link Player} or null
     */
    public static Player playerWithNameOrUUID(String string) {
        if (string == null) return null;
        return JavaUtils.isUUID(string) ? Bukkit.getPlayer(UUID.fromString(string)) : Bukkit.getPlayer(string);
    }

    /**
     * Gets a {@link OfflinePlayer} with the name or {@link java.util.UUID} of given String.
     *
     * @param string a string reference to search for
     * @return the found {@link OfflinePlayer} or {@code null}
     * @deprecated use of {@link Bukkit#getOfflinePlayer(String)}
     */
    @Deprecated
    public static OfflinePlayer offlinePlayerWithNameOrUUID(String string) {
        if (string == null) return null;
        return JavaUtils.isUUID(string) ? Bukkit.getOfflinePlayer(UUID.fromString(string)) : Bukkit.getOfflinePlayer(string); //TODO: breaking, can hang main thread, async
    }

    /**
     * Checks if a {@link Location} is within a specific distance of another {@link Location}.
     *
     * @param location the location to check for {@link Location}
     * @param other    the other {@link Location}
     * @param distance the distance to check for
     * @return true if the {@link Location} is within the distance
     */
    public static boolean isWithinX(Location location, Location other, double distance) {
        return location.getWorld().equals(other.getWorld()) &&
                Math.abs(other.getX() - location.getX()) <= distance && Math.abs(other.getZ() - location.getZ()) <= distance;
    }

    /**
     * Gets the highest {@link Location} at another specified {@link Location}.
     *
     * @param origin the {@link Location} the location to find at
     * @return the highest {@link Location} from origin
     */
    public static Location getHighestLocation(Location origin) {
        return getHighestLocation(origin, null);
    }

    /**
     * Gets the highest {@link Location} at another specified {@link Location}.
     *
     * @param origin the {@link Location} the location to find at
     * @param def    the default {@link Location} if not found
     * @return the highest {@link Location} from origin
     */
    public static Location getHighestLocation(Location origin, Location def) {
        Preconditions.checkNotNull(origin, "The location cannot be null");

        Location cloned = origin.clone();
        World world = cloned.getWorld();
        int x = cloned.getBlockX();
        int y = world.getMaxHeight();
        int z = cloned.getBlockZ();
        while (y > origin.getBlockY()) {
            Block block = world.getBlockAt(x, --y, z);
            if (!block.isEmpty()) {
                Location next = block.getLocation();
                next.setPitch(origin.getPitch());
                next.setYaw(origin.getYaw());
                return next;
            }
        }

        return def;
    }

    /**
     * Checks if a {@link PotionEffectType} is a debuff.
     *
     * @param type the {@link PotionEffectType} to check
     * @return true if the {@link PotionEffectType} is a debuff
     */
    public static boolean isDebuff(PotionEffectType type) {
        return DEBUFF_TYPES.contains(type);
    }

    /**
     * Checks if a {@link PotionEffect} is a debuff.
     *
     * @param potionEffect the {@link PotionEffect} to check
     * @return true if the {@link PotionEffect} is a debuff
     */
    public static boolean isDebuff(PotionEffect potionEffect) {
        return isDebuff(potionEffect.getType());
    }

    /**
     * Checks if a {@link ThrownPotion} is a debuff.
     *
     * @param thrownPotion the {@link ThrownPotion} to check
     * @return true if the {@link ThrownPotion} is a debuff
     */
    public static boolean isDebuff(ThrownPotion thrownPotion) {
        for (PotionEffect effect : thrownPotion.getEffects()) {
            if (isDebuff(effect)) {
                return true;
            }
        }

        return false;
    }
}
