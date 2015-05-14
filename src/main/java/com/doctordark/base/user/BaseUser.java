package com.doctordark.base.user;

import com.doctordark.base.listener.VanishPriority;
import com.doctordark.base.util.GenericUtils;
import com.doctordark.base.util.PersistableLocation;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.net.InetAddresses;
import net.minecraft.server.v1_7_R4.Packet;
import net.minecraft.server.v1_7_R4.PacketPlayOutEntityEquipment;
import net.minecraft.server.v1_7_R4.PlayerConnection;
import net.minecraft.util.org.apache.commons.lang3.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftEntityEquipment;
import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Collection;
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

        if (map.containsKey("addressHistories")) {
            this.addressHistories.addAll(GenericUtils.castList(map.get("addressHistories"), String.class));
        }

        if (map.containsKey("nameHistories")) {
            this.nameHistories.addAll(GenericUtils.castList(map.get("nameHistories"), NameHistory.class));
        }

        if (map.containsKey("backLocation")) {
            Object object = map.get("backLocation");
            if (object instanceof PersistableLocation) {
                this.backLocation = ((PersistableLocation) object).getLocation();
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
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = super.serialize();
        map.put("addressHistories", getAddressHistories());
        map.put("nameHistories", getNameHistories());
        if (backLocation != null) map.put("backLocation", new PersistableLocation(backLocation));
        map.put("messagingSounds", isMessagingSounds());
        map.put("vanished", isVanished());
        map.put("glintEnabled", isGlintEnabled());
        map.put("lastGlintUse", Long.toString(getLastGlintUse()));
        return map;
    }

    @Override
    public String getName() {
        return getLastKnownName();
    }

    public List<NameHistory> getNameHistories() {
        return this.nameHistories;
    }

    public void tryLoggingName(Player player) {
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
        this.vanished = vanished;
        if (update) updateVanishedState(vanished);
    }

    public void updateVanishedState(boolean vanished) {
        Player player = toPlayer();
        if (player == null || !player.isOnline()) {
            return;
        }

        player.spigot().setCollidesWithEntities(!vanished);
        player.setCanSeeInvisibles(vanished); // allow vanished players to see those invisible.

        VanishPriority playerPriority = VanishPriority.of(player);
        for (Player target : Bukkit.getServer().getOnlinePlayers()) {
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
        int viewDistance = Bukkit.getViewDistance();
        PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
        for (Entity entity : player.getNearbyEntities(viewDistance, viewDistance, viewDistance)) {
            if (entity instanceof Player) {
                if (entity.equals(player)) continue;

                Player target = (Player) entity;
                PlayerInventory inventory = target.getInventory();
                int entityID = entity.getEntityId();

                ItemStack[] armour = inventory.getArmorContents();
                for (int i = 0; i < armour.length; i++) {
                    ItemStack stack = armour[i];
                    if (stack != null && stack.getType() != Material.AIR) {
                        connection.sendPacket(new PacketPlayOutEntityEquipment(entityID, (i + 1), CraftItemStack.asNMSCopy(stack)));
                    }
                }

                ItemStack stack = inventory.getItemInHand();
                if (stack != null && stack.getType() != Material.AIR) {
                    connection.sendPacket(new PacketPlayOutEntityEquipment(entityID, CraftEntityEquipment.WEAPON_SLOT, CraftItemStack.asNMSCopy(stack)));
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
        Server server = Bukkit.getServer();
        return server.getPlayer(getUniqueId());
    }
}