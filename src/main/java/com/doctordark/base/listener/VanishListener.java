package com.doctordark.base.listener;

import com.doctordark.base.BasePlugin;
import com.doctordark.base.user.BaseUser;
import com.doctordark.base.user.UserManager;
import com.doctordark.util.BukkitUtils;
import com.google.common.collect.Maps;
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
import org.bukkit.block.DoubleChest;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.UUID;

public class VanishListener implements Listener {

    private static final String FAKE_CHEST_PREFIX = "[F] ";
    private final Map<UUID, Location> fakeChestLocationMap;
    private final BasePlugin plugin;

    public VanishListener(BasePlugin plugin) {
        this.plugin = plugin;
        this.fakeChestLocationMap = Maps.newHashMap();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onPlayerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        UserManager userManager = plugin.getUserManager();
        final BaseUser baseUser = userManager.getUser(uuid);
        if (baseUser.isVanished()) {
            new BukkitRunnable() {
                public void run() {
                    player.sendMessage(ChatColor.GOLD + "You have joined vanished.");
                    baseUser.updateVanishedState(true);
                }
            }.runTask(plugin);
        }

        VanishPriority userPriority = VanishPriority.of(player);
        for (Player target : Bukkit.getServer().getOnlinePlayers()) {
            BaseUser baseTarget = userManager.getUser(target.getUniqueId());
            if ((baseTarget.isVanished()) && (VanishPriority.of(target).isMoreThan(userPriority))) {
                player.hidePlayer(target);
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        BaseUser baseUser = plugin.getUserManager().getUser(uuid);
        if (baseUser.isVanished()) {
            event.setQuitMessage(null);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onPlayerDropItem(PlayerInteractEntityEvent event) {
        Entity entity = event.getRightClicked();
        if (entity instanceof Player) {
            Player player = event.getPlayer();
            if (!player.isOp()) {
                return;
            }

            UUID uuid = player.getUniqueId();
            BaseUser baseUser = plugin.getUserManager().getUser(uuid);
            if (baseUser.isVanished()) {
                Player clicked = (Player) entity;
                player.openInventory(clicked.getInventory());
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        BaseUser baseUser = plugin.getUserManager().getUser(uuid);
        if (baseUser.isVanished()) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You cannot drop items whilst vanished.");
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        BaseUser baseUser = plugin.getUserManager().getUser(uuid);
        if (baseUser.isVanished()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        UUID uuid = player.getUniqueId();
        BaseUser baseUser = plugin.getUserManager().getUser(uuid);
        if (baseUser.isVanished()) {
            event.setDeathMessage(null);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getCause() == EntityDamageEvent.DamageCause.SUICIDE) {
            return;
        }

        Entity entity = event.getEntity();
        if (entity instanceof Player) {
            Player attacked = (Player) entity;
            BaseUser attackedUser = plugin.getUserManager().getUser(attacked.getUniqueId());

            Player attacker = BukkitUtils.getFinalAttacker(event, true);
            if (attackedUser.isVanished()) {
                if (attacker != null) {
                    attacker.sendMessage(ChatColor.RED + "That player is vanished.");
                }

                event.setCancelled(true);
                return;
            }

            if (attacker != null) {
                BaseUser attackerUser = plugin.getUserManager().getUser(attacker.getUniqueId());
                if (attackerUser.isVanished()) {
                    attacker.sendMessage(ChatColor.RED + "You cannot attack players whilst vanished.");
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        BaseUser baseUser = plugin.getUserManager().getUser(uuid);
        if (baseUser.isVanished()) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You cannot break blocks whilst vanished.");
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        BaseUser baseUser = plugin.getUserManager().getUser(uuid);
        if (baseUser.isVanished()) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You cannot place blocks whilst vanished.");
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Action action = event.getAction();
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        BaseUser baseUser = plugin.getUserManager().getUser(uuid);
        if ((action == Action.PHYSICAL) && (baseUser.isVanished())) {
            event.setCancelled(true);
            return;
        }

        if (action == Action.RIGHT_CLICK_BLOCK) {
            Block block = event.getClickedBlock();
            BlockState state = block.getState();
            if ((baseUser.isVanished()) && ((state instanceof InventoryHolder))) {
                InventoryHolder holder = (InventoryHolder) block.getState();
                Inventory inventory = holder.getInventory();
                InventoryType type = inventory.getType();
                if (type == InventoryType.CHEST) {
                    Chest chest = (Chest) state;
                    Location chestLocation = chest.getLocation();

                    Inventory chestInventory = chest.getInventory();
                    Inventory fakeInventory = Bukkit.getServer().createInventory(null, chestInventory.getContents().length, FAKE_CHEST_PREFIX + type.getDefaultTitle());
                    fakeInventory.setContents(chestInventory.getContents());

                    event.setCancelled(true);
                    player.openInventory(fakeInventory);

                    handleFakeChest(player, chest, true);
                    fakeChestLocationMap.put(uuid, chestLocation);
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        UUID uuid = player.getUniqueId();
        if (fakeChestLocationMap.containsKey(uuid)) {
            Location chestLocation = fakeChestLocationMap.get(uuid);
            Block block = chestLocation.getBlock();
            BlockState blockState = block.getState();
            if (blockState instanceof Chest) {
                Chest chest = (Chest) blockState;
                handleFakeChest(player, chest, false);
            }

            fakeChestLocationMap.remove(uuid);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onInventoryClick(InventoryClickEvent event) {
        HumanEntity humanEntity = event.getWhoClicked();
        if ((humanEntity instanceof Player)) {
            Player player = (Player) humanEntity;
            UUID uuid = player.getUniqueId();
            if (fakeChestLocationMap.containsKey(uuid)) {
                ItemStack stack = event.getCurrentItem();
                if ((stack != null) && (stack.getType() != Material.AIR) && (!player.isOp())) {
                    event.setCancelled(true);
                    player.sendMessage(ChatColor.RED + "You cannot interact with fake chest inventories.");
                }
            }
        }
    }

    public void handleFakeChest(Player player, Chest chest, boolean open) {
        Inventory chestInventory = chest.getInventory();
        if (chestInventory instanceof DoubleChestInventory) {
            DoubleChestInventory doubleChestInventory = (DoubleChestInventory) chestInventory;
            DoubleChest doubleChest = doubleChestInventory.getHolder();
            chest = (Chest) doubleChest.getLeftSide();
        }

        final int chestX = chest.getX();
        final int chestY = chest.getY();
        final int chestZ = chest.getZ();

        PacketPlayOutBlockAction packet = new PacketPlayOutBlockAction(chestX, chestY, chestZ, Blocks.CHEST, 1, open ? 1 : 0);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);

        Location chestLocation = chest.getLocation();
        player.playSound(chestLocation, open ? Sound.CHEST_OPEN : Sound.CHEST_CLOSE, 1.0F, 1.0F);
    }
}
