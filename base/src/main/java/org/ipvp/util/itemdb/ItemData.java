package org.ipvp.util.itemdb;

import org.ipvp.base.BasePlugin;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import java.util.LinkedHashMap;
import java.util.Map;

public class ItemData implements ConfigurationSerializable {

    private final Material material;
    private final short itemData;

    public ItemData(final MaterialData data) {
        this(data.getItemType(), data.getData());
    }

    public ItemData(final ItemStack stack) {
        this(stack.getType(), stack.getData().getData());
    }

    /**
     * Constructs a new {@link ItemData} from given magic values.
     *
     * @param material the {@link Material} for data
     * @param itemData the data to use
     * @deprecated magic number
     */
    @Deprecated
    public ItemData(final Material material, final short itemData) {
        this.material = material;
        this.itemData = itemData;
    }

    public ItemData(Map<String, Object> map) {
        Object object = map.get("itemType");
        if (object instanceof String) {
            this.material = Material.getMaterial((String) object);
        } else throw new AssertionError("Incorrectly configurised");

        if ((object = map.get("itemData")) instanceof Short) {
            this.itemData = (Short) object;
        } else throw new AssertionError("Incorrectly configurised");
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("itemType", material.name());
        map.put("itemData", itemData);
        return map;
    }

    public Material getMaterial() {
        return material;
    }

    @Deprecated
    public short getItemData() {
        return itemData;
    }

    public String getItemName() {
        return BasePlugin.getPlugin().getItemDb().getName(new ItemStack(material, 1, itemData));
    }

    public static ItemData fromItemName(String string) {
        ItemStack stack = BasePlugin.getPlugin().getItemDb().getItem(string);
        return new ItemData(stack.getType(), stack.getData().getData());
    }

    public static ItemData fromStringValue(String value) {
        int firstBracketIndex = value.indexOf('(');
        if (firstBracketIndex == -1) return null;

        int otherBracketIndex = value.indexOf(')');
        if (otherBracketIndex == -1) return null;

        String itemName = value.substring(0, firstBracketIndex);
        String itemData = value.substring(firstBracketIndex + 1, otherBracketIndex);

        Material material = Material.getMaterial(itemName);
        return new ItemData(material, Short.parseShort(itemData));
    }

    @Override
    public String toString() {
        return String.valueOf(material.name()) + "(" + String.valueOf(itemData) + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ItemData itemData1 = (ItemData) o;

        if (itemData != itemData1.itemData) return false;
        return material == itemData1.material;
    }

    @Override
    public int hashCode() {
        int result = material != null ? material.hashCode() : 0;
        result = 31 * result + (int) itemData;
        return result;
    }
}
