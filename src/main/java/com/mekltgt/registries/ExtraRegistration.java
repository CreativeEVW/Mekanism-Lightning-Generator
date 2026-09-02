package com.mekltgt.registries;

import com.mekltgt.Mekltgt;
import com.mekltgt.block.DryIceBlock;
import com.mekltgt.block.LargeLightningGeneratorBlock;
import com.mekltgt.block.LightningGeneratorBlock;
import com.mekltgt.block.OverloadProbeBlock;
import com.mekltgt.block.RocketLaunchPlatformBlock;
import com.mekltgt.block.SuperProbeBlock;
import com.mekltgt.blockentity.DryIceBlockEntity;
import com.mekltgt.blockentity.LargeLightningGeneratorBlockEntity;
import com.mekltgt.blockentity.LightningGeneratorBlockEntity;
import com.mekltgt.blockentity.OverloadProbeBlockEntity;
import com.mekltgt.blockentity.RocketLaunchPlatformBlockEntity;
import com.mekltgt.blockentity.SuperProbeBlockEntity;
import com.mekltgt.gear.ModuleLightningAbsorptionUnit;
import com.mekltgt.item.DryIceItem;
import com.mekltgt.item.LargeLightningGeneratorItem;
import com.mekltgt.item.LightningGeneratorItem;
import com.mekltgt.item.OverloadProbeItem;
import com.mekltgt.item.RocketLaunchPlatformItem;
import com.mekltgt.item.SuperProbeItem;
import mekanism.api.chemical.Chemical;
import mekanism.api.text.ILangEntry;
import mekanism.common.block.attribute.AttributeEnergy;
import mekanism.common.block.attribute.AttributeHasBounding;
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
import mekanism.common.util.VoxelShapeUtils;
import mekanism.generators.common.content.blocktype.Generator;
import mekanism.generators.common.content.blocktype.Generator.GeneratorBuilder;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
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

    // Large Lightning Generator
    public static BlockRegistryObject<LargeLightningGeneratorBlock, LargeLightningGeneratorItem> LARGE_LIGHTNING_GENERATOR;
    public static TileEntityTypeRegistryObject<LargeLightningGeneratorBlockEntity> LARGE_LIGHTNING_GENERATOR_BE;
    public static Generator<LargeLightningGeneratorBlockEntity> LARGE_LIGHTNING_GENERATOR_MACHINE;
    public static ContainerTypeRegistryObject<MekanismTileContainer<LargeLightningGeneratorBlockEntity>> LARGE_LIGHTNING_GENERATOR_CONTAINER;

    // Rocket Launch Platform
    public static BlockRegistryObject<RocketLaunchPlatformBlock, RocketLaunchPlatformItem> ROCKET_LAUNCH_PLATFORM;
    public static TileEntityTypeRegistryObject<RocketLaunchPlatformBlockEntity> ROCKET_LAUNCH_PLATFORM_BE;
    public static Generator<RocketLaunchPlatformBlockEntity> ROCKET_LAUNCH_PLATFORM_MACHINE;
    public static ContainerTypeRegistryObject<MekanismTileContainer<RocketLaunchPlatformBlockEntity>> ROCKET_LAUNCH_PLATFORM_CONTAINER;

    // Dry Ice
    public static BlockRegistryObject<DryIceBlock, DryIceItem> DRY_ICE;
    public static TileEntityTypeRegistryObject<DryIceBlockEntity> DRY_ICE_BE;
    public static BlockTypeTile<DryIceBlockEntity> DRY_ICE_TYPE;

    // Overload Probe (ae2lt cross-mod)
    public static BlockRegistryObject<OverloadProbeBlock, OverloadProbeItem> OVERLOAD_PROBE;
    public static TileEntityTypeRegistryObject<OverloadProbeBlockEntity> OVERLOAD_PROBE_BE;
    public static BlockTypeTile<OverloadProbeBlockEntity> OVERLOAD_PROBE_TYPE;

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

    private static final ILangEntry LLG_DESCRIPTION = new ILangEntry() {
        @Override
        public String getTranslationKey() {
            return Util.makeDescriptionId("description", ResourceLocation.fromNamespaceAndPath(Mekltgt.MODID, "large_lightning_generator"));
        }
    };

    /** 3x3x3 多方块结构（主方块位于底部，模型渲染时上移 1 格对齐） */
    private static final AttributeHasBounding THREE_BY_THREE_BY_THREE = new AttributeHasBounding(
            new AttributeHasBounding.HandleBoundingBlock() {
                @Override
                public <DATA> boolean handle(net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos,
                        net.minecraft.world.level.block.state.BlockState state, DATA data,
                        AttributeHasBounding.TriBooleanFunction<net.minecraft.world.level.Level, net.minecraft.core.BlockPos, DATA> predicate) {
                    net.minecraft.core.BlockPos.MutableBlockPos mutable = new net.minecraft.core.BlockPos.MutableBlockPos();
                    for (int x = -1; x <= 1; x++) {
                        for (int y = 0; y <= 2; y++) {
                            for (int z = -1; z <= 1; z++) {
                                if (x != 0 || y != 0 || z != 0) {
                                    mutable.setWithOffset(pos, x, y, z);
                                    if (!predicate.accept(level, mutable, data)) {
                                        return false;
                                    }
                                }
                            }
                        }
                    }
                    return true;
                }
            });

        /** 碰撞形状（贴合模型几何，基础朝向朝北） */
    private static final VoxelShape LLG_BASE_SHAPE = Shapes.or(
        Block.box(5, 6, 28, 11, 12, 31),
        Block.box(4, 5, 31, 12, 13, 32),
        Block.box(5, 21, 28, 11, 27, 31),
        Block.box(4, 20, 31, 12, 28, 32),
        Block.box(6, 18, 28, 10, 21, 30),
        Block.box(11, 22, 28, 14, 26, 30),
        Block.box(6, 27, 28, 10, 30, 30),
        Block.box(2, 22, 28, 5, 26, 30),
        Block.box(29, 4, -13, 31, 33, -11),
        Block.box(29, 4, -9, 31, 33, -7),
        Block.box(29, 4, -5, 31, 33, -3),
        Block.box(29, 4, -1, 31, 33, 1),
        Block.box(29, 4, 3, 31, 33, 5),
        Block.box(29, 4, 7, 31, 33, 9),
        Block.box(29, 4, 11, 31, 33, 13),
        Block.box(29, 4, 15, 31, 33, 17),
        Block.box(29, 4, 19, 31, 33, 21),
        Block.box(29, 4, 23, 31, 33, 25),
        Block.box(29, 4, 27, 31, 33, 29),
        Block.box(-15, 4, -13, -13, 33, -11),
        Block.box(-15, 4, -9, -13, 33, -7),
        Block.box(-15, 4, -5, -13, 33, -3),
        Block.box(-15, 4, -1, -13, 33, 1),
        Block.box(-15, 4, 3, -13, 33, 5),
        Block.box(-15, 4, 7, -13, 33, 9),
        Block.box(-15, 4, 11, -13, 33, 13),
        Block.box(-15, 4, 15, -13, 33, 17),
        Block.box(-15, 4, 19, -13, 33, 21),
        Block.box(-15, 4, 23, -13, 33, 25),
        Block.box(-15, 4, 27, -13, 33, 29),
        Block.box(-13, 4, -15, -11, 33, -13),
        Block.box(-9, 4, -15, -7, 33, -13),
        Block.box(27, 4, -15, 29, 33, -13),
        Block.box(23, 4, -15, 25, 33, -13),
        Block.box(-5, 4, -15, -3, 33, -13),
        Block.box(-1, 4, -15, 1, 33, -13),
        Block.box(3, 4, -15, 5, 33, -13),
        Block.box(11, 4, -15, 13, 33, -13),
        Block.box(15, 4, -15, 17, 33, -13),
        Block.box(19, 4, -15, 21, 33, -13),
        Block.box(7, 4, -15, 9, 33, -13),
        Block.box(-13, 4, 29, -11, 33, 31),
        Block.box(-9, 4, 29, -7, 33, 31),
        Block.box(27, 4, 29, 29, 33, 31),
        Block.box(23, 4, 29, 25, 33, 31),
        Block.box(-5, 4, 29, -3, 33, 31),
        Block.box(-1, 4, 29, 1, 33, 31),
        Block.box(15, 4, 29, 17, 33, 31),
        Block.box(19, 4, 29, 21, 33, 31),
        Block.box(21, 37, -9, 25, 41, 25),
        Block.box(-9, 37, -9, -5, 41, 25),
        Block.box(-5, 37, 21, 21, 41, 25),
        Block.box(-5, 37, -9, 21, 41, -5),
        Block.box(15, 37, 1, 19, 43, 15),
        Block.box(-3, 37, 1, 1, 43, 15),
        Block.box(-3, 37, 15, 19, 43, 19),
        Block.box(-3, 37, -3, 19, 43, 1),
        Block.box(-4, 16, -16, 20, 31, -14),
        Block.box(-13, 4, -13, 29, 34, 29),
        Block.box(-16, 0, -16, 32, 4, 32),
        Block.box(-16, 33, -16, 32, 37, 32),
        Block.box(4, 20, 31, 12, 28, 32),
        Block.box(4, 5, 31, 12, 13, 32),
        Block.box(27, 37, 5, 32, 46, 12),
        Block.box(-16, 37, 5, -11, 46, 12),
        Block.box(5, 36, 6, 10, 48, 11),
        Block.box(4, 37, 27, 11, 46, 32),
        Block.box(4, 37, -16, 11, 46, -11)
    );

    private static final VoxelShape[] LLG_SHAPES = new VoxelShape[4];
    static {
        VoxelShapeUtils.setShape(LLG_BASE_SHAPE, LLG_SHAPES);
    }

    public static void initLargeLightningGenerator(BlockRegistryObject<LargeLightningGeneratorBlock, LargeLightningGeneratorItem> blockRO) {
        LARGE_LIGHTNING_GENERATOR = blockRO;

        LARGE_LIGHTNING_GENERATOR_BE = TILE_ENTITY_TYPES.mekBuilder(LARGE_LIGHTNING_GENERATOR,
                        (pos, state) -> new LargeLightningGeneratorBlockEntity(pos, state))
                .clientTicker(TileEntityMekanism::tickClient)
                .serverTicker(TileEntityMekanism::tickServer)
                .withSimple(Capabilities.CONFIG_CARD)
                .build();

        LARGE_LIGHTNING_GENERATOR_CONTAINER = CONTAINER_TYPES.custom("large_lightning_generator", LargeLightningGeneratorBlockEntity.class)
                .armorSideBar(-20, 11, 0)
                .build();

        LARGE_LIGHTNING_GENERATOR_MACHINE = GeneratorBuilder
                .createGenerator(() -> LARGE_LIGHTNING_GENERATOR_BE, LLG_DESCRIPTION)
                .withGui(() -> LARGE_LIGHTNING_GENERATOR_CONTAINER)
                .withEnergyConfig(() -> LargeLightningGeneratorBlockEntity.MAX_ENERGY)
                .with(AttributeUpgradeSupport.ENERGY_ONLY)
                .with(new AttributeEnergy(() -> 0L, () -> LargeLightningGeneratorBlockEntity.MAX_ENERGY))
                .withCustomShape(LLG_SHAPES)
                .with(THREE_BY_THREE_BY_THREE)
                .withComputerSupport("largeLightningGenerator")
                .build();
    }

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

    private static final ILangEntry OP_DESCRIPTION = new ILangEntry() {
        @Override
        public String getTranslationKey() {
            return Util.makeDescriptionId("description", ResourceLocation.fromNamespaceAndPath(Mekltgt.MODID, "overload_probe"));
        }
    };

    public static void initOverloadProbe(BlockRegistryObject<OverloadProbeBlock, OverloadProbeItem> blockRO) {
        OVERLOAD_PROBE = blockRO;

        OVERLOAD_PROBE_TYPE = new BlockTypeTile<>(() -> OVERLOAD_PROBE_BE, OP_DESCRIPTION);

        OVERLOAD_PROBE_BE = TILE_ENTITY_TYPES.mekBuilder(OVERLOAD_PROBE,
                        (pos, state) -> new OverloadProbeBlockEntity(pos, state))
                .withSimple(Capabilities.CONFIG_CARD)
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
