package com.mekltgt.blockentity;

import com.mekltgt.Mekltgt;
import com.mekltgt.registries.ExtraRegistration;
import mekanism.api.Action;
import mekanism.api.AutomationType;
import mekanism.api.IContentsListener;
import mekanism.api.RelativeSide;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.capabilities.energy.BasicEnergyContainer;
import mekanism.common.capabilities.energy.MachineEnergyContainer;
import mekanism.common.capabilities.fluid.BasicFluidTank;
import mekanism.common.capabilities.fluid.VariableCapacityFluidTank;
import mekanism.common.capabilities.holder.energy.EnergyContainerHelper;
import mekanism.common.capabilities.holder.energy.IEnergyContainerHolder;
import mekanism.common.capabilities.holder.fluid.FluidTankHelper;
import mekanism.common.capabilities.holder.fluid.IFluidTankHolder;
import mekanism.common.capabilities.holder.slot.IInventorySlotHolder;
import mekanism.common.capabilities.holder.slot.InventorySlotHelper;
import mekanism.common.inventory.container.MekanismContainer;
import mekanism.common.inventory.container.slot.ContainerSlotType;
import mekanism.common.inventory.container.slot.SlotOverlay;
import mekanism.common.inventory.container.sync.SyncableBoolean;
import mekanism.common.inventory.container.sync.SyncableLong;
import mekanism.common.inventory.slot.BasicInventorySlot;
import mekanism.common.integration.energy.BlockEnergyCapabilityCache;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.tile.interfaces.IBoundingBlock;
import mekanism.common.util.CableUtils;
import mekanism.common.util.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.capabilities.BlockCapability;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 大型闪电发电机：消耗液态二氧化碳，利用闪电产生电力。
 */
public class LargeLightningGeneratorBlockEntity extends TileEntityMekanism implements IBoundingBlock {

    public static final int MAX_FLUID = 100_000;              // 100B 流体容量
    public static final long MAX_ENERGY = 52_500_000_000L;    // 21 GFE 电力存储（21G FE × 2.5 = 52.5G J）
    private static final long PER_LIGHTNING = 1_000_000_000L; // 400 MFE 每闪电（400M FE × 2.5 = 1G J）

    private BasicFluidTank co2Tank;
    private BasicEnergyContainer energyContainer;
    private BasicInventorySlot ionizationUpgradeSlot;
    private boolean obstructed = false;
    private long producingEnergy = 0;

    @Nullable
    private List<BlockEnergyCapabilityCache> outputCaches;

    public LargeLightningGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(ExtraRegistration.LARGE_LIGHTNING_GENERATOR, pos, state);
    }

    @NotNull
    @Override
    protected IEnergyContainerHolder getInitialEnergyContainers(IContentsListener listener) {
        EnergyContainerHelper builder = EnergyContainerHelper.forSide(facingSupplier);
        builder.addContainer(energyContainer = BasicEnergyContainer.output(
                MachineEnergyContainer.validateBlock(this).getStorage(), listener), RelativeSide.BACK);
        return builder.build();
    }

    @NotNull
    @Override
    protected IFluidTankHolder getInitialFluidTanks(IContentsListener listener) {
        FluidTankHelper builder = FluidTankHelper.forSide(facingSupplier);
        builder.addTank(co2Tank = VariableCapacityFluidTank.input(MAX_FLUID,
                fluid -> fluid.getFluid() == ExtraRegistration.LIQUID_CARBON_DIOXIDE.get(), listener), RelativeSide.BACK);
        return builder.build();
    }

    @NotNull
    @Override
    protected IInventorySlotHolder getInitialInventory(IContentsListener listener) {
        InventorySlotHelper helper = InventorySlotHelper.forSide(facingSupplier);
        ionizationUpgradeSlot = BasicInventorySlot.at(
                stack -> stack.getItem() == Mekltgt.IONIZATION_CORE.get(),
                stack -> stack.getItem() == Mekltgt.IONIZATION_CORE.get(),
                listener, 25, 18);
        ionizationUpgradeSlot.setSlotOverlay(SlotOverlay.UPGRADE);
        ionizationUpgradeSlot.setSlotType(ContainerSlotType.NORMAL);
        helper.addSlot(ionizationUpgradeSlot);
        return helper.build();
    }

    @Override
    protected boolean onUpdateServer() {
        boolean sendUpdatePacket = super.onUpdateServer();

        if (ticker % 20 == 0) {
            boolean newObstructed = checkObstructed();
            if (newObstructed != obstructed) {
                obstructed = newObstructed;
                sendUpdatePacket = true;
            }
        }

        // 发电逻辑：未被遮挡且有二氧化碳
        producingEnergy = 0;
        boolean working = !obstructed && co2Tank != null && co2Tank.getFluidAmount() > 0;
        if (working) {
            int lightningCount = countLightning();
            if (lightningCount > 0) {
                double co2Ratio = (double) co2Tank.getFluidAmount() / MAX_FLUID; // 0.0 到 1.0
                long production = (long) (lightningCount * PER_LIGHTNING * co2Ratio);
                if (production > 0) {
                    // 消耗二氧化碳：按产电比例消耗（每 400MFE 消耗 1B 二氧化碳）
                    int consume = (int) Math.max(1, production / PER_LIGHTNING * 1000);
                    co2Tank.extract(consume, Action.EXECUTE, AutomationType.INTERNAL);
                    energyContainer.insert(production, Action.EXECUTE, AutomationType.INTERNAL);
                    producingEnergy = production;
                    setChanged();
                }
            }
        }
        // 工作状态切换（CO2 + 无遮挡 → 工作）
        setActive(working);

        // 电离核心：仅在机器工作状态下，每 tick 在上方 3 格处召唤安装数道闪电
        if (working && ionizationUpgradeSlot != null && level instanceof ServerLevel serverLevel) {
            int upgradeCount = ionizationUpgradeSlot.getCount();
            for (int i = 0; i < upgradeCount; i++) {
                spawnIonizationLightning(serverLevel);
            }
        }

        // 主动向背面中层弹出电力
        if (level != null && !isRemote() && energyContainer != null && energyContainer.getEnergy() > 0) {
            if (outputCaches == null && level instanceof ServerLevel serverLevel) {
                Direction back = getOppositeDirection();
                outputCaches = new ArrayList<>(1);
                BlockPos ejectPos = worldPosition.offset(back.getStepX(), 1, back.getStepZ()).relative(back);
                outputCaches.add(BlockEnergyCapabilityCache.create(serverLevel, ejectPos, back.getOpposite()));
            }
            if (outputCaches != null) {
                CableUtils.emit(outputCaches, energyContainer, Long.MAX_VALUE);
            }
        }

        return sendUpdatePacket;
    }

    private boolean checkObstructed() {
        if (level == null) return false;
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                for (int y = 3; y <= 100; y++) { // 从机器顶部（y=2 之上）开始检查
                    mutable.set(worldPosition.getX() + x, worldPosition.getY() + y, worldPosition.getZ() + z);
                    if (!level.getBlockState(mutable).isAir()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private int countLightning() {
        if (!(level instanceof ServerLevel)) return 0;
        AABB area = new AABB(
                worldPosition.getX() - 8, worldPosition.getY(), worldPosition.getZ() - 8,
                worldPosition.getX() + 8, worldPosition.getY() + 16, worldPosition.getZ() + 8);
        return level.getEntitiesOfClass(LightningBolt.class, area).size();
    }

    private void spawnIonizationLightning(ServerLevel level) {
        BlockPos target = worldPosition.above(3);
        LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(level);
        if (bolt != null) {
            bolt.setPos(target.getX() + 0.5, target.getY(), target.getZ() + 0.5);
            level.addFreshEntity(bolt);
        }
    }

    public boolean isObstructed() {
        return obstructed;
    }

    public BasicFluidTank getCo2Tank() {
        return co2Tank;
    }

    public BasicEnergyContainer getEnergyContainer() {
        return energyContainer;
    }

    public long getEnergyStoredLong() {
        return energyContainer == null ? 0 : energyContainer.getEnergy();
    }

    public long getProductionRate() {
        return producingEnergy;
    }

    @Override
    public void addContainerTrackers(MekanismContainer container) {
        super.addContainerTrackers(container);
        container.track(SyncableBoolean.create(this::isObstructed, value -> obstructed = value));
        container.track(SyncableLong.create(this::getProductionRate, value -> producingEnergy = value));
    }

    @Nullable
    @Override
    public <T> T getOffsetCapabilityIfEnabled(@NotNull BlockCapability<T, @Nullable Direction> capability, @Nullable Direction side, @NotNull Vec3i offset) {
        if (capability == Capabilities.ENERGY.block()) {
            return Objects.requireNonNull(energyHandlerManager, "Expected to have energy handler").resolve(capability, side);
        } else if (capability == Capabilities.FLUID.block()) {
            return Objects.requireNonNull(fluidHandlerManager, "Expected to have fluid handler").resolve(capability, side);
        }
        return WorldUtils.getCapability(level, capability, worldPosition, null, this, side);
    }

    @Override
    public boolean isOffsetCapabilityDisabled(@NotNull BlockCapability<?, @Nullable Direction> capability, Direction side, @NotNull Vec3i offset) {
        if (capability == Capabilities.ENERGY.block()) {
            return notEnergyPort(side, offset);
        } else if (capability == Capabilities.FLUID.block()) {
            return notFluidPort(side, offset);
        }
        return true;
    }

    private boolean notEnergyPort(Direction side, Vec3i offset) {
        Direction back = getOppositeDirection();
        // 电力输出：背面中层（y=1）
        if (offset.equals(new Vec3i(back.getStepX(), 1, back.getStepZ()))) {
            return side != back;
        }
        return true;
    }

    private boolean notFluidPort(Direction side, Vec3i offset) {
        Direction back = getOppositeDirection();
        // 流体输入：背面下层（y=0，后面中间下面）
        if (offset.equals(new Vec3i(back.getStepX(), 0, back.getStepZ()))) {
            return side != back;
        }
        return true;
    }
}