package com.doctordark.util.bossbar;

import net.minecraft.server.v1_7_R4.EntityLiving;
import net.minecraft.server.v1_7_R4.EntityWither;
import net.minecraft.server.v1_7_R4.World;
import org.bukkit.Location;

public class WitherBar extends BossBar {

    public WitherBar(Location location, String title) {
        super(location, title);
    }

    public WitherBar(Location location, String title, int percent) {
        super(location, title, percent);
    }

    @Override
    int getMaxHealth() {
        return 300;
    }

    @Override
    EntityLiving constructBossEntity(World world) {
        return new EntityWither(world);
    }
}
