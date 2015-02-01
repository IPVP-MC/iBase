package com.doctordark.base.manager;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface UserManager {

    /**
     * Gets a set of players that are staff-chatting.
     *
     * @return set of players in staff chat
     */
    Set<String> getStaffChatting();

    /**
     * Checks if a player is in staff chat.
     *
     * @param uuid the uuid of player to check
     * @return true if player is in staff-chat
     */
    boolean isInStaffChat(UUID uuid);

    /**
     * Sets if a player is in staff chat mode.
     *
     * @param uuid the uuid of player to set for
     * @param chat if should be in staff chat
     */
    void setInStaffChat(UUID uuid, boolean chat);




    /**
     * Gets a set of players that are vanished.
     *
     * @return set of players vanished
     */
    Set<String> getVanished();

    /**
     * Checks if a player is vanished.
     *
     * @param uuid the uuid of player to check
     * @return true if player is vanished
     */
    boolean isVanished(UUID uuid);

    /**
     * Sets if a player is vanished.
     *
     * @param uuid the uuid of player to set for
     * @param vanish if should be vanished
     */
    void setVanished(UUID uuid, boolean vanish);




    /**
     * Gets a map of players that have been delayed
     * for speaking within x seconds.
     *
     * @return map of speak delayed players
     */
    Map<String, Long> getLastSpeakMap();

    /**
     * Gets the remaining chat time of a player.
     *
     * @param uuid the uuid of player to check for
     * @return the remaining speak time for player
     */
    long getRemainingChatDelayTime(UUID uuid);

    /**
     * Gets the last chat time of a player.
     *
     * @param uuid the uuid of player to check for
     * @return the last chat time in millis
     */
    long getLastSpeakTime(UUID uuid);

    /**
     * Updates the last chat time for a player.
     *
     * @param uuid the uuid of player to update for
     */
    void updateLastSpeakTime(UUID uuid);




    /**
     * Gets a map of message spying players.
     *
     * @return the message spying map
     */
    Map<String, List<String>> getMessageSpyMap();

    /**
     * Gets the message spying list of a player.
     *
     * @param uuid the uuid of player to check for
     * @return the list of players spying on
     */
    List<String> getMessageSpying(UUID uuid);




    /**
     * Gets a map of user IP addresses.
     *
     * @return map of user IP addresses
     */
    Map<String, List<String>> getAddressMap();

    /**
     * Gets a set of past IP addresses used by a player.
     *
     * @param uuid the uuid of player to get for
     * @return the set of previous and current ips
     */
    List<String> getAddresses(UUID uuid);

    /**
     * Saves an IP address to the set of user IP addresses.
     *
     * @param uuid the uuid of player to save for
     * @param address the address to save
     */
    void removeAddress(UUID uuid, String address);

    /**
     * Saves an IP address to the set of user IP addresses.
     *
     * @param uuid the uuid of player to save for
     * @param address the address to save
     */
    void saveAddress(UUID uuid, String address);




    /**
     * Reloads the user data from storage.
     */
    void reloadUserData();

    /**
     * Saves the data to storage.
     */
    void saveUserData();
}
