package com.blud.mtrfares;

import com.blud.mtrfares.block.TicketConsumerBlock;
import com.blud.mtrfares.block.TicketValidatorBlock;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public final class BlockRegistry {
    public static final Block TICKET_VALIDATOR = new TicketValidatorBlock(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK));
    public static final Block TICKET_CONSUMER = new TicketConsumerBlock(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK));

    public static void registerAll() {
        Registry.register(Registries.BLOCK, new Identifier(FareSystemAddon.MOD_ID, "ticket_validator"), TICKET_VALIDATOR);
        Registry.register(Registries.ITEM, new Identifier(FareSystemAddon.MOD_ID, "ticket_validator"),
                new BlockItem(TICKET_VALIDATOR, new Item.Settings()));

        Registry.register(Registries.BLOCK, new Identifier(FareSystemAddon.MOD_ID, "ticket_consumer"), TICKET_CONSUMER);
        Registry.register(Registries.ITEM, new Identifier(FareSystemAddon.MOD_ID, "ticket_consumer"),
                new BlockItem(TICKET_CONSUMER, new Item.Settings()));
    }
}