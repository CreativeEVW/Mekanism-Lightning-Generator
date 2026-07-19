package com.mekltgt.item;

import com.mekltgt.block.LightningGeneratorBlock;
import mekanism.common.item.block.ItemBlockTooltip;
import net.minecraft.world.item.Item;

public class LightningGeneratorItem extends ItemBlockTooltip<LightningGeneratorBlock> {

    public LightningGeneratorItem(LightningGeneratorBlock block) {
        super(block, true, new Item.Properties());
    }
}
