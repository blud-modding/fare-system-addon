package com.blud.mtrfares;

import com.blud.mtrfares.item.SingleUseTicketItem;
import com.blud.mtrfares.item.SpecialTicketItem;
import com.blud.mtrfares.item.NormalTicketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public final class ItemRegistry {
    public static final Item SINGLE_USE_TICKET = new SingleUseTicketItem(new Item.Settings());
    public static final Item SPECIAL_TICKET = new SpecialTicketItem(new Item.Settings().maxCount(1));
    public static final Item NORMAL_TICKET = new NormalTicketItem(new Item.Settings().maxCount(1));

    public static void registerAll() {
        Registry.register(Registries.ITEM, new Identifier(FareSystemAddon.MOD_ID, "single_use_ticket"), SINGLE_USE_TICKET);
        Registry.register(Registries.ITEM, new Identifier(FareSystemAddon.MOD_ID, "special_ticket"), SPECIAL_TICKET);
        Registry.register(Registries.ITEM, new Identifier(FareSystemAddon.MOD_ID, "normal_ticket"), NORMAL_TICKET);
    }
}
