package com.doctordark.base.util.chatlib;

import net.minecraft.server.v1_8_R1.ChatModifier;
import net.minecraft.server.v1_8_R1.ChatComponentText;
import net.minecraft.server.v1_8_R1.ChatHoverable;
import net.minecraft.server.v1_8_R1.IChatBaseComponent;
import net.minecraft.server.v1_8_R1.NBTTagCompound;
import net.minecraft.server.v1_8_R1.PacketPlayOutChat;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Util {

    public static String getName(net.minecraft.server.v1_8_R1.ItemStack stack) {
        NBTTagCompound tag = stack.getTag();

        if ((tag != null) && (tag.hasKeyOfType("display", 10))) {
            NBTTagCompound nbttagcompound = tag.getCompound("display");

            if (nbttagcompound.hasKeyOfType("Name", 8)) {
                return nbttagcompound.getString("Name");
            }
        }

        return stack.getItem().a(stack) + ".name";
    }

    /**
     * Creates a new chat base component object from a Bukkit ItemStack.
     *
     * @param stack the stack to create from
     * @return the created chat base component
     */
    public static IChatBaseComponent fromItemStack(ItemStack stack) {
        net.minecraft.server.v1_8_R1.ItemStack nms = CraftItemStack.asNMSCopy(stack);
        NBTTagCompound tag = new NBTTagCompound();
        nms.save(tag);

        ChatComponentText text = new ChatComponentText(getName(nms));
        ChatModifier modifier = text.getChatModifier();
        modifier.setColor(nms.u().e);
        modifier.setChatHoverable(new ChatHoverable(HoverAction.SHOW_ITEM.getNMS(), new ChatComponentText(tag.toString())));
        return text;
    }

    protected static void send(CommandSender sender, IChatBaseComponent text, ChatPosition position) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            PacketPlayOutChat packet = new PacketPlayOutChat(text, position.getId());
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
        } else {
            sender.sendMessage(text.getText());
        }
    }
}
