package com.mekltgt.item;

import com.mekltgt.block.LargeLightningGeneratorBlock;
import com.mekltgt.blockentity.LargeLightningGeneratorBlockEntity;
import com.mekltgt.registries.ExtraRegistration;
import mekanism.common.attachments.containers.ContainerType;
import mekanism.common.attachments.containers.fluid.FluidTanksBuilder;
import mekanism.common.config.MekanismConfig;
import mekanism.common.item.block.ItemBlockTooltip;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;

public class LargeLightningGeneratorItem extends ItemBlockTooltip<LargeLightningGeneratorBlock> {

    public LargeLightningGeneratorItem(LargeLightningGeneratorBlock block) {
        super(block, true, new Item.Properties());
    }

    @Override
    public void attachAttachments(IEventBus eventBus) {
        super.attachAttachments(eventBus);
        ContainerType.FLUID.addDefaultCreators(eventBus, this, () -> FluidTanksBuilder.builder()
                .addBasic(LargeLightningGeneratorBlockEntity.getMaxFluid(),
                        fluid -> fluid.getFluid() == ExtraRegistration.LIQUID_CARBON_DIOXIDE.get())
                .build(), MekanismConfig.storage);
    }
}