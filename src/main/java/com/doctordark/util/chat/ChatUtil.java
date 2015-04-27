package com.doctordark.util.chat;

import net.minecraft.server.v1_7_R4.ChatComponentText;
import net.minecraft.server.v1_7_R4.EntityPlayer;
import net.minecraft.server.v1_7_R4.IChatBaseComponent;
import net.minecraft.server.v1_7_R4.NBTTagCompound;
import net.minecraft.server.v1_7_R4.PacketPlayOutChat;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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

    public static Trans fromItemStack(ItemStack stack) {
        net.minecraft.server.v1_7_R4.ItemStack nms = CraftItemStack.asNMSCopy(stack);
        NBTTagCompound tag = new NBTTagCompound();
        nms.save(tag);
        return new Trans(getName(nms)).setColor(ChatColor.getByChar(nms.w().e.getChar())).setHover(HoverAction.SHOW_ITEM, new ChatComponentText(tag.toString()));
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