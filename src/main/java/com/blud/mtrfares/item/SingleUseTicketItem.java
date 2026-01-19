package com.blud.mtrfares.item;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.world.World;

import java.util.List;

public class SingleUseTicketItem extends Item {
    private static final String NBT_USED = "mtrfares_used";

    public SingleUseTicketItem(Settings settings) {
        super(settings);
    }

    /** Returns true if the stack has been marked used. */
    public static boolean isUsed(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        if (!stack.hasNbt()) return false;
        return stack.getNbt().getBoolean(NBT_USED);
    }

    /** Marks the stack as used. Creates NBT if necessary. */
    public static void markUsed(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return;
        NbtCompound tag = stack.getOrCreateNbt();
        tag.putBoolean(NBT_USED, true);
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        if (isUsed(stack)) {
            tooltip.add(Text.literal("Status: Used"));
        } else {
            tooltip.add(Text.literal("Status: Unused"));
        }
    }
}