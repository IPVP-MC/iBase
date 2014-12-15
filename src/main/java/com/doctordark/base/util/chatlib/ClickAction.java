package com.doctordark.base.util.chatlib;

import net.minecraft.server.v1_8_R1.EnumClickAction;

public enum ClickAction {

    OPEN_URL(EnumClickAction.OPEN_URL),
    OPEN_FILE(EnumClickAction.OPEN_FILE),
    RUN_COMMAND(EnumClickAction.RUN_COMMAND),
    TWITCH_USER_INFO(EnumClickAction.TWITCH_USER_INFO),
    SUGGEST_COMMAND(EnumClickAction.SUGGEST_COMMAND),
    CHANGE_PAGE(EnumClickAction.CHANGE_PAGE),;

    private EnumClickAction clickAction;

    ClickAction(EnumClickAction action) {
        this.clickAction = action;
    }

    public EnumClickAction getNMS() {
        return this.clickAction;
    }
}
