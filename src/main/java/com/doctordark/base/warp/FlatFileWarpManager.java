package com.doctordark.base.warp;

import com.doctordark.util.Config;
import com.doctordark.util.GenericUtils;
import lombok.Getter;
import org.apache.commons.collections4.map.CaseInsensitiveMap;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class FlatFileWarpManager implements WarpManager {

    @Getter
    private String warpDelayWords;

    @Getter
    private long warpDelayMillis;

    @Getter
    private long warpDelayTicks;

    @Getter
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
        return this.warps.keySet();
    }

    @Override
    public Collection<Warp> getWarps() {
        return this.warps.values();
    }

    @Override
    public Warp getWarp(String warpName) {
        return this.warps.get(warpName);
    }

    @Override
    public boolean containsWarp(Warp warp) {
        return this.warps.containsValue(warp);
    }

    @Override
    public void createWarp(Warp warp) {
        this.warps.put(warp.getName(), warp);
    }

    @Override
    public Warp removeWarp(String warpName) {
        return this.warps.remove(warpName);
    }

    @Override
    public void reloadWarpData() {
        this.config = new Config(this.plugin, "warps");

        List<Warp> warps = GenericUtils.createList(this.config.get("warps"), Warp.class);
        this.warps = new CaseInsensitiveMap<>(warps.size());
        for (Warp warp : warps) {
            this.warps.put(warp.getName(), warp);
        }

        this.warpDelayMillis = plugin.getConfig().getLong("warp-delay-millis", 0L);
        this.warpDelayTicks = warpDelayMillis / 50L;
        this.warpDelayWords = DurationFormatUtils.formatDurationWords(warpDelayMillis, true, true);
        this.nearbyPlayerRadiusCancel = plugin.getConfig().getInt("nearby-player-radius-cancel", 0);
    }

    @Override
    public void saveWarpData() {
        this.plugin.getConfig().set("warp-delay-millis", warpDelayMillis);
        this.plugin.getConfig().set("nearby-player-radius-cancel", nearbyPlayerRadiusCancel);
        this.plugin.saveConfig();

        this.config.set("warps", warps);
        this.config.save();
    }
}
