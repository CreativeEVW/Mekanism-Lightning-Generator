package com.mekltgt.item;

import com.mekltgt.block.OverloadProbeBlock;
import mekanism.common.item.block.ItemBlockTooltip;
import net.minecraft.world.item.Item;

public class OverloadProbeItem extends ItemBlockTooltip<OverloadProbeBlock> {

    public OverloadProbeItem(OverloadProbeBlock block) {
        super(block, true, new Item.Properties());
    }
}