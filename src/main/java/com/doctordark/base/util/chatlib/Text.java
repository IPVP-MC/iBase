package com.doctordark.base.util.chatlib;

import net.minecraft.server.v1_8_R1.ChatClickable;
import net.minecraft.server.v1_8_R1.ChatComponentText;
import net.minecraft.server.v1_8_R1.ChatHoverable;
import net.minecraft.server.v1_8_R1.EnumChatFormat;
import net.minecraft.server.v1_8_R1.IChatBaseComponent;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

public class Text extends ChatComponentText {

    public Text() {
        super("");
    }

    public Text(String string) {
        super(string);
    }

    public static Text fromItemStack(ItemStack stack) {
        return new Text().append(Util.fromItemStack(stack));
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

    public Text appendItem(ItemStack stack) {
        return append(Util.fromItemStack(stack));
    }

    public boolean isBold() {
        return getChatModifier().isBold();
    }

    public Text setBold(boolean bold) {
        getChatModifier().setBold(bold);
        return this;
    }

    public boolean isItalic() {
        return getChatModifier().isBold();
    }

    public Text setItalic(boolean italic) {
        getChatModifier().setItalic(italic);
        return this;
    }

    public boolean isUnderlined() {
        return getChatModifier().isUnderlined();
    }

    public Text setUnderline(boolean underline) {
        getChatModifier().setUnderline(underline);
        return this;
    }

    public boolean isRandom() {
        return getChatModifier().isRandom();
    }

    public Text setRandom(boolean random) {
        getChatModifier().setRandom(random);
        return this;
    }

    public boolean isStrikethrough() {
        return getChatModifier().isStrikethrough();
    }

    public Text setStrikethrough(boolean strikethrough) {
        getChatModifier().setStrikethrough(strikethrough);
        return this;
    }

    public ChatColor getColor() {
        return ChatColor.valueOf(getChatModifier().getColor().name());
    }

    public Text setColor(ChatColor color) {
        getChatModifier().setColor(EnumChatFormat.valueOf(color.name()));
        return this;
    }

    public String getShiftClickText() {
        return getChatModifier().j();
    }

    public Text setShiftClickText(String text) {
        getChatModifier().setInsertion(text);
        return this;
    }

    public ChatClickable getChatClickable() {
        return getChatModifier().h();
    }

    public Text setClick(ClickAction action, String value) {
        getChatModifier().setChatClickable(new ChatClickable(action.getNMS(), value));
        return this;
    }

    public ChatHoverable getChatHoverable() {
        return getChatModifier().i();
    }

    public Text setHover(HoverAction action, IChatBaseComponent value) {
        getChatModifier().setChatHoverable(new ChatHoverable(action.getNMS(), value));
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
        send(sender, ChatPosition.CHAT);
    }

    public void send(CommandSender sender, ChatPosition position) {
        Util.send(sender, this, position);
    }
}
