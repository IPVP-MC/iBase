package com.doctordark.base;

import com.google.common.collect.Lists;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class ServerHandler {

    private final List<String> announcements = Lists.newArrayList();
    private final List<String> serverRules = Lists.newArrayList();

    public boolean useProtocolLib;
    private int announcementDelay;
    private long chatSlowedMillis;
    private long chatDisabledMillis;
    private int chatSlowedDelay;
    private int maxPlayers;
    private String broadcastFormat;
    private String fullServerKickMessage;
    private FileConfiguration config;
    private boolean decreasedLagMode;

    private final BasePlugin plugin;

    public ServerHandler(BasePlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        reloadServerData();
    }

    public int getAnnouncementDelay() {
        return this.announcementDelay;
    }

    public void setAnnouncementDelay(int delay) {
        this.announcementDelay = delay;
    }

    public List<String> getAnnouncements() {
        return this.announcements;
    }

    public boolean isChatSlowed() {
        return getRemainingChatSlowedMillis() > 0L;
    }

    public long getChatSlowedMillis() {
        return this.chatSlowedMillis;
    }

    public void setChatSlowedMillis(long ticks) {
        long millis = System.currentTimeMillis();
        this.chatSlowedMillis = (millis + ticks);
    }

    public long getRemainingChatSlowedMillis() {
        return this.chatSlowedMillis - System.currentTimeMillis();
    }

    public boolean isChatDisabled() {
        return getRemainingChatDisabledMillis() > 0L;
    }

    public long getChatDisabledMillis() {
        return this.chatDisabledMillis;
    }

    public void setChatDisabledMillis(long ticks) {
        long millis = System.currentTimeMillis();
        this.chatDisabledMillis = (millis + ticks);
    }

    public long getRemainingChatDisabledMillis() {
        return this.chatDisabledMillis - System.currentTimeMillis();
    }

    public int getChatSlowedDelay() {
        return this.chatSlowedDelay;
    }

    public void setChatSlowedDelay(int delay) {
        this.chatSlowedDelay = delay;
    }

    public int getMaxPlayers() {
        return this.maxPlayers;
    }

    public boolean setMaxPlayers(int maxPlayers) {
        return setMaxPlayers(maxPlayers, true);
    }

    public boolean setMaxPlayers(int maxPlayers, boolean inject) {
        Validate.isTrue(maxPlayers >= 0, "Max players cannot be negative");

        this.maxPlayers = maxPlayers;
        Bukkit.setMaxPlayers(maxPlayers);
        return true;
    }

    public String getBroadcastFormat() {
        return this.broadcastFormat;
    }

    public void setBroadcastFormat(String broadcastFormat) {
        this.broadcastFormat = broadcastFormat;
    }

    public String getFullServerKickMessage() {
        return this.fullServerKickMessage;
    }

    public void setFullServerKickMessage(String fullServerKickMessage) {
        this.fullServerKickMessage = fullServerKickMessage;
    }

    public List<String> getServerRules() {
        return this.serverRules;
    }

    public boolean isDecreasedLagMode() {
        return decreasedLagMode;
    }

    public void setDecreasedLagMode(boolean decreasedLagMode) {
        this.decreasedLagMode = decreasedLagMode;
    }

    public void reloadServerData() {
        plugin.reloadConfig();
        config = plugin.getConfig();

        serverRules.clear();
        for (String each : config.getStringList("server-rules")) {
            serverRules.add(ChatColor.translateAlternateColorCodes('&', each));
        }

        if (config.getConfigurationSection("announcements") != null) {
            announcementDelay = config.getInt("announcements.delay", 300);
            announcements.clear();
            for (String each : config.getStringList("announcements.list")) {
                announcements.add(ChatColor.translateAlternateColorCodes('&', each));
            }
        }

        if (config.getConfigurationSection("chat") != null) {
            chatDisabledMillis = config.getLong("chat.disabled.millis", 0L);
            chatSlowedMillis = config.getLong("chat.slowed.millis", 0L);
            chatSlowedDelay = config.getInt("chat.slowed.delay", 15);
        }

        this.maxPlayers = config.getInt("max-players", 150);
        if (maxPlayers > 0) {
            Bukkit.setMaxPlayers(maxPlayers);
        }

        useProtocolLib = config.getBoolean("use-protocol-lib", true);
        decreasedLagMode = config.getBoolean("decreased-lag-mode");
        broadcastFormat = ChatColor.translateAlternateColorCodes('&',
                config.getString("broadcast.format", "&e[Base] &7%1$s"));
        fullServerKickMessage = ChatColor.translateAlternateColorCodes('&',
                config.getString("full-server-kick-message", "&cThe server is full. \n\n&cDonate for &aVIP&c or above to bypass this restriction."));
    }

    public void saveServerData() {
        config.set("use-protocol-lib", useProtocolLib);
        config.set("chat.disabled.millis", Long.valueOf(this.chatDisabledMillis));
        config.set("chat.slowed.millis", Long.valueOf(this.chatSlowedMillis));
        config.set("chat.slowed-delay", this.chatSlowedDelay);
        config.set("announcements.delay", this.announcementDelay);
        config.set("max-players", this.maxPlayers);
        config.set("decreased-lag-mode", decreasedLagMode);
        plugin.saveConfig();
    }
}
