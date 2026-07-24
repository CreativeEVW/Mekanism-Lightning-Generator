package com.mekltgt.item;

import com.mekltgt.block.DryIceBlock;
import mekanism.common.item.block.ItemBlockMekanism;
import net.minecraft.world.item.Item;

public class DryIceItem extends ItemBlockMekanism<DryIceBlock> {

    public DryIceItem(DryIceBlock block) {
        super(block, new Item.Properties());
    }
}