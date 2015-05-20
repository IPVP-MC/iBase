package com.doctordark.base;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.doctordark.base.user.BaseUser;
import com.doctordark.base.user.UserManager;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ProtocolHook {

    private static final ItemStack AIR = new ItemStack(Material.AIR, 1);

    public static void hook(BasePlugin plugin) {
        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
        UserManager userManager = plugin.getUserManager();

        // Packet listener to disable enchantment glint for entity equipment.
        protocolManager.addPacketListener(new PacketAdapter(plugin, PacketType.Play.Server.ENTITY_EQUIPMENT) {
            @Override
            public void onPacketSending(PacketEvent event) {
                Player player = event.getPlayer();
                BaseUser baseUser = userManager.getUser(player.getUniqueId());
                if (!baseUser.isGlintEnabled()) {
                    PacketContainer packet = event.getPacket().deepClone();

                    // Clear the Enchants from the item for this player.
                    ItemStack stack = packet.getItemModifier().read(0);
                    if (stack != null && stack.getType() != Material.AIR) {
                        convert(stack);
                    }
                }
            }
        });

        // Packet listener to disable enchantment glint for ground items.
        protocolManager.addPacketListener(new PacketAdapter(plugin, PacketType.Play.Server.ENTITY_METADATA) {
            @Override
            public void onPacketSending(PacketEvent event) {
                PacketContainer packet = event.getPacket();

                // See if we are modifying an item stack
                if (packet.getEntityModifier(event).read(0) instanceof Item) {
                    WrappedDataWatcher watcher = new WrappedDataWatcher(packet.getWatchableCollectionModifier().read(0));
                    ItemStack stack = watcher.getItemStack(10);
                    if (stack != null && stack.getType() != Material.AIR) {
                        Player player = event.getPlayer();
                        BaseUser baseUser = userManager.getUser(player.getUniqueId());
                        if (!baseUser.isGlintEnabled()) {
                            convert(stack);
                        }
                    }
                }
            }
        });
    }

    private static ItemStack convert(ItemStack origin) {
        if (origin == null || origin.getType() == Material.AIR) {
            return origin;
        }

        switch (origin.getType()) {
            case POTION:
            case GOLDEN_APPLE:
                if (origin.getDurability() > 0) {
                    origin.setDurability((short) 0);
                }
                break;
            case ENCHANTED_BOOK:
                origin.setType(Material.BOOK);
                break;
            default:
                origin.getEnchantments().keySet().forEach(origin::removeEnchantment);
                break;
        }

        return origin;
    }
}
