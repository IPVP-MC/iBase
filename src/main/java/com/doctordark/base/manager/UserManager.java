package com.doctordark.base.manager;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface UserManager {

    /**
     * Checks if a player is in staff chat.
     *
     * @param uuid the uuid of player to check
     * @return true if player is in staff-chat
     */
    public boolean isInStaffChat(UUID uuid);

    /**
     * Sets if a player is in staff chat mode.
     *
     * @param uuid the uuid of player to set for
     * @param chat if should be in staff chat
     */
    public void setInStaffChat(UUID uuid, boolean chat);

    /**
     * Checks if a player is vanished.
     *
     * @param uuid the uuid of player to check
     * @return true if player is vanished
     */
    public boolean isVanished(UUID uuid);

    /**
     * Sets if a player is vanished.
     *
     * @param uuid the uuid of player to set for
     * @param vanish if should be vanished
     */
    public void setVanished(UUID uuid, boolean vanish);

    /**
     * Gets the remaining chat time of a player.
     *
     * @param uuid the uuid of player to check for
     * @return the remaining speak time for player
     */
    public double getRemainingChatTime(UUID uuid);

    /**
     * Gets the last chat time of a player.
     *
     * @param uuid the uuid of player to check for
     * @return the last chat time in millis
     */
    public long getLastChatTime(UUID uuid);

    /**
     * Sets the last chat time for a player.
     *
     * @param uuid the uuid of player to set for
     * @param millis the last chat time to set
     */
    public void setLastChatTime(UUID uuid, long millis);

    /**
     * Gets the map of message spies.
     *
     * @return the message spy map
     */
    public Map<String, List<String>> getMessageSpyMap();

    /**
     * Gets the message spying list of a player.
     *
     * @param uuid the uuid of player to check for
     * @return the list of players spying on
     */
    public List<String> getMessageSpyList(UUID uuid);

    /**
     * Loads the data from file.
     */
    public void loadData();

    /**
     * Saves the data to file.
     */
    public void saveData();
}
