package com.doctordark.base.listener;

import com.doctordark.base.BasePlugin;
import com.doctordark.util.cuboid.CoordinatePair;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.primitives.Ints;
import net.minecraft.util.gnu.trove.iterator.TObjectIntIterator;
import net.minecraft.util.gnu.trove.map.TObjectIntMap;
import net.minecraft.util.gnu.trove.map.hash.TObjectIntHashMap;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.SpawnerSpawnEvent;
import org.bukkit.event.player.PlayerBreedEntityEvent;
import org.bukkit.event.player.PlayerTemptEntityEvent;

/**
 * Listener that stacks multiple {@link LivingEntity}s into one for lag reduction.
 */
public class MobstackListener implements Listener {

    private static final int NATURAL_STACK_RADIUS = 81;
    private static final int MAX_STACKED_QUANTITY = 40;
    private static final String STACKED_PREFIX = ChatColor.GOLD + "x";

    private final Table<CoordinatePair, EntityType, Integer> naturalSpawnStacks = HashBasedTable.create();
    private final TObjectIntMap<Location> spawnerStacks = new TObjectIntHashMap<>(); // key is the spawner location and value is the representing entity ID.

    private final BasePlugin plugin;

    public MobstackListener(BasePlugin plugin) {
        this.plugin = plugin;
    }

    private CoordinatePair fromLocation(Location location) {
        return new CoordinatePair(location.getWorld(),
                NATURAL_STACK_RADIUS * Math.round(location.getBlockX() / NATURAL_STACK_RADIUS),
                NATURAL_STACK_RADIUS * Math.round(location.getBlockZ() / NATURAL_STACK_RADIUS));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerTemptEntity(PlayerTemptEntityEvent event) {
        int stackedQuantity = getStackedQuantity(event.getEntity());
        if (stackedQuantity >= MAX_STACKED_QUANTITY) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "This entity is already max stacked.");
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerBreedEntity(PlayerBreedEntityEvent event) {
        if (event.getEntity() instanceof Horse) {
            return;
        }

        LivingEntity chosen = plugin.getRandom().nextBoolean() ? event.getFirstParent() : event.getSecondParent();
        int stackedQuantity = getStackedQuantity(chosen);
        if (stackedQuantity == -1) stackedQuantity = 1;

        setStackedQuantity(chosen, ++stackedQuantity);
        event.getPlayer().sendMessage(ChatColor.GREEN + "One of the adults bred has been increased a stack.");
        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onSpawnerSpawn(SpawnerSpawnEvent event) {
        switch (event.getEntityType()) {
            case BAT:
            case ENDERMAN:
            case PIG_ZOMBIE:
                return;
        }

        CreatureSpawner spawner = event.getSpawner();
        World world = spawner.getWorld();
        if (world != null && world.getEnvironment() == World.Environment.THE_END) {
            return;
        }

        Location location = spawner.getLocation();
        Optional<Integer> entityIdOptional = Optional.fromNullable(spawnerStacks.get(location));
        if (entityIdOptional.isPresent()) {
            int entityId = entityIdOptional.get();

            net.minecraft.server.v1_7_R4.Entity nmsTarget = ((CraftWorld) location.getWorld()).getHandle().getEntity(entityId);
            Entity target = nmsTarget != null ? nmsTarget.getBukkitEntity() : null;
            if (target != null && target instanceof LivingEntity) {
                LivingEntity targetLiving = (LivingEntity) target;
                int stackedQuantity = getStackedQuantity(targetLiving);
                if (stackedQuantity == -1) stackedQuantity = 1;

                if (stackedQuantity < MAX_STACKED_QUANTITY) {
                    setStackedQuantity(targetLiving, ++stackedQuantity);
                    event.setCancelled(true);
                    return;
                }
            }
        }

        // We can safely make this the stacking entity of the spawner.
        spawnerStacks.put(location, event.getEntity().getEntityId());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        EntityType entityType = event.getEntityType();
        switch (entityType) {
            case SLIME:
            case ENDERMAN:
            case BAT:
            case BLAZE:
                return;
        }

        switch (event.getSpawnReason()) {
            case CHUNK_GEN:
            case NATURAL:
            case DEFAULT:
                Location location = event.getLocation();
                CoordinatePair coordinatePair = fromLocation(location);

                Optional<Integer> entityIdOptional = Optional.fromNullable(naturalSpawnStacks.get(coordinatePair, entityType));
                if (entityIdOptional.isPresent()) {
                    int entityId = entityIdOptional.get();
                    net.minecraft.server.v1_7_R4.Entity nmsTarget = ((CraftWorld) location.getWorld()).getHandle().getEntity(entityId);
                    Entity target = nmsTarget == null ? null : nmsTarget.getBukkitEntity();
                    if (target != null && target instanceof LivingEntity) {
                        LivingEntity targetLiving = (LivingEntity) target;

                        //TODO: Check sheep colours before overstacking :)

                        final boolean canSpawn;
                        if (targetLiving instanceof Ageable) {
                            canSpawn = ((Ageable) targetLiving).isAdult();
                        } else {
                            canSpawn = !(targetLiving instanceof Zombie) || !((Zombie) targetLiving).isBaby();
                        }

                        if (canSpawn) {
                            int stackedQuantity = getStackedQuantity(targetLiving);
                            if (stackedQuantity == -1) stackedQuantity = 1;

                            if (stackedQuantity < MAX_STACKED_QUANTITY) {
                                setStackedQuantity(targetLiving, ++stackedQuantity);
                                event.setCancelled(true);
                                return;
                            }
                        }
                    }
                }

                // We can safely make this the stacking entity of the chunk.
                naturalSpawnStacks.put(coordinatePair, entityType, event.getEntity().getEntityId());
                break;
            default:
                break;
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity livingEntity = event.getEntity();
        int stackedQuantity = getStackedQuantity(livingEntity);
        if (stackedQuantity > 1) {
            // Spawn another entity in the killed entity's place.
            LivingEntity respawned = (LivingEntity) livingEntity.getWorld().spawnEntity(livingEntity.getLocation(), event.getEntityType());
            setStackedQuantity(respawned, Math.min(MAX_STACKED_QUANTITY, --stackedQuantity));

            // Always adultify.
            if (respawned instanceof Ageable) ((Ageable) respawned).setAdult();
            if (respawned instanceof Zombie) ((Zombie) respawned).setBaby(false);

            if (spawnerStacks.containsValue(livingEntity.getEntityId())) {
                TObjectIntIterator<Location> iterator = spawnerStacks.iterator();
                while (iterator.hasNext()) {
                    iterator.advance();
                    if (iterator.value() == livingEntity.getEntityId()) {
                        iterator.setValue(respawned.getEntityId());
                        break;
                    }
                }
            }
        }
    }

    /**
     * Gets the stacked quantity of a {@link LivingEntity}.
     *
     * @param livingEntity the {@link LivingEntity} to get for
     * @return the stacked quantity or -1 if is not stacked
     */
    private int getStackedQuantity(LivingEntity livingEntity) {
        String customName = livingEntity.getCustomName();
        if (customName != null) {
            customName = customName.replace(STACKED_PREFIX, "");
            return Ints.tryParse(customName);
        }

        return -1;
    }

    /**
     * Sets the stacked quantity of a {@link LivingEntity}.
     *
     * @param livingEntity the {@link LivingEntity} to set for
     * @param quantity     the quantity to set or 0 to stop stacking
     */
    private void setStackedQuantity(LivingEntity livingEntity, int quantity) {
        Preconditions.checkArgument(quantity >= 0, "Stacked quantity cannot be negative");
        Preconditions.checkArgument(quantity <= MAX_STACKED_QUANTITY, "Stacked quantity cannot be more than " + MAX_STACKED_QUANTITY);
        if (quantity <= 1) {
            livingEntity.setCustomName(null);
        } else {
            livingEntity.setCustomName(STACKED_PREFIX + quantity);
            livingEntity.setCustomNameVisible(false);
        }
    }
}
