package com.doctordark.base;

import com.doctordark.util.chat.Lang;
import com.doctordark.util.PersistableLocation;
import com.doctordark.util.SignHandler;
import com.doctordark.util.itemdb.SimpleItemDb;
import com.doctordark.util.chat.HttpMojangLang;
import com.doctordark.util.chat.MojangLang;
import lombok.Getter;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;
import com.doctordark.util.cuboid.Cuboid;
import com.doctordark.util.cuboid.NamedCuboid;
import com.doctordark.util.itemdb.ItemDb;

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

    @Getter
    private MojangLang language;

    private void registerManagers() {
        this.itemDb = new SimpleItemDb(this);
        this.signHandler = new SignHandler(this);

        this.language = new HttpMojangLang();
        try {
            Lang.initialize("en_US");
            //this.language.index("1.7.10", Locale.ENGLISH);
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
