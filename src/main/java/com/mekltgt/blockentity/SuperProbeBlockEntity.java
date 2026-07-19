package com.mekltgt.blockentity;

import com.mekltgt.registries.ExtraRegistration;
import mekanism.common.tile.base.TileEntityMekanism;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class SuperProbeBlockEntity extends TileEntityMekanism {

    public SuperProbeBlockEntity(BlockPos pos, BlockState state) {
        super(ExtraRegistration.SUPER_PROBE, pos, state);
    }
}
