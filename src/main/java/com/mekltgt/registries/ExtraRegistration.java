package com.mekltgt.registries;

import com.mekltgt.Mekltgt;
import com.mekltgt.block.DryIceBlock;
import com.mekltgt.block.LightningGeneratorBlock;
import com.mekltgt.block.RocketLaunchPlatformBlock;
import com.mekltgt.block.SuperProbeBlock;
import com.mekltgt.blockentity.DryIceBlockEntity;
import com.mekltgt.blockentity.LightningGeneratorBlockEntity;
import com.mekltgt.blockentity.RocketLaunchPlatformBlockEntity;
import com.mekltgt.blockentity.SuperProbeBlockEntity;
import com.mekltgt.gear.ModuleLightningAbsorptionUnit;
import com.mekltgt.item.DryIceItem;
import com.mekltgt.item.LightningGeneratorItem;
import com.mekltgt.item.RocketLaunchPlatformItem;
import com.mekltgt.item.SuperProbeItem;
import mekanism.api.chemical.Chemical;
import mekanism.api.text.ILangEntry;
import mekanism.common.block.attribute.AttributeEnergy;
import mekanism.common.block.attribute.AttributeUpgradeSupport;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.content.blocktype.BlockTypeTile;
import mekanism.common.inventory.container.tile.MekanismTileContainer;
import mekanism.common.registration.impl.BlockRegistryObject;
import mekanism.common.registration.impl.ChemicalDeferredRegister;
import mekanism.common.registration.impl.ContainerTypeDeferredRegister;
import mekanism.common.registration.impl.ContainerTypeRegistryObject;
import mekanism.common.registration.impl.DeferredChemical;
import mekanism.common.registration.impl.FluidDeferredRegister;
import mekanism.common.registration.impl.FluidDeferredRegister.MekanismFluidType;
import mekanism.common.registration.impl.FluidRegistryObject;
import mekanism.common.registration.impl.ModuleDeferredRegister;
import mekanism.common.registration.impl.ModuleRegistryObject;
import mekanism.common.registration.impl.TileEntityTypeDeferredRegister;
import mekanism.common.registration.impl.TileEntityTypeRegistryObject;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.generators.common.content.blocktype.Generator;
import mekanism.generators.common.content.blocktype.Generator.GeneratorBuilder;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.LiquidBlock;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.BaseFlowingFluid.Flowing;
import net.neoforged.neoforge.fluids.BaseFlowingFluid.Source;
import net.neoforged.neoforge.registries.DeferredItem;

public class ExtraRegistration {
    public static final TileEntityTypeDeferredRegister TILE_ENTITY_TYPES = new TileEntityTypeDeferredRegister(Mekltgt.MODID);
    public static final ContainerTypeDeferredRegister CONTAINER_TYPES = new ContainerTypeDeferredRegister(Mekltgt.MODID);
    public static final ModuleDeferredRegister MODULES = new ModuleDeferredRegister(Mekltgt.MODID);
    public static final ChemicalDeferredRegister CHEMICALS = new ChemicalDeferredRegister(Mekltgt.MODID);
    public static final FluidDeferredRegister FLUIDS = new FluidDeferredRegister(Mekltgt.MODID);

    // Chemicals
    public static final DeferredChemical<Chemical> CARBON_DIOXIDE = CHEMICALS.register("carbon_dioxide", 0x808080);

    // Fluids
    public static final FluidRegistryObject<MekanismFluidType, Source, Flowing, LiquidBlock, BucketItem> LIQUID_CARBON_DIOXIDE =
            FLUIDS.register("carbon_dioxide",
                    properties -> properties.temperature(195).density(1000).viscosity(1000),
                    renderProperties -> renderProperties.tint(0xFF808080));

    // Lightning Generator
    public static BlockRegistryObject<LightningGeneratorBlock, LightningGeneratorItem> LIGHTNING_GENERATOR;
    public static TileEntityTypeRegistryObject<LightningGeneratorBlockEntity> LIGHTNING_GENERATOR_BE;
    public static Generator<LightningGeneratorBlockEntity> LIGHTNING_GENERATOR_MACHINE;
    public static ContainerTypeRegistryObject<MekanismTileContainer<LightningGeneratorBlockEntity>> LIGHTNING_GENERATOR_CONTAINER;

    // Rocket Launch Platform
    public static BlockRegistryObject<RocketLaunchPlatformBlock, RocketLaunchPlatformItem> ROCKET_LAUNCH_PLATFORM;
    public static TileEntityTypeRegistryObject<RocketLaunchPlatformBlockEntity> ROCKET_LAUNCH_PLATFORM_BE;
    public static Generator<RocketLaunchPlatformBlockEntity> ROCKET_LAUNCH_PLATFORM_MACHINE;
    public static ContainerTypeRegistryObject<MekanismTileContainer<RocketLaunchPlatformBlockEntity>> ROCKET_LAUNCH_PLATFORM_CONTAINER;

    // Dry Ice
    public static BlockRegistryObject<DryIceBlock, DryIceItem> DRY_ICE;
    public static TileEntityTypeRegistryObject<DryIceBlockEntity> DRY_ICE_BE;
    public static BlockTypeTile<DryIceBlockEntity> DRY_ICE_TYPE;

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

    private static final ILangEntry DI_DESCRIPTION = new ILangEntry() {
        @Override
        public String getTranslationKey() {
            return Util.makeDescriptionId("description", ResourceLocation.fromNamespaceAndPath(Mekltgt.MODID, "dry_ice"));
        }
    };

    private static final ILangEntry RLP_DESCRIPTION = new ILangEntry() {
        @Override
        public String getTranslationKey() {
            return Util.makeDescriptionId("description", ResourceLocation.fromNamespaceAndPath(Mekltgt.MODID, "rocket_launch_platform"));
        }
    };

    public static void initRocketLaunchPlatform(BlockRegistryObject<RocketLaunchPlatformBlock, RocketLaunchPlatformItem> blockRO) {
        ROCKET_LAUNCH_PLATFORM = blockRO;

        ROCKET_LAUNCH_PLATFORM_BE = TILE_ENTITY_TYPES.mekBuilder(ROCKET_LAUNCH_PLATFORM,
                        (pos, state) -> new RocketLaunchPlatformBlockEntity(pos, state))
                .serverTicker(TileEntityMekanism::tickServer)
                .withSimple(Capabilities.CONFIG_CARD)
                .build();

        ROCKET_LAUNCH_PLATFORM_CONTAINER = CONTAINER_TYPES.custom("rocket_launch_platform", RocketLaunchPlatformBlockEntity.class)
                .armorSideBar(-20, 11, 0)
                .build();

        ROCKET_LAUNCH_PLATFORM_MACHINE = GeneratorBuilder
                .createGenerator(() -> ROCKET_LAUNCH_PLATFORM_BE, RLP_DESCRIPTION)
                .withGui(() -> ROCKET_LAUNCH_PLATFORM_CONTAINER)
                .withEnergyConfig(() -> RocketLaunchPlatformBlockEntity.MAX_ENERGY)
                .with(AttributeUpgradeSupport.MUFFLING_ONLY)
                .with(new AttributeEnergy(() -> 0L, () -> RocketLaunchPlatformBlockEntity.MAX_ENERGY))
                .build();
    }

    public static void initDryIce(BlockRegistryObject<DryIceBlock, DryIceItem> blockRO) {
        DRY_ICE = blockRO;

        DRY_ICE_TYPE = new BlockTypeTile<>(() -> DRY_ICE_BE, DI_DESCRIPTION);

        DRY_ICE_BE = TILE_ENTITY_TYPES.mekBuilder(DRY_ICE,
                        (pos, state) -> new DryIceBlockEntity(pos, state))
                .serverTicker(TileEntityMekanism::tickServer)
                .build();
    }

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
        CHEMICALS.register(bus);
        FLUIDS.register(bus);
    }
}
