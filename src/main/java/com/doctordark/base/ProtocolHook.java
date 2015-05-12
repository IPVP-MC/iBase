package com.doctordark.base;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.doctordark.base.user.BaseUser;
import com.doctordark.base.user.UserManager;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ProtocolHook {

    public static void hook(BasePlugin plugin) {
        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();

        // Packet listener to disable enchantment glint.
        UserManager userManager = plugin.getUserManager();
        protocolManager.addPacketListener(new PacketAdapter(plugin, PacketType.Play.Server.ENTITY_EQUIPMENT) {
            @Override
            public void onPacketSending(PacketEvent event) {
                Player player = event.getPlayer();
                BaseUser baseUser = userManager.getUser(player.getUniqueId());
                if (!baseUser.isGlintEnabled()) {
                    PacketContainer packet = event.getPacket().deepClone();

                    // Clear the Enchants from the item for this player.
                    ItemStack stack = packet.getItemModifier().read(0);
                    for (Enchantment enchantment : stack.getEnchantments().keySet()) {
                        stack.removeEnchantment(enchantment);
                    }

                    event.setPacket(packet);
                }
            }
        });
    }
}
