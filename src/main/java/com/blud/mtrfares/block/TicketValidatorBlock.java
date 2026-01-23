package com.blud.mtrfares.block;

import com.blud.mtrfares.ItemRegistry;
import com.blud.mtrfares.item.NormalTicketItem;
import com.blud.mtrfares.item.SingleUseTicketItem;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TicketValidatorBlock extends Block {
    public TicketValidatorBlock(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) return ActionResult.SUCCESS; // client: visual only

        ItemStack stack = player.getStackInHand(hand);
        if (stack.isEmpty()) {
            player.sendMessage(Text.literal("Insert a ticket."), true);
            return ActionResult.SUCCESS;
        }

        // Multi-use ticket handling (NormalTicketItem)
        if (stack.getItem() == ItemRegistry.NORMAL_TICKET) {
            if (NormalTicketItem.isExpired(stack)) {
                player.sendMessage(Text.literal("Ticket expired."), true);
                world.playSound(null, pos, SoundEvents.ENTITY_VILLAGER_NO, SoundCategory.BLOCKS, 0.8f, 0.8f);
                return ActionResult.SUCCESS;
            }

            if (NormalTicketItem.isFullyUsed(stack)) {
                player.sendMessage(Text.literal("Ticket has no remaining uses."), true);
                world.playSound(null, pos, SoundEvents.ENTITY_VILLAGER_NO, SoundCategory.BLOCKS, 0.8f, 0.8f);
                return ActionResult.SUCCESS;
            }

            // Honor system: record the use but do not physically block the player
            NormalTicketItem.incrementUseCount(stack);
            // Optionally consume: uncomment if you want to remove the ticket on validation
            // stack.decrement(1);

            player.sendMessage(Text.literal("Ticket validated. Thank you."), false);
            world.playSound(null, pos, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.BLOCKS, 0.8f, 1.0f);
            return ActionResult.SUCCESS;
        }

        // Single-use ticket handling
        if (stack.getItem() == ItemRegistry.SINGLE_USE_TICKET || stack.getItem() instanceof SingleUseTicketItem) {
            if (SingleUseTicketItem.isUsed(stack)) {
                player.sendMessage(Text.literal("This single-use ticket has already been used."), true);
                world.playSound(null, pos, SoundEvents.ENTITY_VILLAGER_NO, SoundCategory.BLOCKS, 0.8f, 0.8f);
                return ActionResult.SUCCESS;
            }

            // Mark used and consume one (honor system: we still consume/mark but do not gate)
            SingleUseTicketItem.markUsed(stack);
            stack.decrement(1);

            player.sendMessage(Text.literal("Single-use ticket accepted."), false);
            world.playSound(null, pos, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.BLOCKS, 0.8f, 1.0f);
            return ActionResult.SUCCESS;
        }

        player.sendMessage(Text.literal("This machine only accepts tickets."), true);
        return ActionResult.SUCCESS;
    }
}