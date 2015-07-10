package com.doctordark.base.warp;

import java.util.List;

public interface WarpManager {

    Warp getWarp(String id);

    List<Warp> getWarps();

    boolean containsWarp(Warp warp);

    void createWarp(Warp warp);

    void removeWarp(Warp warp);

    int getWarpDelaySeconds();

    int getNearbyPlayerRadiusCancel();

    void reloadWarpData();

    void saveWarpData();
}
