package com.doctordark.base.user;

import com.doctordark.base.BasePlugin;
import com.doctordark.util.GenericUtils;
import com.google.common.collect.Sets;
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.LinkedHashMap;
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
        this.ignoring.addAll(GenericUtils.createList(map.get("ignoring"), String.class));
        this.messageSpying.addAll(GenericUtils.createList(map.get("messageSpying"), String.class));

        Object object = map.get("lastRepliedTo");
        if (object instanceof String) {
            this.lastRepliedTo = UUID.fromString((String) object);
        }

        if ((object = map.get("inStaffChat")) instanceof Boolean) {
            this.inStaffChat = (Boolean) object;
        }

        if ((object = map.get("globalChatVisible")) instanceof Boolean) {
            this.globalChatVisible = (Boolean) object;
        }

        if ((object = map.get("staffChatVisible")) instanceof Boolean) {
            this.staffChatVisible = (Boolean) object;
        }

        if ((object = map.get("messagesVisible")) instanceof Boolean) {
            this.messagesVisible = (Boolean) object;
        }

        if ((object = map.get("lastSpeakTimeMillis")) instanceof String) {
            this.lastSpeakTimeMillis = Long.parseLong((String) object);
        }

        if ((object = map.get("lastReceivedMessageMillis")) instanceof String) {
            this.lastReceivedMessageMillis = Long.parseLong((String) object);
        }

        if ((object = map.get("lastSentMessageMillis")) instanceof String) {
            this.lastSentMessageMillis = Long.parseLong((String) object);
        }
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("uniqueID", uniqueId.toString());
        map.put("ignoring", new ArrayList<>(ignoring));
        map.put("messageSpying", new ArrayList<>(messageSpying));
        if (lastRepliedTo != null) {
            map.put("lastRepliedTo", lastRepliedTo.toString());
        }

        map.put("inStaffChat", inStaffChat);
        map.put("globalChatVisible", globalChatVisible);
        map.put("staffChatVisible", staffChatVisible);
        map.put("messagesVisible", messagesVisible);
        map.put("lastSpeakTimeMillis", Long.toString(lastSpeakTimeMillis));
        map.put("lastReceivedMessageMillis", Long.toString(lastReceivedMessageMillis));
        map.put("lastSentMessageMillis", Long.toString(lastSentMessageMillis));
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
        long slowChatDelay = BasePlugin.getPlugin().getServerHandler().getChatSlowedDelay() * 1000L;
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
