package com.doctordark.util.itemdb;

import net.minecraft.util.com.google.common.primitives.Ints;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Essentials project
 */
public class ItemDb {

    private final transient Map<String, Integer> items = new HashMap<>();
    private final transient Map<ItemData, List<String>> names = new HashMap<>();
    private final transient Map<ItemData, String> primaryName = new HashMap<>();
    private final transient Map<String, Short> durabilities = new HashMap<>();
    private final transient ManagedFile file;
    private final transient Pattern splitPattern = Pattern.compile("((.*)[:+',;.](\\d+))");

    public ItemDb(final JavaPlugin plugin) {
        this.file = new ManagedFile("items.csv", plugin);
        this.reloadConfig();
    }

    public void reloadConfig() {
        durabilities.clear();
        items.clear();
        names.clear();
        primaryName.clear();

        final List<String> lines = file.getLines();
        if (lines.isEmpty()) {
            return;
        }

        for (String line : lines) {
            line = line.trim().toLowerCase(Locale.ENGLISH);
            if (line.length() > 0 && line.charAt(0) == '#') {
                continue;
            }

            final String[] parts = line.split("[^a-z0-9]");
            if (parts.length < 2) {
                continue;
            }

            final int numeric = Integer.parseInt(parts[1]);
            final short data = parts.length > 2 && !parts[2].equals("0") ? Short.parseShort(parts[2]) : 0;
            String itemName = parts[0].toLowerCase(Locale.ENGLISH);

            durabilities.put(itemName, data);
            items.put(itemName, numeric);

            ItemData itemData = new ItemData(numeric, data);
            if (names.containsKey(itemData)) {
                List<String> nameList = names.get(itemData);
                nameList.add(itemName);
                Collections.sort(nameList, new LengthCompare());
            } else {
                List<String> nameList = new ArrayList<>();
                nameList.add(itemName);
                names.put(itemData, nameList);
                primaryName.put(itemData, itemName);
            }
        }
    }

    public ItemStack get(final String id) {
        int itemId = 0;
        String itemName;
        short metaData = 0;
        Matcher parts = splitPattern.matcher(id);

        if (parts.matches()) {
            itemName = parts.group(2);
            metaData = Short.parseShort(parts.group(3));
        } else {
            itemName = id;
        }

        Integer last;
        if ((last = Ints.tryParse(itemName)) != null) {
            itemId = last;
        } else if ((last = (Ints.tryParse(id))) != null) {
            itemId = last;
        } else {
            itemName = itemName.toLowerCase(Locale.ENGLISH);
        }

        if (itemId < 1) {
            if (items.containsKey(itemName)) {
                itemId = items.get(itemName);
                if (durabilities.containsKey(itemName) && metaData == 0) {
                    metaData = durabilities.get(itemName);
                }
            } else if (Material.getMaterial(itemName.toUpperCase(Locale.ENGLISH)) != null) {
                Material bMaterial = Material.getMaterial(itemName.toUpperCase(Locale.ENGLISH));
                itemId = bMaterial.getId();
            } else {
                try {
                    itemId = Bukkit.getUnsafe().getMaterialFromInternalName(itemName.toLowerCase(Locale.ENGLISH)).getId();
                } catch (Exception ex) {
                    return null;
                }
            }
        }

        if (itemId < 1) {
            return null;
        }

        final Material mat = Material.getMaterial(itemId);

        if (mat == null) {
            return null;
        }

        final ItemStack result = new ItemStack(mat);
        result.setAmount(mat.getMaxStackSize());
        result.setDurability(metaData);
        return result;
    }

    public ItemStack get(String id, int quantity) {
        final ItemStack result = get(id.toLowerCase(Locale.ENGLISH));
        if (result == null) {
            return null;
        }

        result.setAmount(quantity);
        return result;
    }

    public List<ItemStack> getMatching(Player player, String[] args) {
        List<ItemStack> items = new ArrayList<>();
        PlayerInventory inventory = player.getInventory();

        if (args.length < 1) {
            items.add(player.getItemInHand());
        } else if (args[0].equalsIgnoreCase("hand")) {
            items.add(player.getItemInHand());
        } else if (args[0].equalsIgnoreCase("inventory") || args[0].equalsIgnoreCase("invent") || args[0].equalsIgnoreCase("all")) {
            for (ItemStack stack : inventory.getContents()) {
                if (stack != null && stack.getType() != Material.AIR) {
                    items.add(stack);
                }
            }
        } else if (args[0].equalsIgnoreCase("blocks")) {
            for (ItemStack stack : inventory.getContents()) {
                if (stack != null && stack.getType() != Material.AIR && stack.getType().isBlock()) {
                    items.add(stack);
                }
            }
        } else {
            items.add(get(args[0]));
        }

        if (items.isEmpty()) {
            return new ArrayList<>();
        }

        ItemStack first = items.get(0);
        if (first == null || first.getType() == Material.AIR) {
            return new ArrayList<>();
        }

        return items;
    }

    public String names(ItemStack item) {
        ItemData itemData = new ItemData(item.getTypeId(), item.getDurability());
        List<String> nameList = names.get(itemData);
        if (nameList == null) {
            itemData = new ItemData(item.getTypeId(), (short) 0);
            nameList = names.get(itemData);
            if (nameList == null) {
                return null;
            }
        }

        if (nameList.size() > 15) {
            nameList = nameList.subList(0, 14);
        }

        return StringUtils.join(nameList, ", ");
    }

    public String name(ItemStack item) {
        return CraftItemStack.asNMSCopy(item).getName();
    }

    @Deprecated
    public String oldName(ItemStack item) {
        ItemData itemData = new ItemData(item.getTypeId(), item.getDurability());
        String name = primaryName.get(itemData);
        if (name == null) {
            itemData = new ItemData(item.getTypeId(), (short) 0);
            name = primaryName.get(itemData);
            if (name == null) {
                return null;
            }
        }

        return name;
    }

    public static class ItemData {

        final private int itemNo;
        final private short itemData;

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

        @Override
        public int hashCode() {
            return (31 * itemNo) ^ itemData;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null) {
                return false;
            }
            if (!(o instanceof ItemData)) {
                return false;
            }

            ItemData pair = (ItemData) o;
            return this.itemNo == pair.getItemNo() && this.itemData == pair.getItemData();
        }
    }

    class LengthCompare implements java.util.Comparator<String> {

        public LengthCompare() {
            super();
        }

        @Override
        public int compare(String s1, String s2) {
            return s1.length() - s2.length();
        }
    }
}