package com.doctordark.base.kit;

import com.doctordark.base.BasePlugin;
import com.doctordark.base.kit.argument.KitBypassplaytimeArgument;
import com.doctordark.base.kit.event.KitApplyEvent;
import com.doctordark.base.user.BaseUser;
import com.doctordark.util.ParticleEffect;
import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

/**
 * Listener that handles {@link Kit} based events.
 */
public class KitListener implements Listener {

    private static final ImmutableList<String> ALLOWED_KITS = ImmutableList.of("Fishing", "Feast");

    private final BasePlugin plugin;

    public KitListener(BasePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();
        if (inventory == null) return;

        String title = inventory.getTitle();
        if (title.contains("Kit Preview")) {
            event.setCancelled(true);
        }

        HumanEntity humanEntity = event.getWhoClicked();
        if (title.contains("Kit Selector") && humanEntity instanceof Player) {
            event.setCancelled(true);

            // The player didn't click an item in the top inventory (Kit Menu)
            if (!Objects.equals(event.getView().getTopInventory(), event.getClickedInventory())) {
                return;
            }

            ItemStack stack = event.getCurrentItem();
            if (stack == null || !stack.hasItemMeta()) {
                return;
            }

            ItemMeta meta = stack.getItemMeta();
            if (!meta.hasDisplayName()) {
                return;
            }

            Player player = (Player) humanEntity;
            String name = ChatColor.stripColor(stack.getItemMeta().getDisplayName());
            Kit kit = plugin.getKitManager().getKit(name);
            if (kit == null) {
                return;
            }

            //if (false && event.getClick() == ClickType.MIDDLE) {
            //TODO: player.openInventory(kit.getPreviewInventory());
            //} else {
            kit.applyTo(player, false, true);
            //}
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onKitSign(PlayerInteractEvent event) {
        // The player didn't right click a sign.
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block block = event.getClickedBlock();
            BlockState state = block.getState();
            if (!(state instanceof Sign)) {
                return;
            }

            Sign sign = (Sign) state;
            String[] lines = sign.getLines();

            // Verify that this a valid Kit sign first.
            if (lines.length >= 2 && lines[1].contains("Kit")) {
                Kit kit = plugin.getKitManager().getKit(lines.length >= 3 ? lines[2] : null);
                if (kit == null) {
                    return;
                }

                event.setCancelled(true);
                Player player = event.getPlayer();
                String[] fakeLines = Arrays.copyOf(sign.getLines(), 4);
                final boolean applied = kit.applyTo(player, false, false);
                if (applied) {
                    fakeLines[0] = ChatColor.GREEN + "Successfully";
                    fakeLines[1] = ChatColor.GREEN + "equipped kit";
                    fakeLines[2] = kit.getName();
                    fakeLines[3] = "";
                } else {
                    fakeLines[0] = ChatColor.RED + "Failed to";
                    fakeLines[1] = ChatColor.RED + "equip kit";
                    fakeLines[2] = kit.getName();
                    fakeLines[3] = ChatColor.RED + "Check chat";
                }

                if (applied && plugin.getSignHandler().showLines(player, sign, fakeLines, 100L, false)) {
                    ParticleEffect.FIREWORK_SPARK.display(player, sign.getLocation().add(0.5, 0.5, 0.5), 0.01F, 10);
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onKitApply(KitApplyEvent event) {
        if (event.isForce()) {
            return;
        }

        Player player = event.getPlayer();
        Kit kit = event.getKit();

        if (!player.isOp() && !kit.isEnabled()) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "The " + kit.getName() + " kit is currently disabled.");
            return;
        }

        if (!player.hasPermission(kit.getPermission())) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You do not have permission to use this kit.");
            return;
        }

        UUID uuid = player.getUniqueId();
        long curPlayTime = BasePlugin.getPlugin().getPlayTimeManager().getTotalPlayTime(uuid);
        long minPlayTimeKits = KitManager.MIN_PLAYTIME_KITS;
        if (!KitBypassplaytimeArgument.bypassPlaytime && curPlayTime < minPlayTimeKits && !ALLOWED_KITS.contains(kit.getName())) {
            player.sendMessage(ChatColor.RED + "Kits (with the exception of " + StringUtils.join(ALLOWED_KITS, ", ") + " are disabled until you have at least " +
                    DurationFormatUtils.formatDurationWords(minPlayTimeKits, true, true) + " playtime. " +
                    "You have only played for " + DurationFormatUtils.formatDurationWords(curPlayTime, true, true) + '.');

            event.setCancelled(true);
            return;
        }

        BaseUser baseUser = plugin.getUserManager().getUser(uuid);
        long remaining = baseUser.getRemainingKitCooldown(kit);

        if (remaining > 0L) {
            player.sendMessage(ChatColor.RED + "You cannot use the " + kit.getName() + " kit for " + DurationFormatUtils.formatDurationWords(remaining, true, true) + '.');
            event.setCancelled(true);
            return;
        }

        int curUses = baseUser.getKitUses(kit);
        int maxUses = kit.getMaximumUses();

        if (curUses >= maxUses && maxUses != FlatFileKitManager.UNLIMITED_USES) {
            player.sendMessage(ChatColor.RED + "You have already used this kit " + curUses + '/' + maxUses + " times.");
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onKitApplyMonitor(KitApplyEvent event) {
        if (!event.isForce()) {
            Kit kit = event.getKit();
            BaseUser baseUser = plugin.getUserManager().getUser(event.getPlayer().getUniqueId());
            baseUser.incrementKitUses(kit);
            baseUser.updateKitCooldown(kit);
        }
    }
}
