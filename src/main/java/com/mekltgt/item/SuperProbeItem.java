package com.mekltgt.item;

import com.mekltgt.block.SuperProbeBlock;
import mekanism.common.item.block.ItemBlockMekanism;
import net.minecraft.world.item.Item;

public class SuperProbeItem extends ItemBlockMekanism<SuperProbeBlock> {

    public SuperProbeItem(SuperProbeBlock block) {
        super(block, new Item.Properties());
    }
}
