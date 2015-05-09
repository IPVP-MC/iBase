package com.doctordark.util;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import net.minecraft.server.v1_7_R4.EntityPlayer;
import net.minecraft.server.v1_7_R4.MinecraftServer;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.Metadatable;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.ChatPaginator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public final class BukkitUtils {

    private BukkitUtils() {
    }

    // String for a straight line in Bukkit.
    private static final String STRAIGHT_LINE;
    private static final Map<ChatColor, DyeColor> CHAT_DYE_COLOUR_MAP;
    private static final ImmutableSet<PotionEffectType> DEBUFF_TYPES;

    static {
        STRAIGHT_LINE = ChatColor.STRIKETHROUGH.toString() + StringUtils.repeat("-", 256);
        CHAT_DYE_COLOUR_MAP = ImmutableMap.<ChatColor, DyeColor>builder()
                .put(ChatColor.AQUA, DyeColor.LIGHT_BLUE)
                .put(ChatColor.BLACK, DyeColor.BLACK)
                .put(ChatColor.BLUE, DyeColor.LIGHT_BLUE)
                .put(ChatColor.DARK_AQUA, DyeColor.CYAN)
                .put(ChatColor.DARK_BLUE, DyeColor.BLUE)
                .put(ChatColor.DARK_GRAY, DyeColor.GRAY)
                .put(ChatColor.DARK_GREEN, DyeColor.GREEN)
                .put(ChatColor.DARK_PURPLE, DyeColor.PURPLE)
                .put(ChatColor.DARK_RED, DyeColor.RED)
                .put(ChatColor.GOLD, DyeColor.ORANGE)
                .put(ChatColor.GRAY, DyeColor.SILVER)
                .put(ChatColor.GREEN, DyeColor.LIME)
                .put(ChatColor.LIGHT_PURPLE, DyeColor.MAGENTA)
                .put(ChatColor.RED, DyeColor.RED)
                .put(ChatColor.WHITE, DyeColor.WHITE)
                .put(ChatColor.YELLOW, DyeColor.YELLOW).build();
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

    /**
     * Gets the display name of a {@link CommandSender} or their
     * regular name if they don't have a display name.
     *
     * @param sender the {@link CommandSender} to get for
     * @return the resulted display name
     */
    public static String getDisplayName(CommandSender sender) {
        return (sender instanceof Player) ? ((Player) sender).getDisplayName() : sender.getName();
    }

    /**
     * Gets the time in milliseconds a {@link Player} has been idle for
     *
     * @param player the {@link Player} to get for
     * @return the time in milliseconds
     */
    public static long getIdleTime(Player player) {
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        return (entityPlayer.x() <= 0L) ? 0L : MinecraftServer.ar() - entityPlayer.x();
    }

    /**
     * Converts an {@link ChatColor} to a {@link DyeColor}.
     *
     * @param colour the {@link ChatColor} to be converted
     * @return the converted {@link DyeColor}.
     */
    public static DyeColor toDyeColor(ChatColor colour) {
        return CHAT_DYE_COLOUR_MAP.containsKey(colour) ? CHAT_DYE_COLOUR_MAP.get(colour) : DyeColor.WHITE;
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
     * @param metaDatable the {@link Metadatable} to get for
     * @param input       the string to check for
     * @param plugin      the {@link Plugin} that owns the metadata
     * @return the {@link MetadataValue}
     */
    public static MetadataValue getMetaData(Metadatable metaDatable, String input, Plugin plugin) {
        if (metaDatable.hasMetadata(input)) {
            List<MetadataValue> values = metaDatable.getMetadata(input);
            for (MetadataValue value : values) {
                if (value.getOwningPlugin().equals(plugin)) {
                    return value;
                }
            }
        }

        return null;
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
        Player result = null;
        if (ede instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) ede;
            Entity entity = event.getEntity();
            Entity damager = event.getDamager();
            if (damager instanceof Player) {
                result = (Player) damager;
            } else if (damager instanceof Projectile) {
                Projectile projectile = (Projectile) damager;
                if (projectile.getShooter() instanceof Player) {
                    result = (Player) projectile.getShooter();
                }
            }

            if ((ignoreSelf) && (result != null) && (entity != null) && (entity instanceof Player) && (entity.equals(result))) {
                return null;
            }
        }

        return result;
    }

    /**
     * Generates a centered line that isn't split
     * across the screen of the Minecraft client.
     *
     * @return the generated {@link String}
     */
    public static String generateLine() {
        return generateLine(ChatPaginator.GUARANTEED_NO_WRAP_CHAT_PAGE_WIDTH);
    }

    /**
     * Generates a centered line at a specific length
     * that isn't split between each character.
     *
     * @param length the length to build at
     * @return the generated {@link String}
     */
    public static String generateLine(int length) {
        return (length > STRAIGHT_LINE.length()) ? STRAIGHT_LINE : STRAIGHT_LINE.substring(0, length);
    }

    /**
     * Checks if a {@link Location} is within a specific distance of another {@link Location}.
     *
     * @param location1 the location to check for {@link Location}
     * @param location2 the other {@link Location}
     * @param distance  the distance to check for
     * @return true if the {@link Location} is within the distance
     */
    public static boolean isWithinX(Location location1, Location location2, int distance) {
        World world1 = location1.getWorld();
        World world2 = location2.getWorld();
        if (!world1.equals(world2)) {
            return false;
        }

        double x1 = location1.getX();
        double x2 = location2.getX();
        double z1 = location1.getZ();
        double z2 = location2.getZ();
        return ((Math.abs(x2 - x1) <= distance) && (Math.abs(z2 - z1) <= distance));
    }

    /**
     * Returns all possible {@link String}s for a given argument.
     * <p>This returns a list with a limit of 80 completions.</p>
     *
     * @param args          the arguments to get for
     * @param possibilities the possibles to use
     * @return list of possibilities that are correspondent to the argument
     */
    public static List<String> getCompletions(String[] args, Object... possibilities) {
        return getCompletions(args, 80, possibilities);
    }

    /**
     * Returns the possible {@link String}s based on the current array argument search.
     *
     * @param args          the arguments to get for
     * @param limit         the list limit size
     * @param possibilities the possibles to use
     * @return list of possibilities that are correspondent to the argument
     */
    public static List<String> getCompletions(String[] args, int limit, Object... possibilities) {
        Validate.notNull(args, "The arguments cannot be null");
        Validate.notNull(possibilities, "The possibilities cannot be null");

        int count = 0;
        List<String> list = new ArrayList<>();
        for (Object possibility : possibilities) {
            if (possibility == null) {
                continue;
            }

            List<String> adding = new ArrayList<>();
            if (possibility instanceof String) {
                adding.add((String) possibility);
            } else {
                adding.addAll(Arrays.asList(possibility.toString().replace("[", "").replace("]", "").split(", ")));
            }

            final String argument = args[(args.length - 1)];
            for (String added : adding) {
                if (!added.regionMatches(true, 0, argument, 0, argument.length())) {
                    continue;
                }

                count++;
                list.add(added);

                if (count == limit) {
                    break;
                }
            }
        }

        return list;
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
        Validate.notNull(origin, "The location cannot be null");

        Location cloned = origin.clone();
        World world = cloned.getWorld();
        int x = cloned.getBlockX();
        int y = world.getMaxHeight();
        int z = cloned.getBlockZ();
        while (y > 0) {
            y--;
            Location next = new Location(world, x, y, z);
            Block block = next.getBlock();
            if (!block.isEmpty()) {
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
}
