package com.mekltgt.block;

import com.mekltgt.Mekltgt;
import com.mekltgt.blockentity.RocketLaunchPlatformBlockEntity;
import com.mekltgt.registries.ExtraRegistration;
import mekanism.api.text.ILangEntry;
import mekanism.common.block.interfaces.IHasDescription;
import mekanism.common.block.prefab.BlockTile;
import mekanism.common.registration.impl.TileEntityTypeRegistryObject;
import mekanism.generators.common.content.blocktype.Generator;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class RocketLaunchPlatformBlock extends BlockTile.BlockTileModel<RocketLaunchPlatformBlockEntity, Generator<RocketLaunchPlatformBlockEntity>> implements IHasDescription {

    private static final VoxelShape SHAPE = Shapes.or(
            // 底座
            Block.box(0, 0, 0, 16, 4, 16),
            // 四角立柱
            Block.box(0, 4, 0, 2, 14, 2),
            Block.box(14, 4, 0, 16, 14, 2),
            Block.box(0, 4, 14, 2, 14, 16),
            Block.box(14, 4, 14, 16, 14, 16),
            // 顶部四梁
            Block.box(0, 14, 0, 16, 16, 2),
            Block.box(0, 14, 14, 16, 16, 16),
            Block.box(14, 14, 2, 16, 16, 14),
            Block.box(0, 14, 2, 2, 16, 14),
            // 南侧细节板
            Block.box(5, 6, 14, 11, 12, 16),
            Block.box(7, 4, 14, 9, 6, 16),
            // 北侧细节板
            Block.box(5, 6, 0, 11, 12, 2),
            Block.box(7, 4, 0, 9, 6, 2),
            // 西侧细节板
            Block.box(0, 4, 7, 2, 6, 9),
            Block.box(0, 6, 5, 2, 12, 11),
            // 东侧细节板
            Block.box(14, 4, 7, 16, 6, 9),
            Block.box(14, 6, 5, 16, 12, 11)
    );

    public RocketLaunchPlatformBlock() {
        super(ExtraRegistration.ROCKET_LAUNCH_PLATFORM_MACHINE, Block.Properties.of()
                .mapColor(MapColor.COLOR_GRAY)
                .strength(3.5F, 16F)
                .requiresCorrectToolForDrops()
                .noOcclusion());
    }

    @NotNull
    @Override
    public TileEntityTypeRegistryObject<RocketLaunchPlatformBlockEntity> getTileType() {
        return ExtraRegistration.ROCKET_LAUNCH_PLATFORM_BE;
    }

    @NotNull
    @Override
    public ILangEntry getDescription() {
        return () -> Util.makeDescriptionId("description", ResourceLocation.fromNamespaceAndPath(Mekltgt.MODID, "rocket_launch_platform"));
    }

    @NotNull
    @Override
    public VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter world, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return SHAPE;
    }

    @NotNull
    @Override
    public VoxelShape getCollisionShape(@NotNull BlockState state, @NotNull BlockGetter world, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return SHAPE;
    }
}