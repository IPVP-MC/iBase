package com.doctordark.base.manager;

public interface ServerManager {

    /**
     * Checks if global chat is enabled.
     *
     * @return true if global chat is enabled
     */
    public boolean isChatEnabled();

    /**
     * Sets if global chat is enabled.
     *
     * @param enabled if chat should be enabled
     */
    public void setChatEnabled(boolean enabled);

    /**
     * Checks if global chat is in slow mode.
     *
     * @return true if global chat is slowed
     */
    public boolean isChatSlowed();

    /**
     * Sets if global chat is in slow mode.
     *
     * @param slowed if chat should be slowed
     */
    public void setChatSlowed(boolean slowed);

    /**
     * Gets the delay before players can speak
     * again when the chat is slowed down.
     *
     * @return the slow chat delay time
     */
    public int getSlowChatDelay();

    /**
     * Sets the delay before players can speak
     * again when the chat is slowed down.
     *
     * @param delay the delay to set
     */
    public void setSlowChatDelay(int delay);
}
