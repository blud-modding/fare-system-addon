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

public class TicketConsumerBlock extends Block {
    public TicketConsumerBlock(Settings settings) {
        super(settings);
    }

    /**
     * Honor-system consumer:
     * - NormalTicketItem: clear used flag and increment use count.
     * - SingleUseTicketItem: consume the ticket.
     * - All actions are server-side and only provide feedback; they do not block movement.
     */
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) return ActionResult.SUCCESS;

        ItemStack stack = player.getStackInHand(hand);
        if (stack.isEmpty()) {
            player.sendMessage(Text.literal("Insert a ticket to consume or reset."), true);
            return ActionResult.SUCCESS;
        }

        // If single-use ticket: consume it
        if (stack.getItem() == ItemRegistry.SINGLE_USE_TICKET || stack.getItem() instanceof SingleUseTicketItem) {
            // Optionally mark used before consuming; here we consume directly
            stack.decrement(1);
            player.sendMessage(Text.literal("Single-use ticket consumed."), false);
            world.playSound(null, pos, SoundEvents.ENTITY_GENERIC_EAT, SoundCategory.BLOCKS, 0.8f, 1.0f);
            return ActionResult.SUCCESS;
        }

        // If multi-use ticket: clear used flag and increment use count
        if (stack.getItem() == ItemRegistry.NORMAL_TICKET) {
            NormalTicketItem.clearUsed(stack);         // clear optional used flag
            NormalTicketItem.incrementUseCount(stack); // record a use

            player.sendMessage(Text.literal("Multi-use ticket consumed and reset."), false);
            world.playSound(null, pos, SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.BLOCKS, 0.8f, 1.0f);
            return ActionResult.SUCCESS;
        }

        player.sendMessage(Text.literal("This machine only accepts tickets."), true);
        return ActionResult.SUCCESS;
    }
}