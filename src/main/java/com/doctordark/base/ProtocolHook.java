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
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ProtocolHook {

    private static final ItemStack AIR = new ItemStack(Material.AIR, 1);

    public static void hook(BasePlugin basePlugin) {
        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
        UserManager userManager = basePlugin.getUserManager();

        // Packet listener to disable enchantment glint for entity equipment.
        protocolManager.addPacketListener(new PacketAdapter(basePlugin, PacketType.Play.Server.ENTITY_EQUIPMENT) {
            @Override
            public void onPacketSending(PacketEvent event) {
                if (!basePlugin.getServerHandler().useProtocolLib) {
                    return;
                }

                Player player = event.getPlayer();
                BaseUser baseUser = userManager.getUser(player.getUniqueId());
                if (!baseUser.isGlintEnabled()) {
                    PacketContainer packet = event.getPacket();
                    ItemStack stack = packet.getItemModifier().read(0);
                    if (stack != null && stack.getType() != Material.AIR) {
                        convert(stack);
                    }
                }
            }
        });

        // Packet listener to disable enchantment glint for ground items.
        protocolManager.addPacketListener(new PacketAdapter(basePlugin, PacketType.Play.Server.ENTITY_METADATA) {
            @Override
            public void onPacketSending(PacketEvent event) {
                if (!basePlugin.getServerHandler().useProtocolLib) {
                    return;
                }

                Player player = event.getPlayer();
                BaseUser baseUser = userManager.getUser(player.getUniqueId());
                if (!baseUser.isGlintEnabled()) {
                    PacketContainer packet = event.getPacket();

                    // See if we are modifying an item stack
                    if (packet.getEntityModifier(event).read(0) instanceof Item) {
                        WrappedDataWatcher watcher = new WrappedDataWatcher(packet.getWatchableCollectionModifier().read(0));
                        ItemStack stack = watcher.getItemStack(10).clone();
                        if (stack != null && stack.getType() != Material.AIR) {
                            convert(stack);
                        }
                    }
                }
            }
        });

        // Packet listener to disable enchantment glint for own items.
        /*protocolManager.addPacketListener(new PacketAdapter(plugin, PacketType.Play.Server.SET_SLOT, PacketType.Play.Server.WINDOW_ITEMS) {
            @Override
            public void onPacketSending(PacketEvent event) {
                if (!basePlugin.getServerHandler().useProtocolLib) {
                    return;
                }

                Player player = event.getPlayer();
                BaseUser baseUser = userManager.getUser(player.getUniqueId());
                if (!baseUser.isGlintEnabled()) {
                    PacketContainer packet = event.getPacket();
                    if (packet.getType() == PacketType.Play.Server.SET_SLOT) {
                        convert(packet.getItemModifier().read(0));
                    } else {
                        ItemStack[] elements = packet.getItemArrayModifier().read(0);
                        for (ItemStack next : elements) {
                            if (next != null && next.getType() != Material.AIR) {
                                convert(next);
                            }
                        }
                    }
                }
            }
        });*/
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
