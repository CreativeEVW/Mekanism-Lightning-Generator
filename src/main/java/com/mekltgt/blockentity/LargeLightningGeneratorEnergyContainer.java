package com.mekltgt.blockentity;

import mekanism.api.IContentsListener;
import mekanism.api.functions.ConstantPredicates;
import mekanism.common.capabilities.energy.BasicEnergyContainer;
import mekanism.common.capabilities.energy.MachineEnergyContainer;

/**
 * 大型闪电发电机的电力容器：输出型，能量升级按 Mekanism 原版倍率生效。
 */
public class LargeLightningGeneratorEnergyContainer extends MachineEnergyContainer<LargeLightningGeneratorBlockEntity> {

    public LargeLightningGeneratorEnergyContainer(LargeLightningGeneratorBlockEntity tile, IContentsListener listener) {
        super(
                MachineEnergyContainer.validateBlock(tile).getStorage(),
                MachineEnergyContainer.validateBlock(tile).getUsage(),
                ConstantPredicates.alwaysTrue(),
                BasicEnergyContainer.internalOnly,
                tile, listener);
    }
}
