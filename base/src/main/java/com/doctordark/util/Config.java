package com.doctordark.util;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

/**
 * Represents a Configuration file to retrieve data.
 */
public class Config extends YamlConfiguration {

    private final String fileName;
    private final JavaPlugin plugin;

    /**
     * Creates a new config with a given name.
     *
     * @param plugin   the plugin to create for
     * @param fileName the name of the file
     */
    public Config(JavaPlugin plugin, String fileName) {
        this(plugin, fileName, ".yml");
    }

    /**
     * Creates a new config with a given name and file extension.
     *
     * @param plugin        the plugin to create for
     * @param fileName      the name of the file
     * @param fileExtension the extension of the file
     */
    public Config(JavaPlugin plugin, String fileName, String fileExtension) {
        this.plugin = plugin;
        this.fileName = fileName + (fileName.endsWith(fileExtension) ? "" : fileExtension);
        this.createFile();
    }

    /**
     * Gets the name of this file.
     *
     * @return the file
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Gets the plugin that owns this config.
     *
     * @return the owning config plugin
     */
    public JavaPlugin getPlugin() {
        return plugin;
    }

    /**
     * Creates configuration file with the fileName in the plugin folder.
     */
    private void createFile() {
        File folder = plugin.getDataFolder();
        try {
            File file = new File(folder, fileName);
            if (!file.exists()) {
                if (plugin.getResource(fileName) != null) {
                    plugin.saveResource(fileName, false);
                } else {
                    save(file);
                }
            } else {
                load(file);
                save(file);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Saves the configuration file to the plugin data.
     */
    public void save() {
        File folder = plugin.getDataFolder();
        try {
            save(new File(folder, fileName));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Config)) return false;

        Config config = (Config) o;

        if (fileName != null ? !fileName.equals(config.fileName) : config.fileName != null) return false;
        return !(plugin != null ? !plugin.equals(config.plugin) : config.plugin != null);
    }

    @Override
    public int hashCode() {
        int result = fileName != null ? fileName.hashCode() : 0;
        result = 31 * result + (plugin != null ? plugin.hashCode() : 0);
        return result;
    }
}
