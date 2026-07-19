package com.mekltgt.block;

import com.mekltgt.Mekltgt;
import com.mekltgt.blockentity.LightningGeneratorBlockEntity;
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

public class LightningGeneratorBlock extends BlockTile.BlockTileModel<LightningGeneratorBlockEntity, Generator<LightningGeneratorBlockEntity>> implements IHasDescription {

    private static final VoxelShape SHAPE;

    static {
        VoxelShape base = Block.box(3, 0, 3, 13, 3, 13);
        VoxelShape pillars = Shapes.or(
                Block.box(10, 2, 7, 12, 16, 9),
                Block.box(7, 2, 10, 9, 16, 12),
                Block.box(9, 2, 9, 11, 16, 11),
                Block.box(5, 2, 9, 7, 16, 11),
                Block.box(7, 2, 4, 9, 16, 6),
                Block.box(9, 2, 5, 11, 16, 7),
                Block.box(5, 2, 5, 7, 16, 7),
                Block.box(8, 2, 7, 10, 16, 9),
                Block.box(4, 2, 7, 6, 16, 9),
                Block.box(6, 2, 7, 8, 16, 9),
                Block.box(7, 2, 6, 9, 16, 7),
                Block.box(7, 2, 9, 9, 16, 10)
        );
        SHAPE = Shapes.or(base, pillars);
    }

    public LightningGeneratorBlock() {
        super(ExtraRegistration.LIGHTNING_GENERATOR_MACHINE, Block.Properties.of()
                .mapColor(MapColor.COLOR_GREEN)
                .strength(3.5F, 16F)
                .requiresCorrectToolForDrops()
                .noOcclusion());
    }

    @NotNull
    @Override
    public TileEntityTypeRegistryObject<LightningGeneratorBlockEntity> getTileType() {
        return ExtraRegistration.LIGHTNING_GENERATOR_BE;
    }

    @NotNull
    @Override
    public ILangEntry getDescription() {
        return () -> Util.makeDescriptionId("description", ResourceLocation.fromNamespaceAndPath(Mekltgt.MODID, "lightning_generator"));
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