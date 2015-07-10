package com.doctordark.base.warp;

import com.doctordark.util.Config;
import com.doctordark.util.GenericUtils;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class FlatFileWarpManager implements WarpManager {

    private int warpDelaySeconds;
    private int nearbyPlayerRadiusCancel;

    private final JavaPlugin plugin;
    private List<Warp> warps;
    private Config config;

    public FlatFileWarpManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.reloadWarpData();
    }

    @Override
    public Warp getWarp(String id) {
        for (Warp warp : warps) {
            if (warp.getName().equalsIgnoreCase(id)) {
                return warp;
            }
        }

        return null;
    }

    @Override
    public List<Warp> getWarps() {
        return warps;
    }

    @Override
    public boolean containsWarp(Warp warp) {
        return warps.contains(warp);
    }

    @Override
    public void createWarp(Warp warp) {
        warps.add(warp);
    }

    @Override
    public void removeWarp(Warp warp) {
        warps.remove(warp);
    }

    @Override
    public int getWarpDelaySeconds() {
        return warpDelaySeconds;
    }

    @Override
    public int getNearbyPlayerRadiusCancel() {
        return nearbyPlayerRadiusCancel;
    }

    @Override
    public void reloadWarpData() {
        config = new Config(plugin, "warps");
        warps = GenericUtils.createList(config.get("warps"), Warp.class);

        warpDelaySeconds = plugin.getConfig().getInt("warp-delay-seconds", 0);
        nearbyPlayerRadiusCancel = plugin.getConfig().getInt("nearby-player-radius-cancel", 0);
    }

    @Override
    public void saveWarpData() {
        plugin.getConfig().set("warp-delay-seconds", warpDelaySeconds);
        plugin.getConfig().set("nearby-player-radius-cancel", nearbyPlayerRadiusCancel);
        plugin.saveConfig();

        config.set("warps", warps);
        config.save();
    }
}
