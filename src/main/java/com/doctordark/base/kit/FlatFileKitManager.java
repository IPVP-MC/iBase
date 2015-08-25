package com.doctordark.base.kit;

import com.doctordark.base.BasePlugin;
import com.doctordark.base.kit.event.KitRenameEvent;
import com.doctordark.util.Config;
import com.doctordark.util.GenericUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.ChatPaginator;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Implementation of the {@link KitManager} saving to YAML.
 */
public class FlatFileKitManager implements KitManager, Listener {

    private Config config;

    private final Map<String, Kit> kitNameMap = Maps.newTreeMap(String.CASE_INSENSITIVE_ORDER);
    private final Map<UUID, Kit> kitUUIDMap = Maps.newHashMap();

    private List<Kit> kits;
    private final BasePlugin plugin;

    public FlatFileKitManager(BasePlugin plugin) {
        this.plugin = plugin;
        this.reloadKitData();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onKitRename(KitRenameEvent event) {
        this.kitNameMap.remove(event.getOldName());
        this.kitNameMap.put(event.getNewName(), event.getKit());
    }

    @Override
    public List<Kit> getKits() {
        return this.kits;
    }

    @Override
    public Kit getKit(UUID uuid) {
        return this.kitUUIDMap.get(uuid);
    }

    @Override
    public Kit getKit(String id) {
        return this.kitNameMap.get(id);
    }

    @Override
    public boolean containsKit(Kit kit) {
        return this.kits.contains(kit);
    }

    @Override
    public void createKit(Kit kit) {
        if (this.kits.add(kit)) {
            this.kitNameMap.put(kit.getName(), kit);
            this.kitUUIDMap.put(kit.getUniqueID(), kit);
        }
    }

    @Override
    public void removeKit(Kit kit) {
        if (this.kits.remove(kit)) {
            this.kitNameMap.remove(kit.getName());
            this.kitUUIDMap.remove(kit.getUniqueID());
        }
    }

    private static final int INV_WIDTH = 9;

    @Override
    public Inventory getGui(Player player) {
        UUID uuid = player.getUniqueId();
        Inventory inventory = Bukkit.createInventory(player, (kits.size() + INV_WIDTH - 1) / INV_WIDTH * INV_WIDTH, ChatColor.BLUE + "Kit Selector");
        for (Kit kit : kits) {
            ItemStack stack = kit.getImage();
            String description = kit.getDescription();

            final List<String> lore;
            String kitPermission = kit.getPermissionNode();
            if (kitPermission == null || player.hasPermission(kitPermission)) {
                lore = Lists.newArrayList();
                lore.add(kit.isEnabled() ? ChatColor.YELLOW + kit.getDelayWords() + " cooldown" : ChatColor.RED + "Disabled");
                int maxUses = kit.getMaximumUses();
                if (maxUses != UNLIMITED_USES) {
                    lore.add(ChatColor.YELLOW + "Used " + plugin.getUserManager().getUser(uuid).getKitUses(kit) + '/' + maxUses + " times.");
                }

                if (description != null) {
                    lore.add(" ");
                    for (String part : ChatPaginator.wordWrap(description, 24)) {
                        lore.add(ChatColor.WHITE + part);
                    }
                }
            } else lore = Lists.newArrayList(ChatColor.RED + "You do not own this kit.");

            ItemStack cloned = stack.clone(); //TODO: cloning necessary?
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
        if (object instanceof List) {
            this.kits = GenericUtils.createList(object, Kit.class);
        } else this.kits = Lists.newArrayList();

        for (Kit kit : this.kits) {
            this.kitNameMap.put(kit.getName(), kit);
            this.kitUUIDMap.put(kit.getUniqueID(), kit);
        }
    }

    @Override
    public void saveKitData() {
        this.config.set("kits", this.kits);
        this.config.save();
    }
}
