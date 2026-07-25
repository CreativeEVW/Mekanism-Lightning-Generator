package com.mekltgt.item;

import com.mekltgt.block.DryIceBlock;
import mekanism.common.item.block.ItemBlockTooltip;
import net.minecraft.world.item.Item;

public class DryIceItem extends ItemBlockTooltip<DryIceBlock> {

    public DryIceItem(DryIceBlock block) {
        super(block, true, new Item.Properties());
    }
}