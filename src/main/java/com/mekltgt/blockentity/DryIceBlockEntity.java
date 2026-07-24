package com.mekltgt.blockentity;

import com.mekltgt.registries.ExtraRegistration;
import mekanism.common.tile.base.TileEntityMekanism;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class DryIceBlockEntity extends TileEntityMekanism {

    private static final int MAX_LIFETIME = 400; // 20秒 (20 TPS × 20)

    public DryIceBlockEntity(BlockPos pos, BlockState state) {
        super(ExtraRegistration.DRY_ICE, pos, state);
    }

    @Override
    protected boolean onUpdateServer() {
        super.onUpdateServer();

        if (level == null || isRemote()) return false;

        if (ticker % 10 == 0) {
            AABB area = new AABB(worldPosition).inflate(2);
            for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, area)) {
                // 维持冰冻视觉效果（屏幕边缘结冰/震动），每次补满冻伤槽
                // 因为原版aiStep每tick会-2，所以直接设到最大值确保效果持续
                entity.setTicksFrozen(entity.getTicksRequiredToFreeze());
            }
        }

        // 每2秒直接造成冰冻伤害（原版细雪伤害逻辑需要实体站在细雪里才扣血，
        // 干冰通过外部施加冰冻，必须手动调用hurt）
        if (ticker % 40 == 0) {
            AABB area = new AABB(worldPosition).inflate(2);
            for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, area)) {
                entity.hurt(level.damageSources().freeze(), 1.0F);
            }
        }

        // 20秒后消失
        if (ticker >= MAX_LIFETIME) {
            level.removeBlock(worldPosition, false);
        }

        return true;
    }

    public int getRemainingTicks() {
        return Math.max(0, MAX_LIFETIME - (int) ticker);
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
    }
}