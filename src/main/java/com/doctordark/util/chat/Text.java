package com.doctordark.util.chat;

import net.minecraft.server.v1_7_R4.ChatClickable;
import net.minecraft.server.v1_7_R4.ChatComponentText;
import net.minecraft.server.v1_7_R4.ChatHoverable;
import net.minecraft.server.v1_7_R4.EnumChatFormat;
import net.minecraft.server.v1_7_R4.IChatBaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Text extends ChatComponentText {

    public Text() {
        super("");
    }

    public Text(String string) {
        super(string);
    }

    public Text append(String text) {
        return (Text) a(text);
    }

    public Text append(IChatBaseComponent node) {
        return (Text) addSibling(node);
    }

    public Text append(IChatBaseComponent... nodes) {
        for (IChatBaseComponent node : nodes) {
            addSibling(node);
        }
        return this;
    }

    public static Trans fromItemStack(ItemStack stack) {
        return ChatUtil.fromItemStack(stack);
    }

    public Text appendItem(ItemStack stack) {
        return append(ChatUtil.fromItemStack(stack));
    }

    public Text setBold(boolean bold) {
        getChatModifier().setBold(bold);
        return this;
    }

    public Text setItalic(boolean italic) {
        getChatModifier().setItalic(italic);
        return this;
    }

    public Text setUnderline(boolean underline) {
        getChatModifier().setUnderline(underline);
        return this;
    }

    public Text setRandom(boolean random) {
        getChatModifier().setRandom(random);
        return this;
    }

    public Text setStrikethrough(boolean strikethrough) {
        getChatModifier().setStrikethrough(strikethrough);
        return this;
    }

    public Text setColor(ChatColor color) {
        getChatModifier().setColor(EnumChatFormat.valueOf(color.name()));
        return this;
    }

    public Text setClick(ClickAction action, String value) {
        this.getChatModifier().setChatClickable(new ChatClickable(action.getNMS(), value));
        return this;
    }

    public Text setHover(HoverAction action, IChatBaseComponent value) {
        getChatModifier().a(new ChatHoverable(action.getNMS(), value));
        return this;
    }

    public Text setHoverText(String text) {
        return setHover(HoverAction.SHOW_TEXT, new Text(text));
    }

    @Override
    public IChatBaseComponent f() {
        return h();
    }

    public void send(CommandSender sender) {
        ChatUtil.send(sender, this);
    }

    public void broadcast() {
        broadcast(null);
    }

    public void broadcast(String permission) {
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            if (permission == null || player.hasPermission(permission)) {
                send(player);
            }
        }

        send(Bukkit.getServer().getConsoleSender());
    }
}