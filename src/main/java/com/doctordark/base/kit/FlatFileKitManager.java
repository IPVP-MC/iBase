package com.doctordark.base.kit;

import com.doctordark.base.BasePlugin;
import com.doctordark.util.Config;
import com.doctordark.util.GenericUtils;
import net.minecraft.util.org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.ChatPaginator;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Implementation of the {@link KitManager} saving to YAML.
 */
public class FlatFileKitManager implements KitManager {

    private Config config;
    private List<Kit> kits;
    private final BasePlugin plugin;

    public FlatFileKitManager(BasePlugin plugin) {
        this.plugin = plugin;
        this.reloadKitData();
    }

    @Override
    public List<Kit> getKits() {
        return kits;
    }

    @Override
    public boolean containsKit(Kit kit) {
        return kits.contains(kit);
    }

    @Override
    public void createKit(Kit kit) {
        kits.add(kit);
    }

    @Override
    public void removeKit(Kit kit) {
        kits.remove(kit);
    }

    @Override
    public Kit getKit(String id) {
        for (Kit kit : kits) {
            if (kit.getName().equalsIgnoreCase(id)) {
                return kit;
            }
        }

        return null;
    }

    @Override
    public Inventory getGui(Player player) {
        UUID uuid = player.getUniqueId();
        Inventory inventory = Bukkit.createInventory(player, ((kits.size() + 8) / 9 * 9), ChatColor.BLUE + "Kit Selector");
        for (Kit kit : kits) {
            ItemStack stack = kit.getImage();
            String description = kit.getDescription();

            int curUses = plugin.getUserManager().getUser(uuid).getKitUses(kit);
            int maxUses = kit.getMaximumUses();

            List<String> lore = new ArrayList<>();
            if (player.hasPermission(kit.getPermission())) {
                if (!kit.isEnabled()) {
                    lore.add(ChatColor.RED + "Disabled");
                } else if (kit.getDelayMillis() > 0L) {
                    lore.add(ChatColor.YELLOW + DurationFormatUtils.formatDurationWords(kit.getDelayMillis(), true, true) + " cooldown");
                }

                if (maxUses != UNLIMITED_USES) {
                    lore.add(ChatColor.YELLOW + "Used " + curUses + '/' + maxUses + " times.");
                }

                if (description != null) {
                    lore.add(" ");
                    for (String part : ChatPaginator.wordWrap(description, 24)) {
                        lore.add(ChatColor.WHITE + part);
                    }
                }
            } else {
                lore.add(ChatColor.RED + "You do not own this kit.");
            }

            ItemStack cloned = stack.clone();
            ItemMeta meta = cloned.getItemMeta();
            meta.setDisplayName(ChatColor.GREEN + kit.getName());
            meta.setLore(lore);
            cloned.setItemMeta(meta);
            inventory.addItem(cloned);
        }

        return inventory;
    }

    @Override
    public void reloadKitData() {
        this.config = new Config(plugin, "kits");

        // Load the kits.
        Object object = config.get("kits");
        if (!(object instanceof List)) {
            kits = new ArrayList<>();
        } else {
            kits = GenericUtils.createList(object, Kit.class);
        }
    }

    @Override
    public void saveKitData() {
        config.set("kits", kits);
        config.save();
    }
}
