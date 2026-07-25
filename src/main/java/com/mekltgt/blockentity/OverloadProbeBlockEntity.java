package com.mekltgt.blockentity;

import com.mekltgt.registries.ExtraRegistration;
import mekanism.common.tile.base.TileEntityMekanism;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class OverloadProbeBlockEntity extends TileEntityMekanism {

    public OverloadProbeBlockEntity(BlockPos pos, BlockState state) {
        super(ExtraRegistration.OVERLOAD_PROBE, pos, state);
    }
}