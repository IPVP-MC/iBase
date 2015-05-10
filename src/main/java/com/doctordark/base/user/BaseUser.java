package com.doctordark.base.user;

import com.doctordark.base.listener.VanishPriority;
import com.doctordark.base.util.GenericUtils;
import com.doctordark.base.util.PersistableLocation;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.net.InetAddresses;
import net.minecraft.util.org.apache.commons.lang3.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BaseUser extends ServerParticipator {

    private final List<String> addressHistories = Lists.newArrayList();
    private final List<NameHistory> nameHistories = Lists.newArrayList();

    private Location backLocation;
    private boolean messagingSounds;
    private boolean vanished;

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
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = super.serialize();
        map.put("addressHistories", getAddressHistories());
        map.put("nameHistories", getNameHistories());
        if (backLocation != null) map.put("backLocation", new PersistableLocation(backLocation));
        map.put("messagingSounds", isMessagingSounds());
        map.put("vanished", vanished);
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