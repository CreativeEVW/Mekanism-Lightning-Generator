package com.mekltgt.block;

import com.mekltgt.blockentity.SuperProbeBlockEntity;
import com.mekltgt.registries.ExtraRegistration;
import mekanism.common.block.prefab.BlockTile;
import mekanism.common.content.blocktype.BlockTypeTile;
import mekanism.common.registration.impl.TileEntityTypeRegistryObject;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class SuperProbeBlock extends BlockTile.BlockTileModel<SuperProbeBlockEntity, BlockTypeTile<SuperProbeBlockEntity>> {

    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    private static final VoxelShape BASE = Block.box(5, 0, 5, 11, 3, 11);
    private static final VoxelShape PILLAR = Block.box(7, 3, 7, 9, 11, 9);
    private static final VoxelShape RING1 = Block.box(6, 4, 6, 10, 5, 10);
    private static final VoxelShape RING2 = Block.box(6, 6, 6, 10, 7, 10);
    private static final VoxelShape RING3 = Block.box(6, 8, 6, 10, 9, 10);
    private static final VoxelShape SHAPE = Shapes.or(BASE, PILLAR, RING1, RING2, RING3);

    public SuperProbeBlock() {
        super(ExtraRegistration.SUPER_PROBE_TYPE, Block.Properties.of()
                .mapColor(MapColor.COLOR_PURPLE)
                .strength(3.0F)
                .noOcclusion());
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.UP));
    }

    @NotNull
    @Override
    public TileEntityTypeRegistryObject<SuperProbeBlockEntity> getTileType() {
        return ExtraRegistration.SUPER_PROBE_BE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, Direction.UP);
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
