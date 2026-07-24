package com.mekltgt;

import com.mekltgt.block.DryIceBlock;
import com.mekltgt.block.LightningGeneratorBlock;
import com.mekltgt.block.RocketLaunchPlatformBlock;
import com.mekltgt.block.SuperProbeBlock;
import com.mekltgt.blockentity.LightningGeneratorBlockEntity;
import com.mekltgt.config.MekltgtConfig;
import com.mekltgt.gui.LightningGeneratorScreen;
import com.mekltgt.gui.RocketLaunchPlatformScreen;
import com.mekltgt.item.DryIceItem;
import com.mekltgt.item.LightningGeneratorItem;
import com.mekltgt.item.RocketLaunchPlatformItem;
import com.mekltgt.item.SuperAlloyItem;
import com.mekltgt.item.SuperCoreItem;
import com.mekltgt.item.SuperProbeItem;
import com.mekltgt.item.ThunderstormRocketItem;
import com.mekltgt.registries.ExtraRegistration;
import mekanism.common.registration.impl.BlockRegistryObject;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import mekanism.client.ClientRegistrationUtil;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(Mekltgt.MODID)
public class Mekltgt {
    public static final String MODID = "mekltgt";

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final DeferredItem<Item> SUPER_ALLOY = ITEMS.register("super_alloy", SuperAlloyItem::new);
    public static final DeferredItem<Item> SUPER_CORE = ITEMS.register("super_core", SuperCoreItem::new);

    public static final DeferredBlock<SuperProbeBlock> SUPER_PROBE = BLOCKS.register("super_probe", SuperProbeBlock::new);
    public static final DeferredItem<SuperProbeItem> SUPER_PROBE_ITEM = ITEMS.register("super_probe",
            () -> new SuperProbeItem(SUPER_PROBE.get()));

    public static final DeferredBlock<LightningGeneratorBlock> LIGHTNING_GENERATOR_BLOCK = BLOCKS.register("lightning_generator", LightningGeneratorBlock::new);
    public static final DeferredItem<LightningGeneratorItem> LIGHTNING_GENERATOR_ITEM = ITEMS.register("lightning_generator", () -> new LightningGeneratorItem(LIGHTNING_GENERATOR_BLOCK.get()));

    public static final DeferredBlock<RocketLaunchPlatformBlock> ROCKET_LAUNCH_PLATFORM_BLOCK = BLOCKS.register("rocket_launch_platform", RocketLaunchPlatformBlock::new);
    public static final DeferredItem<RocketLaunchPlatformItem> ROCKET_LAUNCH_PLATFORM_ITEM = ITEMS.register("rocket_launch_platform",
            () -> new RocketLaunchPlatformItem(ROCKET_LAUNCH_PLATFORM_BLOCK.get()));

    public static final DeferredBlock<DryIceBlock> DRY_ICE_BLOCK = BLOCKS.register("dry_ice", DryIceBlock::new);
    public static final DeferredItem<DryIceItem> DRY_ICE_ITEM = ITEMS.register("dry_ice",
            () -> new DryIceItem(DRY_ICE_BLOCK.get()));

    public static final DeferredItem<ThunderstormRocketItem> THUNDERSTORM_ROCKET = ITEMS.register("thunderstorm_rocket",
            ThunderstormRocketItem::new);

    public static final DeferredItem<Item> LIGHTNING_ABSORPTION_ITEM = ITEMS.register("lightning_absorption_unit",
            () -> mekanism.common.content.gear.ModuleHelper.get().createModuleItem(
                    () -> ExtraRegistration.LIGHTNING_ABSORPTION_MODULE, new Item.Properties()));

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MEKLTGT_TAB = CREATIVE_MODE_TABS.register("mekltgt_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.mekltgt"))
                    .withTabsBefore(CreativeModeTabs.COMBAT)
                    .icon(() -> new ItemStack(SUPER_ALLOY.get()))
                    .displayItems((parameters, output) -> {
                        output.accept(SUPER_ALLOY.get());
                        output.accept(SUPER_CORE.get());
                        output.accept(SUPER_PROBE_ITEM.get());
                        output.accept(LIGHTNING_GENERATOR_ITEM.get());
                        output.accept(LIGHTNING_ABSORPTION_ITEM.get());
                        output.accept(DRY_ICE_ITEM.get());
                        output.accept(THUNDERSTORM_ROCKET.get());
                        output.accept(ROCKET_LAUNCH_PLATFORM_ITEM.get());
                    }).build());

    public Mekltgt(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.COMMON, MekltgtConfig.SPEC, "Mekltgt.toml");

        BlockRegistryObject<LightningGeneratorBlock, LightningGeneratorItem> lgBlockRO = new BlockRegistryObject<>(LIGHTNING_GENERATOR_BLOCK, LIGHTNING_GENERATOR_ITEM);
        ExtraRegistration.initLightningGenerator(lgBlockRO);

        BlockRegistryObject<SuperProbeBlock, SuperProbeItem> spBlockRO = new BlockRegistryObject<>(SUPER_PROBE, SUPER_PROBE_ITEM);
        ExtraRegistration.initSuperProbe(spBlockRO);

        BlockRegistryObject<RocketLaunchPlatformBlock, RocketLaunchPlatformItem> rlpBlockRO = new BlockRegistryObject<>(ROCKET_LAUNCH_PLATFORM_BLOCK, ROCKET_LAUNCH_PLATFORM_ITEM);
        ExtraRegistration.initRocketLaunchPlatform(rlpBlockRO);

        BlockRegistryObject<DryIceBlock, DryIceItem> diBlockRO = new BlockRegistryObject<>(DRY_ICE_BLOCK, DRY_ICE_ITEM);
        ExtraRegistration.initDryIce(diBlockRO);

        ExtraRegistration.initLightningAbsorptionModule(LIGHTNING_ABSORPTION_ITEM);

        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        ExtraRegistration.register(modEventBus);

        // 注册桶的发射器行为（需延迟到注册完成后）
        modEventBus.addListener(net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent.class,
                e -> ExtraRegistration.FLUIDS.registerBucketDispenserBehavior());

        // 延迟到 RegisterCapabilitiesEvent 再注册容器和附件
        modEventBus.addListener(net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent.class,
                e -> {
                    LightningGeneratorItem lgItem = LIGHTNING_GENERATOR_ITEM.get();
                    lgItem.attachAttachments(modEventBus);
                    lgItem.attachCapabilities(e);

                    RocketLaunchPlatformItem rlpItem = ROCKET_LAUNCH_PLATFORM_ITEM.get();
                    rlpItem.attachAttachments(modEventBus);
                    rlpItem.attachCapabilities(e);
                });

        // 向Mekanism注册闪电吸收单元安装至MekaSuit头盔
        modEventBus.addListener(net.neoforged.fml.event.lifecycle.InterModEnqueueEvent.class,
                e -> mekanism.api.MekanismIMC.addMekaSuitHelmetModules(
                        ExtraRegistration.LIGHTNING_ABSORPTION_MODULE));
    }

    @EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void registerScreens(RegisterMenuScreensEvent event) {
            event.register(ExtraRegistration.LIGHTNING_GENERATOR_CONTAINER.get(), LightningGeneratorScreen::new);
            event.register(ExtraRegistration.ROCKET_LAUNCH_PLATFORM_CONTAINER.get(), RocketLaunchPlatformScreen::new);
        }

        @SubscribeEvent
        public static void clientSetup(FMLClientSetupEvent event) {
            event.enqueueWork(() -> {
                // 设置液态二氧化碳流体为半透明渲染层
                for (var fluid : ExtraRegistration.FLUIDS.getFluidEntries()) {
                    ItemBlockRenderTypes.setRenderLayer(fluid.value(), RenderType.translucent());
                }
            });
        }

        @SubscribeEvent
        public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
            ClientRegistrationUtil.registerFluidExtensions(event, ExtraRegistration.FLUIDS);
        }

        @SubscribeEvent
        public static void registerColorHandlers(RegisterColorHandlersEvent.Item event) {
            ClientRegistrationUtil.registerBucketColorHandler(event, ExtraRegistration.FLUIDS);
        }
    }
}
