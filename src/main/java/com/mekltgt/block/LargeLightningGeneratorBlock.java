package com.mekltgt.block;

import com.mekltgt.Mekltgt;
import com.mekltgt.blockentity.LargeLightningGeneratorBlockEntity;
import com.mekltgt.registries.ExtraRegistration;
import mekanism.api.text.ILangEntry;
import mekanism.common.block.interfaces.IHasDescription;
import mekanism.common.block.prefab.BlockTile;
import mekanism.common.registration.impl.TileEntityTypeRegistryObject;
import mekanism.generators.common.content.blocktype.Generator;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.MapColor;
import org.jetbrains.annotations.NotNull;

public class LargeLightningGeneratorBlock extends BlockTile.BlockTileModel<LargeLightningGeneratorBlockEntity, Generator<LargeLightningGeneratorBlockEntity>> implements IHasDescription {

    public LargeLightningGeneratorBlock() {
        super(ExtraRegistration.LARGE_LIGHTNING_GENERATOR_MACHINE, Block.Properties.of()
                .mapColor(MapColor.COLOR_GREEN)
                .strength(3.5F, 16F)
                .requiresCorrectToolForDrops()
                .noOcclusion());
    }

    @NotNull
    @Override
    public TileEntityTypeRegistryObject<LargeLightningGeneratorBlockEntity> getTileType() {
        return ExtraRegistration.LARGE_LIGHTNING_GENERATOR_BE;
    }

    @NotNull
    @Override
    public ILangEntry getDescription() {
        return () -> Util.makeDescriptionId("description", ResourceLocation.fromNamespaceAndPath(Mekltgt.MODID, "large_lightning_generator"));
    }
}