package com.doctordark.base.listener;

import com.doctordark.base.BasePlugin;
import com.doctordark.base.event.PlayerVanishEvent;
import com.doctordark.base.user.BaseUser;
import com.doctordark.util.BukkitUtils;
import net.minecraft.server.v1_7_R4.Blocks;
import net.minecraft.server.v1_7_R4.PacketPlayOutBlockAction;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class VanishListener implements Listener {

    private static final String CHEST_INTERACT_PERMISSION = "base.vanish.chestinteract";
    private static final String INVENTORY_INTERACT_PERMISSION = "base.vanish.inventorysee";
    private static final String FAKE_CHEST_PREFIX = "[F] ";

    private final Map<UUID, Location> fakeChestLocationMap = new HashMap<>();
    private final Set<Player> onlineVanishedPlayers = new HashSet<>();

    private final BasePlugin plugin;

    public VanishListener(BasePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // Hide any current vanished players to this player.
        if (!onlineVanishedPlayers.isEmpty()) {
            VanishPriority selfPriority = VanishPriority.of(player);
            if (selfPriority != VanishPriority.HIGHEST) {
                for (Player target : onlineVanishedPlayers) {
                    if (plugin.getUserManager().getUser(target.getUniqueId()).isVanished() && VanishPriority.of(target).isMoreThan(selfPriority)) {
                        player.hidePlayer(target);
                    }
                }
            }
        }

        BaseUser baseUser = plugin.getUserManager().getUser(player.getUniqueId());
        if (baseUser.isVanished()) {
            onlineVanishedPlayers.add(player);
            player.sendMessage(ChatColor.GOLD + "You have joined vanished.");
            baseUser.updateVanishedState(player, true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (plugin.getUserManager().getUser(event.getPlayer().getUniqueId()).isVanished()) {
            onlineVanishedPlayers.remove(event.getPlayer());
            event.setQuitMessage(null);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerVanish(PlayerVanishEvent event) {
        if (event.isVanished()) {
            onlineVanishedPlayers.add(event.getPlayer());
        } else {
            onlineVanishedPlayers.remove(event.getPlayer());
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Entity entity = event.getRightClicked();
        if (entity instanceof Player) {
            Player player = event.getPlayer();
            if (!player.isSneaking() && player.hasPermission(INVENTORY_INTERACT_PERMISSION) && plugin.getUserManager().getUser(player.getUniqueId()).isVanished()) {
                player.openInventory(((Player) entity).getInventory());
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onEntityTarget(EntityTargetEvent event) {
        if (event.getReason() == EntityTargetEvent.TargetReason.CUSTOM) {
            return;
        }

        Entity target = event.getTarget();
        Entity entity = event.getEntity();
        if ((entity instanceof ExperienceOrb || entity instanceof LivingEntity) && target instanceof Player && plugin.getUserManager().getUser(target.getUniqueId()).isVanished()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        if (plugin.getUserManager().getUser(event.getPlayer().getUniqueId()).isVanished()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (plugin.getUserManager().getUser(event.getEntity().getUniqueId()).isVanished()) {
            event.setDeathMessage(null);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onEntityDamage(EntityDamageEvent event) {
        EntityDamageEvent.DamageCause cause = event.getCause();
        if (cause == EntityDamageEvent.DamageCause.VOID || cause == EntityDamageEvent.DamageCause.SUICIDE) {
            return;
        }

        Entity entity = event.getEntity();
        if (entity instanceof Player) {
            Player attacked = (Player) entity;
            BaseUser attackedUser = plugin.getUserManager().getUser(attacked.getUniqueId());

            Player attacker = BukkitUtils.getFinalAttacker(event, true);
            if (attackedUser.isVanished()) {
                if (attacker != null && VanishPriority.of(attacked) != VanishPriority.NONE) {
                    attacker.sendMessage(ChatColor.RED + "That player is vanished.");
                }

                event.setCancelled(true);
                return;
            }

            if (attacker != null && plugin.getUserManager().getUser(attacker.getUniqueId()).isVanished()) {
                attacker.sendMessage(ChatColor.RED + "You cannot attack players whilst vanished.");
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        BaseUser baseUser = plugin.getUserManager().getUser(player.getUniqueId());
        if (baseUser.isVanished() && !player.hasPermission("base.vanish.build")) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You cannot build whilst vanished.");
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        BaseUser baseUser = plugin.getUserManager().getUser(player.getUniqueId());
        if (baseUser.isVanished() && !player.hasPermission("base.vanish.build")) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You cannot build whilst vanished.");
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onBucketEmpty(PlayerBucketEmptyEvent event) {
        Player player = event.getPlayer();
        BaseUser baseUser = plugin.getUserManager().getUser(player.getUniqueId());
        if (baseUser.isVanished() && !player.hasPermission("base.vanish.build")) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You cannot build whilst vanished.");
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        switch (event.getAction()) {
            case PHYSICAL:
                if (plugin.getUserManager().getUser(uuid).isVanished()) {
                    event.setCancelled(true);
                }
                break;
            case RIGHT_CLICK_BLOCK:
                Block block = event.getClickedBlock();
                BlockState state = block.getState();
                if (state instanceof Chest && plugin.getUserManager().getUser(uuid).isVanished()) {
                    Chest chest = (Chest) state;
                    Location chestLocation = chest.getLocation();
                    InventoryType type = chest.getInventory().getType();
                    if (type == InventoryType.CHEST && fakeChestLocationMap.putIfAbsent(uuid, chestLocation) == null) {
                        ItemStack[] contents = chest.getInventory().getContents();
                        Inventory fakeInventory = Bukkit.createInventory(null, contents.length, FAKE_CHEST_PREFIX + type.getDefaultTitle());
                        fakeInventory.setContents(contents);

                        event.setCancelled(true);
                        player.openInventory(fakeInventory);

                        handleFakeChest(player, chest, true);
                        fakeChestLocationMap.put(uuid, chestLocation);
                    }
                }
                break;
            default:
                break;
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        Location chestLocation;
        if ((chestLocation = fakeChestLocationMap.remove(player.getUniqueId())) != null) {
            BlockState blockState = chestLocation.getBlock().getState();
            if (blockState instanceof Chest) {
                handleFakeChest(player, (Chest) blockState, false);
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onInventoryClick(InventoryClickEvent event) {
        HumanEntity humanEntity = event.getWhoClicked();
        if (humanEntity instanceof Player) {
            Player player = (Player) humanEntity;
            if (fakeChestLocationMap.containsKey(player.getUniqueId())) {
                ItemStack stack = event.getCurrentItem();
                if (stack != null && stack.getType() != Material.AIR && !player.hasPermission(CHEST_INTERACT_PERMISSION)) {
                    event.setCancelled(true);
                    player.sendMessage(ChatColor.RED + "You cannot interact with fake chest inventories.");
                }
            }
        }
    }

    public static void handleFakeChest(Player player, Chest chest, boolean open) {
        Inventory chestInventory = chest.getInventory();
        if (chestInventory instanceof DoubleChestInventory) {
            chest = (Chest) ((DoubleChestInventory) chestInventory).getHolder().getLeftSide();
        }

        // Fake the chest open.
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutBlockAction(chest.getX(), chest.getY(), chest.getZ(), Blocks.CHEST, 1, (open ? 1 : 0)));
        player.playSound(chest.getLocation(), open ? Sound.CHEST_OPEN : Sound.CHEST_CLOSE, 1.0F, 1.0F);
    }
}
