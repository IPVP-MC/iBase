package com.doctordark.base.manager;

import java.util.List;

public interface ServerManager {

    /**
     * Gets the delay of announcement broadcasting.
     *
     * @return the announcement broadcast delay
     */
    int getAnnouncementDelay();

    /**
     * Sets the delay of announcement broadcasting.
     *
     * @param delay the announcement broadcast delay to set
     */
    void setAnnouncementDelay(int delay);

    /**
     * Gets a list of announcements for the server.
     *
     * @return list of automatic announcements
     */
    List<String> getAnnouncements();

    /**
     * Checks if global chat is enabled.
     *
     * @return true if global chat is enabled
     */
    boolean isChatEnabled();

    /**
     * Sets if global chat is enabled.
     *
     * @param enabled if chat should be enabled
     */
    void setChatEnabled(boolean enabled);

    /**
     * Checks if global chat is in slow mode.
     *
     * @return true if global chat is slowed
     */
    boolean isChatSlowed();

    /**
     * Sets if global chat is in slow mode.
     *
     * @param slowed if chat should be slowed
     */
    void setChatSlowed(boolean slowed);

    /**
     * Gets the delay before players can speak
     * again when the chat is slowed down.
     *
     * @return the slow chat delay time
     */
    int getSlowChatDelay();

    /**
     * Sets the delay before players can speak
     * again when the chat is slowed down.
     *
     * @param delay the delay to set
     */
    void setSlowChatDelay(int delay);

    int getMaxPlayers();

    void setMaxPlayers(int maxPlayers);

    /**
     * Loads the data from file.
     */
    void reloadServerData();

    /**
     * Saves the data to file.
     */
    void saveServerData();
}
