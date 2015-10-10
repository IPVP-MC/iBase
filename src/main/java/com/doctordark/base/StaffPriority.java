package com.doctordark.base;

import com.google.common.collect.ImmutableMap;
import org.bukkit.entity.Player;

/**
 * Represents the priority of a staff member.
 */
public enum StaffPriority {

    HIGHEST(6), HIGH(5), MEDIUM(4), NORMAL(3), LOW(2), LOWEST(1), NONE(0);

    private final int priorityLevel;

    StaffPriority(int priorityLevel) {
        this.priorityLevel = priorityLevel;
    }

    private static final ImmutableMap<Integer, StaffPriority> BY_ID;

    static {
        ImmutableMap.Builder<Integer, StaffPriority> builder = new ImmutableMap.Builder<>();
        for (StaffPriority staffPriority : values()) {
            builder.put(staffPriority.priorityLevel, staffPriority);
        }

        BY_ID = builder.build();
    }

    /**
     * Gets the ID level for this {@link StaffPriority}.
     *
     * @return the priority level
     */
    public int getPriorityLevel() {
        return this.priorityLevel;
    }

    /**
     * Checks if this {@link StaffPriority} has more priority that another {@link StaffPriority}.
     *
     * @param other the other {@link StaffPriority} to compare to
     * @return true if this {@link StaffPriority} is more than the other
     */
    public boolean isMoreThan(StaffPriority other) {
        return this.priorityLevel > other.priorityLevel;
    }

    /**
     * Gets the {@link StaffPriority} for a specific level.
     *
     * @param level the level to get for
     * @return the {@link StaffPriority} of level, or {@code NONE}
     */
    public static StaffPriority of(int level) {
        return BY_ID.get(level);
    }

    /**
     * Gets the {@link StaffPriority} of a specific {@link Player}.
     *
     * @param player the {@link Player} to get for
     * @return the {@link StaffPriority} of {@link Player}, or {@code NONE}
     */
    public static StaffPriority of(Player player) {
        for (StaffPriority staffPriority : values()) {
            if (player.hasPermission("base.staffpriority." + staffPriority.priorityLevel)) {
                return staffPriority;
            }
        }

        return NONE;
    }
}
