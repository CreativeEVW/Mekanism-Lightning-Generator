package com.mekltgt.item;

import com.mekltgt.block.SuperProbeBlock;
import mekanism.common.item.block.ItemBlockTooltip;
import net.minecraft.world.item.Item;

public class SuperProbeItem extends ItemBlockTooltip<SuperProbeBlock> {

    public SuperProbeItem(SuperProbeBlock block) {
        super(block, true, new Item.Properties());
    }
}