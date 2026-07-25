package com.mekltgt.block;

import com.mekltgt.Mekltgt;
import com.mekltgt.blockentity.DryIceBlockEntity;
import com.mekltgt.registries.ExtraRegistration;
import mekanism.api.text.ILangEntry;
import mekanism.common.block.interfaces.IHasDescription;
import mekanism.common.block.prefab.BlockTile;
import mekanism.common.content.blocktype.BlockTypeTile;
import mekanism.common.registration.impl.TileEntityTypeRegistryObject;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class DryIceBlock extends BlockTile.BlockTileModel<DryIceBlockEntity, BlockTypeTile<DryIceBlockEntity>> implements IHasDescription {

    private static final VoxelShape SHAPE = Block.box(4.0, 0.0, 4.0, 12.0, 8.0, 12.0);

    public DryIceBlock() {
        super(ExtraRegistration.DRY_ICE_TYPE, Block.Properties.of()
                .mapColor(MapColor.SNOW)
                .strength(0.5F)
                .sound(SoundType.GLASS)
                .noOcclusion()
                .randomTicks());
    }

    @NotNull
    @Override
    public TileEntityTypeRegistryObject<DryIceBlockEntity> getTileType() {
        return ExtraRegistration.DRY_ICE_BE;
    }

    @NotNull
    @Override
    public ILangEntry getDescription() {
        return () -> Util.makeDescriptionId("description", ResourceLocation.fromNamespaceAndPath(Mekltgt.MODID, "dry_ice"));
    }

    @NotNull
    @Override
    public VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return SHAPE;
    }

    @NotNull
    @Override
    public VoxelShape getCollisionShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return SHAPE;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        double centerX = pos.getX() + 0.5;
        double centerY = pos.getY() + 0.5;
        double centerZ = pos.getZ() + 0.5;

        for (int i = 0; i < 3; i++) {
            double x = centerX + (random.nextDouble() - 0.5) * 4.0;
            double y = centerY + (random.nextDouble() - 0.5) * 4.0;
            double z = centerZ + (random.nextDouble() - 0.5) * 4.0;
            level.addParticle(ParticleTypes.SNOWFLAKE, x, y, z, 0, 0, 0);
        }
    }
}