package com.doctordark.base.listener;

import org.bukkit.entity.Player;

public enum VanishPriority {

    HIGHEST(6), HIGH(5), MEDIUM(4), NORMAL(3), LOW(2), LOWEST(1), NONE(0);

    private final int level;

    VanishPriority(int level) {
        this.level = level;
    }

    /**
     * Gets the ID level for this priority.
     *
     * @return the vanish priority level
     */
    public int getLevel() {
        return this.level;
    }

    /**
     * Checks if this vanish priority is more than another.
     *
     * @param other the other priority to compare to
     * @return true if this priority is more than the other
     */
    public boolean isMoreThan(VanishPriority other) {
        return getLevel() > other.getLevel();
    }

    /**
     * Gets the vanish priority of a specific level.
     *
     * @param level the level to get from
     * @return the vanish priority of level
     */
    public static VanishPriority of(int level) {
        for (VanishPriority vanishPriority : VanishPriority.values()) {
            if (vanishPriority.getLevel() == level) {
                return vanishPriority;
            }
        }

        return NONE;
    }

    /**
     * Gets the vanish priority of a specific player.
     *
     * @param player the player to get from
     * @return the vanish priority of player
     */
    public static VanishPriority of(Player player) {
        for (int i = HIGHEST.getLevel(); i > NONE.getLevel(); i--) {
            if (player.hasPermission("base.vanishpriority." + i)) {
                return of(i);
            }
        }

        return NONE;
    }
}
