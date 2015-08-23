package com.doctordark.util.chat;

import net.minecraft.server.v1_7_R4.ChatComponentText;
import net.minecraft.server.v1_7_R4.ChatHoverable;
import net.minecraft.server.v1_7_R4.ChatModifier;
import net.minecraft.server.v1_7_R4.EntityPlayer;
import net.minecraft.server.v1_7_R4.EnumChatFormat;
import net.minecraft.server.v1_7_R4.IChatBaseComponent;
import net.minecraft.server.v1_7_R4.NBTTagCompound;
import net.minecraft.server.v1_7_R4.PacketPlayOutChat;
import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

/**
 * Deprecated as the BungeeCord Chat API should be used instead
 */
@Deprecated
public class ChatUtil {

    /**
     * Gets the name of an NMS {@link net.minecraft.server.v1_7_R4.ItemStack}.
     *
     * @param stack the {@link net.minecraft.server.v1_7_R4.ItemStack} to get for
     * @return the printable name used by Minecraft
     */
    public static String getName(net.minecraft.server.v1_7_R4.ItemStack stack) {
        if ((stack.tag != null) && (stack.tag.hasKeyOfType("display", 10))) {
            NBTTagCompound nbttagcompound = stack.tag.getCompound("display");
            if (nbttagcompound.hasKeyOfType("Name", 8)) {
                return nbttagcompound.getString("Name");
            }
        }

        return stack.getItem().a(stack) + ".name";
    }

    public static Trans localFromItem(ItemStack stack) {
        if (stack.getType() == Material.POTION && stack.getData().getData() == (short) 0) {
            Potion potion = Potion.fromItemStack(stack);
            // Cleaner potion names.
            if (potion != null) {
                PotionType type = potion.getType();
                // Water bottles, etc, don't have a type.
                if (type != null && type != PotionType.WATER) {
                    String effectName = (potion.isSplash() ? "Splash " : "") + WordUtils.capitalizeFully(type.name().replace('_', ' ')) + " L" + potion.getLevel();
                    return ChatUtil.fromItemStack(stack).append(" of " + effectName);
                }
            }
        }

        return fromItemStack(stack);
    }

    public static Trans fromItemStack(ItemStack stack) {
        net.minecraft.server.v1_7_R4.ItemStack nms = CraftItemStack.asNMSCopy(stack);
        NBTTagCompound tag = new NBTTagCompound();
        nms.save(tag);
        return new Trans(getName(nms)).setColor(ChatColor.getByChar(nms.w().e.getChar())).setHover(HoverAction.SHOW_ITEM, new ChatComponentText(tag.toString()));
    }

    public static void reset(IChatBaseComponent text) {
        ChatModifier modifier = text.getChatModifier();
        modifier.a((ChatHoverable) null);
        modifier.setChatClickable(null);
        modifier.setBold(false);
        modifier.setColor(EnumChatFormat.RESET);
        modifier.setItalic(false);
        modifier.setRandom(false);
        modifier.setStrikethrough(false);
        modifier.setUnderline(false);
    }

    public static void send(CommandSender sender, IChatBaseComponent text) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            PacketPlayOutChat packet = new PacketPlayOutChat(text, true);

            EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
            entityPlayer.playerConnection.sendPacket(packet);
        } else {
            sender.sendMessage(text.c());
        }
    }
}