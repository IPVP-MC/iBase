package com.doctordark.util.itemdb;

import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SimpleItemDb implements ItemDb {

    private static final Comparator<String> COMPARATOR = new Comparator<String>() {
        @Override
        public int compare(String o1, String o2) {
            return o1.length() - o2.length();
        }
    };

    private final Map<String, Integer> items = new HashMap<>();
    private final Map<ItemData, List<String>> names = new HashMap<>();
    private final Map<ItemData, String> primaryName = new HashMap<>();
    private final Map<String, Short> durabilities = new HashMap<>();
    private final ManagedFile file;
    private final Pattern splitPattern = Pattern.compile("((.*)[:+',;.](\\d+))");

    public SimpleItemDb(final JavaPlugin plugin) {
        this.file = new ManagedFile("items.csv", plugin);
        this.reloadItemDatabase();
    }

    private static final Pattern PARTS_PATTERN = Pattern.compile("[^a-z0-9]");

    @Override
    public void reloadItemDatabase() {
        if (file.getFile() == null) return;
        final List<String> lines = file.getLines();

        if (lines.isEmpty()) {
            return;
        }

        durabilities.clear();
        items.clear();
        names.clear();
        primaryName.clear();

        for (String line : lines) {
            line = line.trim().toLowerCase(Locale.ENGLISH);
            if (line.length() > 0 && line.charAt(0) == '#') {
                continue;
            }

            final String[] parts = PARTS_PATTERN.split(line);
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
                Collections.sort(nameList, COMPARATOR);
            } else {
                List<String> nameList = Lists.newArrayList(itemName);
                names.put(itemData, nameList);
                primaryName.put(itemData, itemName);
            }
        }
    }

    @Override
    public ItemStack getPotion(String id) {
        return getPotion(id, 1);
    }

    @Override
    public ItemStack getPotion(String id, int quantity) {
        boolean splash = false;
        boolean extended = false;

        if (id.endsWith("s")) {
            id = id.substring(0, id.length() - 1);
            splash = true;
        }

        if (id.endsWith("e")) {
            id = id.substring(0, id.length() - 1);
            extended = true;
        }

        Integer level = Ints.tryParse(id.substring(id.length() - 1, id.length()));
        id = id.substring(0, id.length() - 1);

        PotionType type;
        switch (id.toLowerCase(Locale.ENGLISH)) {
            case "hp":
                type = PotionType.FIRE_RESISTANCE;
                break;
            case "rp":
                type = PotionType.REGEN;
                break;
            case "dp":
                type = PotionType.INSTANT_DAMAGE;
                break;
            case "swp":
                type = PotionType.SPEED;
                break;
            case "slp":
                type = PotionType.SLOWNESS;
                break;
            case "strp":
                type = PotionType.STRENGTH;
                break;
            case "wp":
                type = PotionType.WEAKNESS;
                break;
            case "pp":
                type = PotionType.POISON;
                break;
            case "frp":
                type = PotionType.FIRE_RESISTANCE;
                break;
            case "invp":
                type = PotionType.INVISIBILITY;
                break;
            case "nvp":
                type = PotionType.NIGHT_VISION;
                break;
            default:
                return null;
        }

        if (level == null || level > type.getMaxLevel()) {
            return null;
        }

        Potion potion = new Potion(type);
        potion.setLevel(level);
        potion.setSplash(splash);
        potion.setHasExtendedDuration(extended);

        ItemStack result = potion.toItemStack(quantity);
        result.setDurability((short) (result.getDurability() + 8192));
        return result;
    }

    @Override
    public ItemStack getItem(String id) {
        ItemStack result = getItem(id, 1);

        if (result == null) {
            return null;
        }

        result.setAmount(result.getMaxStackSize());
        return result;
    }

    @Override
    public ItemStack getItem(String id, int quantity) {
        ItemStack result = getPotion(id, quantity);

        if (result != null) {
            return result;
        }

        int itemId = 0;
        short metaData = 0;
        String itemName;
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
                    Material bMaterial = Bukkit.getUnsafe().getMaterialFromInternalName(itemName.toLowerCase(Locale.ENGLISH));
                    itemId = bMaterial.getId();
                } catch (Exception ex) {
                    return null;
                }
            }
        }

        if (itemId < 1) {
            return null;
        }

        final Material mat = Material.getMaterial(itemId);
        if (mat == null) return null;

        result = new ItemStack(mat);
        result.setAmount(quantity);
        result.setDurability(metaData);
        return result;
    }

    @Override
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
            items.add(getItem(args[0]));
        }

        if (items.isEmpty() || items.get(0).getType() == Material.AIR) {
            return null;
        }

        return items;
    }

    @Override
    public String getName(ItemStack item) {
        return CraftItemStack.asNMSCopy(item).getName();
    }

    @Deprecated
    @Override
    public String getPrimaryName(ItemStack item) {
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

    @Override
    public String getNames(ItemStack item) {
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
}