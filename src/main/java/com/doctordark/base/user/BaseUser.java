package com.doctordark.base.user;

import com.doctordark.base.BasePlugin;
import com.doctordark.base.listener.VanishPriority;
import com.doctordark.base.util.GenericUtils;
import com.doctordark.base.util.PersistableLocation;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class BaseUser implements ConfigurationSerializable {

    private final TreeSet<String> ignoring = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
    private final Set<String> messageSpying = Sets.newHashSet();
    private final List<String> addressHistories = Lists.newArrayList();
    private final List<NameHistory> nameHistories = Lists.newArrayList();

    private final UUID userUUID;
    private Location backLocation;
    private UUID lastRepliedTo;
    private boolean vanished;
    private boolean inStaffChat;
    private boolean toggledStaffChat;
    private boolean toggledChat;
    private boolean toggledMessages;
    private long lastSpeakTimeMillis;
    private long lastReceivedMessageMillis;
    private long lastSentMessageMillis;

    public BaseUser(UUID userUUID) {
        this.userUUID = userUUID;
    }

    public BaseUser(Map<String, Object> map) {
        if (map.containsKey("userUUID")) {
            this.userUUID = UUID.fromString((String) map.get("userUUID"));
        } else {
            this.userUUID = null;
        }

        if (map.containsKey("ignoring")) {
            this.ignoring.addAll(GenericUtils.castList(map.get("ignoring"), String.class));
        }

        if (map.containsKey("messageSpying")) {
            this.messageSpying.addAll(GenericUtils.castList(map.get("messageSpying"), String.class));
        }

        if (map.containsKey("ipHistories")) {
            this.addressHistories.addAll(GenericUtils.castList(map.get("ipHistories"), String.class));
        }

        if (map.containsKey("nameHistories")) {
            this.nameHistories.addAll(GenericUtils.castList(map.get("nameHistories"), NameHistory.class));
        }

        if (map.containsKey("backLocation")) {
            Object object = map.get("backLocation");
            if ((object instanceof PersistableLocation)) {
                this.backLocation = ((PersistableLocation) object).getLocation();
            }
        }

        if (map.containsKey("lastRepliedTo")) {
            this.lastRepliedTo = UUID.fromString((String) map.get("lastRepliedTo"));
        }

        if (map.containsKey("vanished")) {
            this.vanished = (Boolean) map.get("vanished");
        }

        if (map.containsKey("inStaffChat")) {
            this.inStaffChat = (Boolean) map.get("inStaffChat");
        }

        if (map.containsKey("toggledStaffChat")) {
            this.toggledStaffChat = (Boolean) map.get("toggledStaffChat");
        }

        if (map.containsKey("toggledChat")) {
            this.toggledChat = (Boolean) map.get("toggledChat");
        }

        if (map.containsKey("toggledMessages")) {
            this.toggledMessages = (Boolean) map.get("toggledMessages");
        }

        if (map.containsKey("lastSpeakTimeMillis")) {
            this.lastSpeakTimeMillis = ((Integer) map.get("lastSpeakTimeMillis") * 1000L);
        }

        if (map.containsKey("lastReceivedMessageMillis")) {
            this.lastReceivedMessageMillis = ((Integer) map.get("lastReceivedMessageMillis") * 1000L);
        }

        if (map.containsKey("lastSentMessageMillis")) {
            this.lastReceivedMessageMillis = ((Integer) map.get("lastSentMessageMillis") * 1000L);
        }
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = Maps.newHashMap();
        map.put("userUUID", getUserUUID().toString());
        map.put("ignoring", new ArrayList<>(getIgnoring()));
        map.put("messageSpying", new ArrayList<>(getMessageSpyingIds()));
        map.put("ipHistories", getAddressHistories());
        map.put("nameHistories", getNameHistories());

        if (backLocation != null) {
            map.put("backLocation", new PersistableLocation(backLocation));
        }

        if (lastRepliedTo != null) {
            map.put("lastRepliedTo", lastRepliedTo.toString());
        }

        map.put("vanished", isVanished());
        map.put("inStaffChat", isInStaffChat());
        map.put("toggledStaffChat", isToggledStaffChat());
        map.put("toggledChat", isToggledChat());
        map.put("toggledMessages", isToggledMessages());
        map.put("lastSpeakTimeMillis", (int) (getLastSpeakTimeMillis() / 1000L));
        map.put("lastReceivedMessageMillis", (int) (getLastReceivedMessageMillis() / 1000L));
        map.put("lastSentMessageMillis", (int) (getLastSentMessageMillis() / 1000L));
        return map;
    }

    public TreeSet<String> getIgnoring() {
        return this.ignoring;
    }

    public Set<String> getMessageSpyingIds() {
        return this.messageSpying;
    }

    public List<String> getAddressHistories() {
        return this.addressHistories;
    }

    public void logAddress(String address) {
        if (!this.addressHistories.contains(address)) {
            this.addressHistories.add(address);
        }
    }

    public List<NameHistory> getNameHistories() {
        return this.nameHistories;
    }

    public void tryLoggingName(Player player) {
        String playerName = player.getName();
        for (NameHistory nameHistory : getNameHistories()) {
            if (nameHistory.getName().contains(playerName)) {
                return;
            }
        }

        long millis = System.currentTimeMillis();
        this.nameHistories.add(new NameHistory(playerName, millis));
    }

    public Player getLastRepliedTo() {
        return this.lastRepliedTo == null ? null : Bukkit.getServer().getPlayer(this.lastRepliedTo);
    }

    public void setLastRepliedTo(BaseUser baseUser) {
        this.lastRepliedTo = baseUser.getUserUUID();
    }

    public void setLastRepliedTo(UUID lastRepliedTo) {
        this.lastRepliedTo = lastRepliedTo;
    }

    public Location getBackLocation() {
        return this.backLocation;
    }

    public void setBackLocation(Location backLocation) {
        this.backLocation = backLocation;
    }

    public boolean isVanished() {
        return this.vanished;
    }

    public void setVanished(boolean vanished, boolean update) {
        this.vanished = vanished;
        if (update) {
            updateVanishedState(vanished);
        }
    }

    public void updateVanishedState(boolean vanished) {
        Player player = getPlayer();
        if ((player == null) || (!player.isOnline())) {
            return;
        }

        VanishPriority playerPriority = VanishPriority.of(player);
        for (Player target : Bukkit.getServer().getOnlinePlayers()) {
            if ((vanished) && (playerPriority.isMoreThan(VanishPriority.of(target)))) {
                target.hidePlayer(player);
            } else {
                target.showPlayer(player);
            }
        }
    }

    public boolean isInStaffChat() {
        return this.inStaffChat;
    }

    public void setInStaffChat(boolean inStaffChat) {
        this.inStaffChat = inStaffChat;
    }

    public boolean isToggledStaffChat() {
        return this.toggledStaffChat;
    }

    public void setToggledStaffChat(boolean toggledStaffChat) {
        this.toggledStaffChat = toggledStaffChat;
    }

    public boolean isToggledChat() {
        return this.toggledChat;
    }

    public void setToggledChat(boolean toggledChat) {
        this.toggledChat = toggledChat;
    }

    public boolean isToggledMessages() {
        return this.toggledMessages;
    }

    public void setToggledMessages(boolean toggledMessages) {
        this.toggledMessages = toggledMessages;
    }

    public long getLastSpeakTimeMillis() {
        return this.lastSpeakTimeMillis;
    }

    public void setLastSpeakTimeMillis(long lastSpeakTimeMillis) {
        this.lastSpeakTimeMillis = lastSpeakTimeMillis;
    }

    public long getLastSpeakTimeRemaining() {
        if (this.lastSpeakTimeMillis > 0L) {
            return this.lastSpeakTimeMillis - System.currentTimeMillis();
        }

        return 0L;
    }

    public void updateLastSpeakTime() {
        BasePlugin plugin = JavaPlugin.getPlugin(BasePlugin.class);
        long slowChatDelay = plugin.getServerHandler().getChatSlowedDelay() * 1000L;
        this.lastSpeakTimeMillis = (System.currentTimeMillis() + slowChatDelay);
    }

    public long getLastReceivedMessageMillis() {
        return this.lastReceivedMessageMillis;
    }

    public void setLastReceivedMessageMillis(long lastReceivedMessageMillis) {
        this.lastReceivedMessageMillis = lastReceivedMessageMillis;
    }

    public long getLastSentMessageMillis() {
        return this.lastSentMessageMillis;
    }

    public void setLastSentMessageMillis(long lastSentMessageMillis) {
        this.lastSentMessageMillis = lastSentMessageMillis;
    }

    public UUID getUserUUID() {
        return this.userUUID;
    }

    public String getName() {
        return Iterables.getLast(this.nameHistories).getName();
    }

    public Player getPlayer() {
        Server server = Bukkit.getServer();
        return server.getPlayer(getUserUUID());
    }
}
