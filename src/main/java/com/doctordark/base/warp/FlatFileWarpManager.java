package com.doctordark.base.warp;

import com.doctordark.util.Config;
import com.doctordark.util.GenericUtils;
import org.apache.commons.collections4.map.CaseInsensitiveMap;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class FlatFileWarpManager implements WarpManager {

    private String warpDelayWords;
    private long warpDelayMillis;
    private long warpDelayTicks;

    private int nearbyPlayerRadiusCancel;

    private Map<String, Warp> warps = new CaseInsensitiveMap<>();

    private final JavaPlugin plugin;
    private Config config;

    public FlatFileWarpManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.reloadWarpData();
    }

    @Override
    public Collection<String> getWarpNames() {
        return warps.keySet();
    }

    @Override
    public Collection<Warp> getWarps() {
        return warps.values();
    }

    @Override
    public Warp getWarp(String warpName) {
        return warps.get(warpName);
    }

    @Override
    public boolean containsWarp(Warp warp) {
        return warps.containsValue(warp);
    }

    @Override
    public void createWarp(Warp warp) {
        warps.put(warp.getName(), warp);
    }

    @Override
    public Warp removeWarp(String warpName) {
        return warps.remove(warpName);
    }

    @Override
    public String getWarpDelayWords() {
        return warpDelayWords;
    }

    @Override
    public long getWarpDelayMillis() {
        return warpDelayMillis;
    }

    @Override
    public long getWarpDelayTicks() {
        return warpDelayTicks;
    }

    @Override
    public int getNearbyPlayerRadiusCancel() {
        return nearbyPlayerRadiusCancel;
    }

    @Override
    public void reloadWarpData() {
        this.config = new Config(plugin, "warps");

        List<Warp> warps = GenericUtils.createList(config.get("warps"), Warp.class);
        this.warps = new CaseInsensitiveMap<>(warps.size());
        for (Warp warp : warps) {
            this.warps.put(warp.getName(), warp);
        }

        warpDelayMillis = plugin.getConfig().getLong("warp-delay-millis", 0L);
        warpDelayTicks = warpDelayMillis / 50L;
        warpDelayWords = DurationFormatUtils.formatDurationWords(warpDelayMillis, true, true);

        nearbyPlayerRadiusCancel = plugin.getConfig().getInt("nearby-player-radius-cancel", 0);
    }

    @Override
    public void saveWarpData() {
        plugin.getConfig().set("warp-delay-millis", warpDelayMillis);
        plugin.getConfig().set("nearby-player-radius-cancel", nearbyPlayerRadiusCancel);
        plugin.saveConfig();

        config.set("warps", warps);
        config.save();
    }
}
