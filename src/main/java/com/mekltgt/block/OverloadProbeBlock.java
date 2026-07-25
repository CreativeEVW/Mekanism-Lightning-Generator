package com.mekltgt.block;

import com.mekltgt.Mekltgt;
import com.mekltgt.blockentity.OverloadProbeBlockEntity;
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
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class OverloadProbeBlock extends BlockTile.BlockTileModel<OverloadProbeBlockEntity, BlockTypeTile<OverloadProbeBlockEntity>> implements IHasDescription {

    private static final VoxelShape BASE = Block.box(5, 0, 5, 11, 3, 11);
    private static final VoxelShape PILLAR = Block.box(7, 3, 7, 9, 11, 9);
    private static final VoxelShape RING1 = Block.box(6, 4, 6, 10, 5, 10);
    private static final VoxelShape RING2 = Block.box(6, 6, 6, 10, 7, 10);
    private static final VoxelShape RING3 = Block.box(6, 8, 6, 10, 9, 10);
    private static final VoxelShape SHAPE = Shapes.or(BASE, PILLAR, RING1, RING2, RING3);

    public OverloadProbeBlock() {
        super(ExtraRegistration.OVERLOAD_PROBE_TYPE, Block.Properties.of()
                .mapColor(MapColor.COLOR_PURPLE)
                .strength(3.0F)
                .noOcclusion());
    }

    @NotNull
    @Override
    public TileEntityTypeRegistryObject<OverloadProbeBlockEntity> getTileType() {
        return ExtraRegistration.OVERLOAD_PROBE_BE;
    }

    @NotNull
    @Override
    public ILangEntry getDescription() {
        return () -> Util.makeDescriptionId("description", ResourceLocation.fromNamespaceAndPath(Mekltgt.MODID, "overload_probe"));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState();
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
        if (random.nextInt(3) == 0) {
            double x = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.6;
            double y = pos.getY() + 0.5 + (random.nextDouble() - 0.5) * 0.6;
            double z = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.6;
            level.addParticle(ParticleTypes.ELECTRIC_SPARK, x, y, z, 0, 0.02, 0);
        }
    }
}