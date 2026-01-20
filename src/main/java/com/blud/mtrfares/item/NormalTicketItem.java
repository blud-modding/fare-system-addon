package com.blud.mtrfares.item;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.world.World;

import java.time.Instant;
import java.util.List;

public class NormalTicketItem extends Item {
    // NBT keys
    private static final String KEY_USED = "mtrfares_used"; // optional, still available if you want it
    private static final String KEY_ZONES = "mtrfares_zones";
    private static final String KEY_CLASS = "mtrfares_class";
    private static final String KEY_PURCHASE = "mtrfares_purchase_ts"; // epoch seconds (long)
    private static final String KEY_VALID_MIN = "mtrfares_valid_minutes"; // validity duration in minutes (long)
    private static final String KEY_USE_COUNT = "mtrfares_use_count"; // number of times used
    private static final String KEY_MAX_USES = "mtrfares_max_uses"; // optional maximum uses

    public NormalTicketItem(Settings settings) {
        super(settings);
    }

    // Used flag helpers (optional)
    public static void markUsed(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return;
        NbtCompound tag = stack.getOrCreateNbt();
        tag.putBoolean(KEY_USED, true);
    }

    public static void clearUsed(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return;
        if (!stack.hasNbt()) return;
        NbtCompound tag = stack.getNbt();
        if (tag.contains(KEY_USED)) tag.remove(KEY_USED);
    }

    public static boolean isUsed(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        return stack.hasNbt() && stack.getNbt().contains(KEY_USED) && stack.getNbt().getBoolean(KEY_USED);
    }

    // Use counter helpers
    public static long getUseCount(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return 0L;
        return stack.hasNbt() ? stack.getNbt().getLong(KEY_USE_COUNT) : 0L;
    }

    public static void setUseCount(ItemStack stack, long count) {
        if (stack == null || stack.isEmpty()) return;
        stack.getOrCreateNbt().putLong(KEY_USE_COUNT, Math.max(0, count));
    }

    public static void incrementUseCount(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return;
        NbtCompound tag = stack.getOrCreateNbt();
        long current = tag.contains(KEY_USE_COUNT) ? tag.getLong(KEY_USE_COUNT) : 0L;
        tag.putLong(KEY_USE_COUNT, current + 1L);
    }

    // Optional max uses helpers
    public static void setMaxUses(ItemStack stack, long maxUses) {
        if (stack == null || stack.isEmpty()) return;
        stack.getOrCreateNbt().putLong(KEY_MAX_USES, Math.max(0, maxUses));
    }

    public static long getMaxUses(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return 0L;
        return stack.hasNbt() ? stack.getNbt().getLong(KEY_MAX_USES) : 0L;
    }

    public static boolean isFullyUsed(ItemStack stack) {
        long max = getMaxUses(stack);
        if (max <= 0) return false; // no max set => never "fully used"
        return getUseCount(stack) >= max;
    }

    // Zones
    public static void setZones(ItemStack stack, String zones) {
        if (stack == null || stack.isEmpty()) return;
        stack.getOrCreateNbt().putString(KEY_ZONES, zones);
    }

    public static String getZones(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return "";
        return stack.hasNbt() ? stack.getNbt().getString(KEY_ZONES) : "";
    }

    // Travel class
    public static void setTravelClass(ItemStack stack, String travelClass) {
        if (stack == null || stack.isEmpty()) return;
        stack.getOrCreateNbt().putString(KEY_CLASS, travelClass);
    }

    public static String getTravelClass(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return "";
        return stack.hasNbt() ? stack.getNbt().getString(KEY_CLASS) : "";
    }

    // Purchase time (epoch seconds)
    public static void setPurchaseNow(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return;
        stack.getOrCreateNbt().putLong(KEY_PURCHASE, Instant.now().getEpochSecond());
    }

    public static long getPurchaseEpochSec(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return 0L;
        return stack.hasNbt() ? stack.getNbt().getLong(KEY_PURCHASE) : 0L;
    }

    // Validity in minutes
    public static void setValidityMinutes(ItemStack stack, long minutes) {
        if (stack == null || stack.isEmpty()) return;
        stack.getOrCreateNbt().putLong(KEY_VALID_MIN, minutes);
    }

    public static long getValidityMinutes(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return 0L;
        return stack.hasNbt() ? stack.getNbt().getLong(KEY_VALID_MIN) : 0L;
    }

    // Expiry checks computed at check time
    public static boolean isExpired(ItemStack stack) {
        long purchase = getPurchaseEpochSec(stack);
        long minutes = getValidityMinutes(stack);
        if (purchase <= 0L || minutes <= 0L) return false; // treat as non-expiring if not set
        long now = Instant.now().getEpochSecond();
        long elapsedMin = (now - purchase) / 60L;
        return elapsedMin >= minutes;
    }

    public static long remainingMinutes(ItemStack stack) {
        long purchase = getPurchaseEpochSec(stack);
        long minutes = getValidityMinutes(stack);
        if (purchase <= 0L || minutes <= 0L) return Long.MAX_VALUE;
        long rem = minutes - ((Instant.now().getEpochSecond() - purchase) / 60L);
        return rem > 0 ? rem : 0;
    }

    // Tooltip showing zones, class, validity and use counter (no used/unused status)
    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        String zones = getZones(stack);
        if (!zones.isEmpty()) tooltip.add(Text.literal("Zones: " + zones));

        String travelClass = getTravelClass(stack);
        if (!travelClass.isEmpty()) tooltip.add(Text.literal("Class: " + travelClass));

        long minutes = getValidityMinutes(stack);
        if (minutes > 0 && getPurchaseEpochSec(stack) > 0) {
            long rem = remainingMinutes(stack);
            if (rem == Long.MAX_VALUE) {
                tooltip.add(Text.literal("Valid: unknown"));
            } else if (rem == 0) {
                tooltip.add(Text.literal("Valid: expired"));
            } else {
                tooltip.add(Text.literal("Remaining: " + rem + " min"));
            }
        }

        // Use counter display
        long uses = getUseCount(stack);
        long max = getMaxUses(stack);
        if (max > 0) {
            long remainingUses = max - uses;
            if (remainingUses < 0) remainingUses = 0;
            tooltip.add(Text.literal("Uses: " + uses + " / " + max + " (remaining " + remainingUses + ")"));
        } else {
            tooltip.add(Text.literal("Uses: " + uses));
        }
    }
}