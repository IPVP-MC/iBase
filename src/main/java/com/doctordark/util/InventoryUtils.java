package com.doctordark.util;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public final class InventoryUtils {

    private InventoryUtils() {
    }

    /**
     * Safely removes an ItemStack with a specific quantity
     * from an inventory ignoring any ItemMeta.
     *
     * @param inventory the inventory to remove for
     * @param stack     the stack to be removed
     * @param quantity  the amount to be removed
     */
    public static void removeItem(Inventory inventory, ItemStack stack, int quantity) {
        Material material = stack.getType();
        ItemStack[] contents = inventory.getContents();

        short durability = stack.getDurability();
        boolean compareDamage = material.getMaxDurability() == 0;
        for (int i = 0; i < quantity; i++) {
            for (ItemStack item : contents) {
                if (item == null || item.getType() != material) continue;
                if (compareDamage && item.getDurability() != durability) continue;

                int amount = item.getAmount();
                if (amount == 1) {
                    int first = inventory.first(item);
                    if (inventory.first(item) != -1) {
                        inventory.clear(first);
                    }
                } else {
                    item.setAmount(amount - 1);
                }

                break;
            }
        }
    }

    public static int countAmount(Inventory inventory, ItemStack stack) {
        int count = 0;
        for (ItemStack item : inventory.getContents()) {
            if (item != null && item.getDurability() == stack.getDurability() && item.getType() == stack.getType()) {
                count += item.getAmount();
            }
        }

        return count;
    }

    /**
     * Counts how much of an item an inventory contains.
     *
     * @param inventory the inventory to count for
     * @param material  the material to count for
     * @param data      the data value to count for
     * @return amount of the item inventory contains
     */
    public static int countAmount(Inventory inventory, Material material, int data) {
        ItemStack[] contents = inventory.getContents();

        short durability = (short) data;
        boolean compareDamage = material.getMaxDurability() == 0;

        int counter = 0;
        for (ItemStack item : contents) {
            if (item == null) continue;
            if (item.getType() != material) continue;
            if (compareDamage && (item.getDurability() != durability)) continue;

            counter += item.getAmount();
        }

        return counter;
    }

    public static boolean isEmpty(Inventory inventory) {
        boolean result = true;
        for (ItemStack stack : inventory.getContents()) {
            if (stack != null && stack.getType() != Material.AIR) {
                result = false;
                break;
            }
        }

        return result;
    }

    public static boolean clickedTopInventory(InventoryDragEvent event) {
        InventoryView view = event.getView();
        if (view.getTopInventory() == null) {
            return false;
        }

        boolean result = false;
        for (Map.Entry<Integer, ItemStack> entry : event.getNewItems().entrySet()) {
            if (entry.getKey() < event.getView().getTopInventory().getSize()) {
                result = true;
                break;
            }
        }

        return result;
    }
}
