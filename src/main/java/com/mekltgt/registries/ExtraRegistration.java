package com.mekltgt.registries;

import com.mekltgt.Mekltgt;
import com.mekltgt.block.LightningGeneratorBlock;
import com.mekltgt.block.SuperProbeBlock;
import com.mekltgt.blockentity.LightningGeneratorBlockEntity;
import com.mekltgt.blockentity.SuperProbeBlockEntity;
import com.mekltgt.gear.ModuleLightningAbsorptionUnit;
import com.mekltgt.item.LightningGeneratorItem;
import com.mekltgt.item.SuperProbeItem;
import mekanism.api.text.ILangEntry;
import mekanism.common.block.attribute.AttributeEnergy;
import mekanism.common.block.attribute.AttributeUpgradeSupport;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.content.blocktype.BlockTypeTile;
import mekanism.common.inventory.container.tile.MekanismTileContainer;
import mekanism.common.registration.impl.BlockRegistryObject;
import mekanism.common.registration.impl.ContainerTypeDeferredRegister;
import mekanism.common.registration.impl.ContainerTypeRegistryObject;
import mekanism.common.registration.impl.ModuleDeferredRegister;
import mekanism.common.registration.impl.ModuleRegistryObject;
import mekanism.common.registration.impl.TileEntityTypeDeferredRegister;
import mekanism.common.registration.impl.TileEntityTypeRegistryObject;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.generators.common.content.blocktype.Generator;
import mekanism.generators.common.content.blocktype.Generator.GeneratorBuilder;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;

public class ExtraRegistration {
    public static final TileEntityTypeDeferredRegister TILE_ENTITY_TYPES = new TileEntityTypeDeferredRegister(Mekltgt.MODID);
    public static final ContainerTypeDeferredRegister CONTAINER_TYPES = new ContainerTypeDeferredRegister(Mekltgt.MODID);
    public static final ModuleDeferredRegister MODULES = new ModuleDeferredRegister(Mekltgt.MODID);

    // Lightning Generator
    public static BlockRegistryObject<LightningGeneratorBlock, LightningGeneratorItem> LIGHTNING_GENERATOR;
    public static TileEntityTypeRegistryObject<LightningGeneratorBlockEntity> LIGHTNING_GENERATOR_BE;
    public static Generator<LightningGeneratorBlockEntity> LIGHTNING_GENERATOR_MACHINE;
    public static ContainerTypeRegistryObject<MekanismTileContainer<LightningGeneratorBlockEntity>> LIGHTNING_GENERATOR_CONTAINER;

    // Super Probe
    public static BlockRegistryObject<SuperProbeBlock, SuperProbeItem> SUPER_PROBE;
    public static TileEntityTypeRegistryObject<SuperProbeBlockEntity> SUPER_PROBE_BE;
    public static BlockTypeTile<SuperProbeBlockEntity> SUPER_PROBE_TYPE;

    // Lightning Absorption Unit
    public static DeferredItem<Item> LIGHTNING_ABSORPTION_ITEM;
    public static ModuleRegistryObject<ModuleLightningAbsorptionUnit> LIGHTNING_ABSORPTION_MODULE;

    private static final ILangEntry LG_DESCRIPTION = new ILangEntry() {
        @Override
        public String getTranslationKey() {
            return Util.makeDescriptionId("description", ResourceLocation.fromNamespaceAndPath(Mekltgt.MODID, "lightning_generator"));
        }
    };

    private static final ILangEntry SP_DESCRIPTION = new ILangEntry() {
        @Override
        public String getTranslationKey() {
            return Util.makeDescriptionId("description", ResourceLocation.fromNamespaceAndPath(Mekltgt.MODID, "super_probe"));
        }
    };

    public static void initLightningGenerator(BlockRegistryObject<LightningGeneratorBlock, LightningGeneratorItem> blockRO) {
        LIGHTNING_GENERATOR = blockRO;

        LIGHTNING_GENERATOR_BE = TILE_ENTITY_TYPES.mekBuilder(LIGHTNING_GENERATOR,
                        (pos, state) -> new LightningGeneratorBlockEntity(pos, state))
                .serverTicker(TileEntityMekanism::tickServer)
                .withSimple(Capabilities.CONFIG_CARD)
                .build();

        LIGHTNING_GENERATOR_CONTAINER = CONTAINER_TYPES.custom("lightning_generator", LightningGeneratorBlockEntity.class)
                .armorSideBar(-20, 11, 0)
                .build();

        LIGHTNING_GENERATOR_MACHINE = GeneratorBuilder
                .createGenerator(() -> LIGHTNING_GENERATOR_BE, LG_DESCRIPTION)
                .withGui(() -> LIGHTNING_GENERATOR_CONTAINER)
                .withEnergyConfig(() -> LightningGeneratorBlockEntity.MAX_ENERGY)
                .with(AttributeUpgradeSupport.ENERGY_ONLY)
                .with(new AttributeEnergy(() -> 0L, () -> LightningGeneratorBlockEntity.MAX_ENERGY))
                .withComputerSupport("lightningGenerator")
                .build();
    }

    public static void initSuperProbe(BlockRegistryObject<SuperProbeBlock, SuperProbeItem> blockRO) {
        SUPER_PROBE = blockRO;

        SUPER_PROBE_TYPE = new BlockTypeTile<>(() -> SUPER_PROBE_BE, SP_DESCRIPTION);

        SUPER_PROBE_BE = TILE_ENTITY_TYPES.mekBuilder(SUPER_PROBE,
                        (pos, state) -> new SuperProbeBlockEntity(pos, state))
                .withSimple(Capabilities.CONFIG_CARD)
                .build();
    }

    public static void initLightningAbsorptionModule(DeferredItem<Item> item) {
        LIGHTNING_ABSORPTION_ITEM = item;
        LIGHTNING_ABSORPTION_MODULE = MODULES.registerInstanced(
                "lightning_absorption_unit",
                ModuleLightningAbsorptionUnit::new,
                () -> item,
                builder -> builder.maxStackSize(1)
        );
    }

    public static void register(IEventBus bus) {
        TILE_ENTITY_TYPES.register(bus);
        CONTAINER_TYPES.register(bus);
        MODULES.register(bus);
    }
}
