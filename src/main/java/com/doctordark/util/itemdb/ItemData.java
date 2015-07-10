package com.doctordark.util.itemdb;

import com.doctordark.base.BasePlugin;
import org.bukkit.inventory.ItemStack;

public class ItemData {

    private final int itemNo;
    private final short itemData;

    public ItemData(final int itemNo, final short itemData) {
        this.itemNo = itemNo;
        this.itemData = itemData;
    }

    public int getItemNo() {
        return itemNo;
    }

    public short getItemData() {
        return itemData;
    }

    public static ItemData fromItemName(String string) {
        ItemStack stack = BasePlugin.getPlugin().getItemDb().getItem(string);
        return new ItemData(stack.getTypeId(), stack.getData().getData());
    }

    public String getItemName() {
        return BasePlugin.getPlugin().getItemDb().getName(new ItemStack(itemNo, 1, itemData));
    }

    @Override
    public int hashCode() {
        return (31 * itemNo) ^ itemData;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }

        if (!(object instanceof ItemData)) {
            return false;
        }

        ItemData pair = (ItemData) object;
        return this.itemNo == pair.itemNo && this.itemData == pair.itemData;
    }
}
