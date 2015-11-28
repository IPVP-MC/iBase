package org.ipvp.base;

import org.ipvp.util.PersistableLocation;
import org.ipvp.util.SignHandler;
import org.ipvp.util.chat.Lang;
import org.ipvp.util.cuboid.Cuboid;
import org.ipvp.util.cuboid.NamedCuboid;
import org.ipvp.util.itemdb.ItemDb;
import org.ipvp.util.itemdb.SimpleItemDb;
import lombok.Getter;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.Random;

public class BasePlugin extends JavaPlugin {

    @Getter
    private static BasePlugin plugin;

    @Getter
    private Random random = new Random();

    @Getter
    private ItemDb itemDb;

    @Getter
    private SignHandler signHandler;

    private void registerManagers() {
        this.itemDb = new SimpleItemDb(this);
        this.signHandler = new SignHandler(this);

        try {
            Lang.initialize("en_US");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onEnable() {
        BasePlugin.plugin = this;

        ConfigurationSerialization.registerClass(PersistableLocation.class);
        ConfigurationSerialization.registerClass(Cuboid.class);
        ConfigurationSerialization.registerClass(NamedCuboid.class);

        this.registerManagers();
    }

    @Override
    public void onDisable() {
        this.signHandler.cancelTasks(null);

        BasePlugin.plugin = null;
    }
}
