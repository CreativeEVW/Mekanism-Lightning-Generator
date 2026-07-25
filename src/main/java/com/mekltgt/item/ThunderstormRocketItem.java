package com.mekltgt.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class ThunderstormRocketItem extends Item {

    public ThunderstormRocketItem() {
        super(new Item.Properties().stacksTo(16));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        if (flag.isAdvanced()) {
            tooltip.add(Component.translatable("description.mekltgt.thunderstorm_rocket"));
        }
    }
}