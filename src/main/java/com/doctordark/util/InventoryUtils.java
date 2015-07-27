package com.doctordark.util;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Map;
import java.util.Set;

public final class InventoryUtils {

    private InventoryUtils() {
    }

    /**
     * Safely removes an ItemStack with a specific quantity
     * from an inventory ignoring any ItemMeta.
     *
     * @param inventory the inventory to remove for
     * @param type      the material to remove
     * @param data      the data value to remove
     * @param quantity  the amount to be removed
     */
    public static void removeItem(Inventory inventory, Material type, short data, int quantity) {
        ItemStack[] contents = inventory.getContents();
        boolean compareDamage = type.getMaxDurability() == 0;

        for (int i = quantity; i > 0; i--) {
            for (ItemStack content : contents) {
                if (content == null || content.getType() != type) continue;
                if (compareDamage && content.getData().getData() != data) continue;

                if (content.getAmount() <= 1) {
                    inventory.removeItem(content);
                } else {
                    content.setAmount(content.getAmount() - 1);
                }
                break;
            }
        }
    }

    /**
     * Counts how much of an item an inventory contains.
     *
     * @param inventory the inventory to count for
     * @param type      the material to count for
     * @param data      the data value to count for
     * @return amount of the item inventory contains
     */
    public static int countAmount(Inventory inventory, Material type, short data) {
        ItemStack[] contents = inventory.getContents();
        boolean compareDamage = type.getMaxDurability() == 0;

        int counter = 0;
        for (ItemStack item : contents) {
            if (item == null || item.getType() != type) continue;
            if (compareDamage && item.getData().getData() != data) continue;

            counter += item.getAmount();
        }

        return counter;
    }

    public static boolean isEmpty(Inventory inventory) {
        return isEmpty(inventory, false);
    }

    public static boolean isEmpty(Inventory inventory, boolean checkArmour) {
        boolean result = true;
        ItemStack[] contents = inventory.getContents();
        for (ItemStack content : contents) {
            if (content != null && content.getType() != Material.AIR) {
                result = false;
                break;
            }
        }

        if (result) return true;
        if (inventory instanceof PlayerInventory) {
            contents = ((PlayerInventory) inventory).getContents();
            for (ItemStack content : contents) {
                if (content != null && content.getType() != Material.AIR) {
                    result = false;
                    break;
                }
            }
        }

        return result;
    }

    public static boolean clickedTopInventory(InventoryDragEvent event) {
        InventoryView view = event.getView();
        Inventory topInventory = view.getTopInventory();
        if (topInventory == null) {
            return false;
        }

        boolean result = false;
        Set<Map.Entry<Integer, ItemStack>> entrySet = event.getNewItems().entrySet();
        int size = topInventory.getSize();
        for (Map.Entry<Integer, ItemStack> entry : entrySet) {
            if (entry.getKey() < size) {
                result = true;
                break;
            }
        }

        return result;
    }
}
