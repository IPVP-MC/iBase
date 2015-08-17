package com.doctordark.base.user;

import com.doctordark.base.BasePlugin;
import com.doctordark.base.event.PlayerVanishEvent;
import com.doctordark.base.kit.Kit;
import com.doctordark.base.listener.VanishPriority;
import com.doctordark.util.GenericUtils;
import com.doctordark.util.PersistableLocation;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.net.InetAddresses;
import net.minecraft.server.v1_7_R4.DataWatcher;
import net.minecraft.server.v1_7_R4.PacketPlayOutEntityEquipment;
import net.minecraft.server.v1_7_R4.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_7_R4.PlayerConnection;
import net.minecraft.util.gnu.trove.map.TObjectIntMap;
import net.minecraft.util.gnu.trove.map.TObjectLongMap;
import net.minecraft.util.gnu.trove.map.hash.TObjectIntHashMap;
import net.minecraft.util.gnu.trove.map.hash.TObjectLongHashMap;
import net.minecraft.util.gnu.trove.procedure.TObjectIntProcedure;
import net.minecraft.util.gnu.trove.procedure.TObjectLongProcedure;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftItem;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BaseUser extends ServerParticipator {

    private final List<String> addressHistories = Lists.newArrayList();
    private final List<NameHistory> nameHistories = Lists.newArrayList();

    private Location backLocation;
    private boolean messagingSounds;
    private boolean vanished;
    private boolean glintEnabled = true;
    private long lastGlintUse;

    private final TObjectIntMap<String> kitUseMap = new TObjectIntHashMap<>();
    private final TObjectLongMap<String> kitCooldownMap = new TObjectLongHashMap<>();

    /**
     * @see ServerParticipator#ServerParticipator(UUID)
     */
    public BaseUser(UUID uniqueID) {
        super(uniqueID);
    }

    /**
     * @see ServerParticipator#ServerParticipator(Map)
     */
    public BaseUser(Map<String, Object> map) {
        super(map);

        this.addressHistories.addAll(GenericUtils.createList(map.get("addressHistories"), String.class));
        if (map.containsKey("nameHistories")) {
            this.nameHistories.addAll(GenericUtils.createList(map.get("nameHistories"), NameHistory.class));
        }

        if (map.containsKey("backLocation")) {
            Object object = map.get("backLocation");
            if (object instanceof PersistableLocation) {
                PersistableLocation persistableLocation = ((PersistableLocation) object);
                if (persistableLocation.getWorld() != null) {
                    this.backLocation = ((PersistableLocation) object).getLocation();
                }
            }
        }

        if (map.containsKey("messagingSounds")) {
            this.messagingSounds = (Boolean) map.get("messagingSounds");
        }

        if (map.containsKey("vanished")) {
            this.vanished = (Boolean) map.get("vanished");
        }

        if (map.containsKey("glintEnabled")) {
            this.glintEnabled = (Boolean) map.get("glintEnabled");
        }

        if (map.containsKey("lastGlintUse")) {
            this.lastGlintUse = Long.parseLong((String) map.get("lastGlintUse"));
        }

        this.kitUseMap.putAll(GenericUtils.castMap(map.get("kit-use-map"), String.class, Integer.class));
        this.kitCooldownMap.putAll(GenericUtils.castMap(map.get("kit-cooldown-map"), String.class, Long.class));
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = super.serialize();
        map.put("addressHistories", this.addressHistories);
        map.put("nameHistories", this.nameHistories);
        if (backLocation != null && backLocation.getWorld() != null) { // the world may be null
            map.put("backLocation", new PersistableLocation(backLocation));
        }

        map.put("messagingSounds", messagingSounds);
        map.put("vanished", vanished);
        map.put("glintEnabled", glintEnabled);
        map.put("lastGlintUse", Long.toString(lastGlintUse));

        Map<String, Integer> kitUseSaveMap = new HashMap<>(kitUseMap.size());
        kitUseMap.forEachEntry(new TObjectIntProcedure<String>() {
            @Override
            public boolean execute(String s, int i) {
                kitUseSaveMap.put(s, i);
                return true;
            }
        });

        map.put("kit-use-map", kitUseSaveMap);

        Map<String, Integer> kitCooldownSaveMap = new HashMap<>(kitCooldownMap.size());
        kitCooldownMap.forEachEntry(new TObjectLongProcedure<String>() {
            @Override
            public boolean execute(String s, long i) {
                kitCooldownMap.put(s, i);
                return true;
            }
        });

        map.put("kit-cooldown-map", kitCooldownSaveMap);
        return map;
    }

    public long getRemainingKitCooldown(Kit kit) {
        long remaining = kitCooldownMap.get(kit.getName());
        if (remaining == kitCooldownMap.getNoEntryValue()) return 0L;
        return remaining - System.currentTimeMillis();
    }

    public void updateKitCooldown(Kit kit) {
        kitCooldownMap.put(kit.getName(), System.currentTimeMillis() + kit.getDelayMillis());
    }

    public int getKitUses(Kit kit) {
        int i = this.kitUseMap.get(kit.getName());
        return i == this.kitUseMap.getNoEntryValue() ? 0 : i;
    }

    public int incrementKitUses(Kit kit) {
        return kitUseMap.adjustOrPutValue(kit.getName(), 1, 1);
    }

    @Override
    public String getName() {
        return getLastKnownName();
    }

    public List<NameHistory> getNameHistories() {
        return this.nameHistories;
    }

    public void tryLoggingName(Player player) {
        Preconditions.checkNotNull(player, "Cannot log null player");
        String playerName = player.getName();
        for (NameHistory nameHistory : nameHistories) {
            if (nameHistory.getName().contains(playerName)) {
                return;
            }
        }

        nameHistories.add(new NameHistory(playerName, System.currentTimeMillis()));
    }

    public List<String> getAddressHistories() {
        return this.addressHistories;
    }

    public void tryLoggingAddress(String address) {
        Preconditions.checkNotNull(address, "Cannot log null address");
        if (!addressHistories.contains(address)) {
            Validate.isTrue(InetAddresses.isInetAddress(address), "Not an Inet address");
            addressHistories.add(address);
        }
    }

    public Location getBackLocation() {
        return this.backLocation;
    }

    public void setBackLocation(Location backLocation) {
        this.backLocation = backLocation;
    }

    public boolean isMessagingSounds() {
        return messagingSounds;
    }

    public void setMessagingSounds(boolean messagingSounds) {
        this.messagingSounds = messagingSounds;
    }

    public boolean isVanished() {
        return vanished;
    }

    public void setVanished(boolean vanished) {
        setVanished(vanished, true);
    }

    public void setVanished(boolean vanished, boolean update) {
        setVanished(Bukkit.getPlayer(getUniqueId()), vanished, update);
    }

    public boolean setVanished(@Nullable Player player, boolean vanished, boolean update) {
        if (this.vanished != vanished) {
            if (player != null) {
                PlayerVanishEvent event = new PlayerVanishEvent(player, vanished);
                Bukkit.getPluginManager().callEvent(event);
                if (event.isCancelled()) return false;

                if (update) {
                    updateVanishedState(player, vanished);
                }
            }

            this.vanished = vanished;
            return true;
        }

        return false;
    }

    public void updateVanishedState(Player player, boolean vanished) {
        player.spigot().setCollidesWithEntities(!vanished);
        player.showInvisibles(vanished); // allow vanished players to see those invisible.

        VanishPriority playerPriority = VanishPriority.of(player);
        for (Player target : Bukkit.getOnlinePlayers()) {
            if (player.equals(target)) continue;
            if (vanished && playerPriority.isMoreThan(VanishPriority.of(target))) {
                target.hidePlayer(player);
            } else {
                target.showPlayer(player);
            }
        }
    }

    /**
     * Checks if glint for {@link org.bukkit.enchantments.Enchantment}s on
     * {@link org.bukkit.inventory.ItemStack}s is enabled for this {@link BaseUser}.
     *
     * @return true if glint is enabled
     */
    public boolean isGlintEnabled() {
        return glintEnabled;
    }

    /**
     * Sets if glint for {@link org.bukkit.enchantments.Enchantment}s on
     * {@link org.bukkit.inventory.ItemStack}s is enabled for this {@link BaseUser}.
     *
     * @param glintEnabled the value to set
     */
    public void setGlintEnabled(boolean glintEnabled) {
        this.setGlintEnabled(glintEnabled, true);
    }

    /**
     * Sets if glint for {@link org.bukkit.enchantments.Enchantment}s on
     * {@link org.bukkit.inventory.ItemStack}s is enabled for this {@link BaseUser}.
     *
     * @param glintEnabled the value to set
     * @param update       if it should update for nearby entities
     */
    public void setGlintEnabled(boolean glintEnabled, boolean update) {
        Player player = toPlayer();
        if (player == null || !player.isOnline()) {
            return;
        }

        this.glintEnabled = glintEnabled;
        if (BasePlugin.getPlugin().getServerHandler().useProtocolLib) {
            int viewDistance = Bukkit.getViewDistance();
            PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
            for (Entity entity : player.getNearbyEntities(viewDistance, viewDistance, viewDistance)) {
                if (entity instanceof org.bukkit.entity.Item) {
                    org.bukkit.entity.Item item = (org.bukkit.entity.Item) entity;
                    if (item instanceof CraftItem) {
                        CraftItem craftItem = (CraftItem) item;
                        DataWatcher watcher = craftItem.getHandle().getDataWatcher();
                        connection.sendPacket(new PacketPlayOutEntityMetadata(entity.getEntityId(), watcher, true));
                    }
                } else if (entity instanceof Player && !entity.equals(player)) {
                    Player target = (Player) entity;
                    PlayerInventory inventory = target.getInventory();
                    int entityID = entity.getEntityId();

                    ItemStack[] armour = inventory.getArmorContents();
                    for (int i = 0; i < armour.length; i++) {
                        ItemStack stack = armour[i];
                        if (stack != null && stack.getType() != Material.AIR) {
                            connection.sendPacket(new PacketPlayOutEntityEquipment(entityID, i + 1, CraftItemStack.asNMSCopy(stack)));
                        }
                    }

                    ItemStack stack = inventory.getItemInHand();
                    if (stack != null && stack.getType() != Material.AIR) {
                        connection.sendPacket(new PacketPlayOutEntityEquipment(entityID, /*CraftEntityEquipment.WEAPON_SLOT(*/0, CraftItemStack.asNMSCopy(stack)));
                    }
                }
            }
        }
    }

    public long getLastGlintUse() {
        return lastGlintUse;
    }

    public void setLastGlintUse(long lastGlintUse) {
        this.lastGlintUse = lastGlintUse;
    }

    /**
     * Gets the last known name of this {@link BaseUser}.
     *
     * @return the last known name
     */
    public String getLastKnownName() {
        return Iterables.getLast(nameHistories).getName();
    }

    /**
     * Converts this {@link BaseUser} to a {@link Player}.
     *
     * @return the converted {@link Player}
     */
    public Player toPlayer() {
        return Bukkit.getPlayer(getUniqueId());
    }
}