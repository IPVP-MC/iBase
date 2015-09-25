package com.doctordark.base.warp;

import java.util.Collection;

public interface WarpManager {

    Collection<String> getWarpNames();

    Collection<Warp> getWarps();

    Warp getWarp(String warpName);

    boolean containsWarp(Warp warp);

    void createWarp(Warp warp);

    /**
     * Removes a {@link Warp}.
     *
     * @param warpName the name of warp to remove
     * @return the removed warp
     */
    Warp removeWarp(String warpName);

    String getWarpDelayWords();

    long getWarpDelayMillis();

    long getWarpDelayTicks();

    int getNearbyPlayerRadiusCancel();

    void reloadWarpData();

    void saveWarpData();
}
