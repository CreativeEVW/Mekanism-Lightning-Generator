package com.mekltgt.item;

import com.mekltgt.block.RocketLaunchPlatformBlock;
import mekanism.common.item.block.ItemBlockTooltip;
import net.minecraft.world.item.Item;

public class RocketLaunchPlatformItem extends ItemBlockTooltip<RocketLaunchPlatformBlock> {

    public RocketLaunchPlatformItem(RocketLaunchPlatformBlock block) {
        super(block, true, new Item.Properties());
    }
}