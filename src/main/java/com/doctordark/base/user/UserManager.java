package com.doctordark.base.user;

import com.doctordark.base.BasePlugin;
import com.doctordark.util.Config;
import com.google.common.collect.Maps;
import org.apache.commons.lang.Validate;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.MemorySection;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public class UserManager {

    private final ConsoleUser console;
    private final Map<UUID, ServerParticipator> participators;
    private final Config userConfig;

    public UserManager(BasePlugin plugin) {
        this.participators = Maps.newHashMap();
        this.userConfig = new Config(plugin, "participators");
        this.reloadParticipatorData();

        // Load the ConsoleUser data here.
        if (this.participators.containsKey(ConsoleUser.CONSOLE_UUID)) {
            this.console = (ConsoleUser) this.participators.get(ConsoleUser.CONSOLE_UUID);
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
        Validate.notNull(sender, "CommandSender cannot be null");
        if (sender instanceof ConsoleCommandSender) {
            return console;
        } else if (sender instanceof Player) {
            Player player = (Player) sender;
            return participators.get(player.getUniqueId());
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
        Validate.notNull(uuid, "Unique ID cannot be null");
        return participators.get(uuid);
    }

    /**
     * Gets a {@link BaseUser} from a given {@link UUID}.
     *
     * @param uuid the {@link UUID} to get for
     * @return the {@link BaseUser} or null
     */
    public BaseUser getUser(UUID uuid) {
        ServerParticipator participator = getParticipator(uuid);
        if (participator != null && participator instanceof BaseUser) {
            return (BaseUser) participator;
        } else {
            final BaseUser baseUser;
            participators.put(uuid, baseUser = new BaseUser(uuid));
            return baseUser;
        }
    }

    /**
     * Reloads the {@link ServerParticipator} data from storage.
     */
    public void reloadParticipatorData() {
        this.participators.clear();

        Object object = this.userConfig.get("participators");
        if (object != null && object instanceof MemorySection) {
            MemorySection section = (MemorySection) object;
            for (String id : section.getKeys(false)) {
                ServerParticipator participator = (ServerParticipator) userConfig.get("participators." + id);
                this.participators.put(UUID.fromString(id), participator);
            }
        }
    }

    /**
     * Saves the {@link ServerParticipator} data to storage.
     */
    public void saveParticipatorData() {
        for (Map.Entry<UUID, ServerParticipator> entry : participators.entrySet()) {
            String id = entry.getValue().getUniqueId().toString();
            userConfig.set("participators." + id, entry.getValue());
        }

        userConfig.save();
    }
}
