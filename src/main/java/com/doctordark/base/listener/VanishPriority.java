package com.doctordark.base.listener;

import org.bukkit.entity.Player;

public enum VanishPriority {

    HIGHEST(6), HIGH(5), MEDIUM(4), NORMAL(3), LOW(2), LOWEST(1), NONE(0);

    private final int level;

    VanishPriority(int level) {
        this.level = level;
    }

    public int getLevel() {
        return this.level;
    }

    public boolean isMoreThan(VanishPriority other) {
        return getLevel() > other.getLevel();
    }

    public static VanishPriority of(int level) {
        for (VanishPriority vanishPriority : VanishPriority.values()) {
            if (vanishPriority.getLevel() == level) {
                return vanishPriority;
            }
        }

        return NONE;
    }

    public static VanishPriority of(Player player) {
        for (int i = HIGHEST.getLevel(); i > NONE.getLevel(); i--) {
            if (player.hasPermission("base.vanishpriority." + i)) {
                return of(i);
            }
        }

        return NONE;
    }
}
