package com.doctordark.base.manager;

public class FlatFileServerManager implements ServerManager {

    private boolean chatEnabled;
    private boolean chatSlowed;
    private int chatSlowedDelay;

    public FlatFileServerManager() {
        // TODO: load from actual config
        chatEnabled = true;
        chatSlowed = false;
        chatSlowedDelay = 15;
    }

    @Override
    public boolean isChatEnabled() {
        return chatEnabled;
    }

    @Override
    public void setChatEnabled(boolean enabled) {
        this.chatEnabled = enabled;
    }

    @Override
    public boolean isChatSlowed() {
        return chatSlowed;
    }

    @Override
    public void setChatSlowed(boolean slowed) {
        this.chatSlowed = slowed;
    }

    @Override
    public int getSlowChatDelay() {
        return chatSlowedDelay;
    }

    @Override
    public void setSlowChatDelay(int delay) {
        this.chatSlowedDelay = delay;
    }
}
