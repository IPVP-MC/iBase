package com.doctordark.base.user;

import com.doctordark.base.BasePlugin;
import com.doctordark.util.Config;
import com.google.common.base.Preconditions;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.MemorySection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class UserManager {

    private final ConsoleUser console;
    private final JavaPlugin plugin;

    private Config userConfig;
    private Map<UUID, ServerParticipator> participators;

    public UserManager(BasePlugin plugin) {
        this.plugin = plugin;
        this.reloadParticipatorData();

        // Load the ConsoleUser data here.
        ServerParticipator participator = participators.get(ConsoleUser.CONSOLE_UUID);
        if (participator != null) {
            this.console = (ConsoleUser) participator;
        } else {
            this.participators.put(ConsoleUser.CONSOLE_UUID, this.console = new ConsoleUser());
        }
    }

    /**
     * Gets the {@link ConsoleUser}.
     *
     * @return the {@link ConsoleUser}
     */
    public ConsoleUser getConsole() {
        return console;
    }

    /**
     * Gets the {@link ServerParticipator}s held by this {@link UserManager}.
     *
     * @return map of {@link ServerParticipator}s held
     */
    public Map<UUID, ServerParticipator> getParticipators() {
        return this.participators;
    }

    /**
     * Gets a {@link ServerParticipator} by a given {@link CommandSender}.
     *
     * @param sender the {@link CommandSender} to get for
     * @return the {@link ServerParticipator} or null if not found
     */
    public ServerParticipator getParticipator(CommandSender sender) {
        Preconditions.checkNotNull(sender, "CommandSender cannot be null");
        if (sender instanceof ConsoleCommandSender) {
            return console;
        } else if (sender instanceof Player) {
            return participators.get(((Player) sender).getUniqueId());
        } else {
            return null;
        }
    }

    /**
     * Gets a {@link ServerParticipator} from a given {@link UUID}.
     *
     * @param uuid the {@link UUID} to get from
     * @return the returned {@link ServerParticipator}
     */
    public ServerParticipator getParticipator(UUID uuid) {
        Preconditions.checkNotNull(uuid, "Unique ID cannot be null");
        return participators.get(uuid);
    }

    /**
     * Gets a {@link BaseUser} from a given {@link UUID}.
     *
     * @param uuid the {@link UUID} to get for
     * @return the {@link BaseUser} or null
     */
    public BaseUser getUser(UUID uuid) {
        final BaseUser baseUser;
        ServerParticipator participator = getParticipator(uuid);
        if (participator != null && participator instanceof BaseUser) {
            baseUser = (BaseUser) participator;
        } else {
            participators.put(uuid, baseUser = new BaseUser(uuid));
        }

        return baseUser;
    }

    /**
     * Reloads the {@link ServerParticipator} data from storage.
     */
    public void reloadParticipatorData() {
        userConfig = new Config(plugin, "participators");
        Object object = userConfig.get("participators");
        if (object instanceof MemorySection) {
            MemorySection section = (MemorySection) object;
            Set<String> keys = section.getKeys(false);
            this.participators = new HashMap<>(keys.size());
            for (String id : keys) {
                this.participators.put(UUID.fromString(id), (ServerParticipator) userConfig.get("participators." + id));
            }
        } else {
            this.participators = new HashMap<>();
        }
    }

    /**
     * Saves the {@link ServerParticipator} data to storage.
     */
    public void saveParticipatorData() {
        Map<String, ServerParticipator> saveMap = new LinkedHashMap<>(participators.size());
        for (Map.Entry<UUID, ServerParticipator> entry : participators.entrySet()) {
            saveMap.put(entry.getKey().toString(), entry.getValue());
        }

        userConfig.set("participators", saveMap);
        userConfig.save();
    }
}
