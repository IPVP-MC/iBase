package com.doctordark.base.user;

import com.doctordark.base.BasePlugin;
import com.doctordark.base.util.GenericUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Represents a {@link ServerParticipator}.
 */
public abstract class ServerParticipator implements ConfigurationSerializable {

    private final UUID uniqueId;

    private final Set<String> ignoring = Sets.newTreeSet(String.CASE_INSENSITIVE_ORDER);
    private final Set<String> messageSpying = Sets.newHashSet();

    private UUID lastRepliedTo;
    private boolean inStaffChat;
    private boolean globalChatVisible = true;
    private boolean staffChatVisible = true;
    private boolean messagesVisible = true;
    private long lastSpeakTimeMillis;
    private long lastReceivedMessageMillis;
    private long lastSentMessageMillis;

    /**
     * Constructs a {@link ServerParticipator} from a given {@link UUID}.
     *
     * @param uniqueId the {@link UUID}
     */
    public ServerParticipator(UUID uniqueId) {
        this.uniqueId = uniqueId;
    }

    /**
     * Constructs a {@link ServerParticipator} from a map.
     *
     * @param map the map to construct from
     */
    public ServerParticipator(Map<String, Object> map) {
        this.uniqueId = UUID.fromString((String) map.get("uniqueID"));
        this.ignoring.addAll(GenericUtils.castList(map.get("ignoring"), String.class));
        this.messageSpying.addAll(GenericUtils.castList(map.get("messageSpying"), String.class));

        if (map.containsKey("lastRepliedTo")) {
            this.lastRepliedTo = UUID.fromString((String) map.get("lastRepliedTo"));
        }

        if (map.containsKey("inStaffChat")) {
            this.inStaffChat = (Boolean) map.get("inStaffChat");
        }

        if (map.containsKey("globalChatVisible")) {
            this.globalChatVisible = (Boolean) map.get("globalChatVisible");
        }

        if (map.containsKey("staffChatVisible")) {
            this.staffChatVisible = (Boolean) map.get("staffChatVisible");
        }

        if (map.containsKey("messagesVisible")) {
            this.messagesVisible = (Boolean) map.get("messagesVisible");
        }

        if (map.containsKey("lastSpeakTimeMillis")) {
            this.lastSpeakTimeMillis = Long.parseLong((String) map.get("lastSpeakTimeMillis"));
        }

        if (map.containsKey("lastReceivedMessageMillis")) {
            this.lastReceivedMessageMillis = Long.parseLong((String) map.get("lastReceivedMessageMillis"));
        }

        if (map.containsKey("lastSentMessageMillis")) {
            this.lastSentMessageMillis = Long.parseLong((String) map.get("lastSentMessageMillis"));
        }
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = Maps.newHashMap();
        map.put("uniqueID", uniqueId.toString());
        map.put("ignoring", Lists.newArrayList(getIgnoring()));
        map.put("messageSpying", Lists.newArrayList(getMessageSpying()));
        if (lastRepliedTo != null) {
            map.put("lastRepliedTo", lastRepliedTo.toString());
        }

        map.put("inStaffChat", isInStaffChat());
        map.put("globalChatVisible", isGlobalChatVisible());
        map.put("staffChatVisible", isStaffChatVisible());
        map.put("messagesVisible", isMessagesVisible());
        map.put("lastSpeakTimeMillis", Long.toString(getLastSpeakTimeMillis()));
        map.put("lastReceivedMessageMillis", Long.toString(getLastReceivedMessageMillis()));
        map.put("lastSentMessageMillis", Long.toString(getLastSentMessageMillis()));
        return map;
    }

    /**
     * Gets the name of this {@link ServerParticipator}.
     *
     * @return the name
     */
    public abstract String getName();

    /**
     * Gets the {@link UUID} of this {@link ServerParticipator}.
     *
     * @return the {@link UUID}
     */
    public UUID getUniqueId() {
        return uniqueId;
    }

    public Set<String> getIgnoring() {
        return ignoring;
    }

    public Set<String> getMessageSpying() {
        return messageSpying;
    }

    public UUID getLastRepliedTo() {
        return lastRepliedTo;
    }

    public Player getLastRepliedToPlayer() {
        return Bukkit.getPlayer(lastRepliedTo);
    }

    public void setLastRepliedTo(UUID lastRepliedTo) {
        this.lastRepliedTo = lastRepliedTo;
    }

    /**
     * Gets if this {@link ServerParticipator} is in staff chat.
     *
     * @return true if is in staff chat
     */
    public boolean isInStaffChat() {
        return inStaffChat;
    }

    /**
     * Sets if this {@link ServerParticipator} is in staff chat.
     *
     * @param inStaffChat if should be in staff chat
     */
    public void setInStaffChat(boolean inStaffChat) {
        this.inStaffChat = inStaffChat;
    }

    public boolean isGlobalChatVisible() {
        return globalChatVisible;
    }

    public void setGlobalChatVisible(boolean globalChatVisible) {
        this.globalChatVisible = globalChatVisible;
    }

    public boolean isStaffChatVisible() {
        return staffChatVisible;
    }

    public void setStaffChatVisible(boolean staffChatVisible) {
        this.staffChatVisible = staffChatVisible;
    }

    public boolean isMessagesVisible() {
        return messagesVisible;
    }

    public void setMessagesVisible(boolean messagesVisible) {
        this.messagesVisible = messagesVisible;
    }

    public long getLastSpeakTimeRemaining() {
        if (this.lastSpeakTimeMillis > 0L) {
            return this.lastSpeakTimeMillis - System.currentTimeMillis();
        } else {
            return 0L;
        }
    }

    public long getLastSpeakTimeMillis() {
        return lastSpeakTimeMillis;
    }

    public void setLastSpeakTimeMillis(long lastSpeakTimeMillis) {
        this.lastSpeakTimeMillis = lastSpeakTimeMillis;
    }

    public void updateLastSpeakTime() {
        BasePlugin plugin = BasePlugin.getPlugin();
        long slowChatDelay = plugin.getServerHandler().getChatSlowedDelay() * 1000L;
        this.lastSpeakTimeMillis = (System.currentTimeMillis() + slowChatDelay);
    }

    public long getLastReceivedMessageMillis() {
        return lastReceivedMessageMillis;
    }

    public void setLastReceivedMessageMillis(long lastReceivedMessageMillis) {
        this.lastReceivedMessageMillis = lastReceivedMessageMillis;
    }

    public long getLastSentMessageMillis() {
        return lastSentMessageMillis;
    }

    public void setLastSentMessageMillis(long lastSentMessageMillis) {
        this.lastSentMessageMillis = lastSentMessageMillis;
    }
}
