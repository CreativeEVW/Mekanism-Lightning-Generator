package com.mekltgt.blockentity;

import com.mekltgt.config.MekltgtConfig;
import com.mekltgt.registries.ExtraRegistration;
import mekanism.api.Action;
import mekanism.api.AutomationType;
import mekanism.api.IContentsListener;
import mekanism.api.Upgrade;
import mekanism.api.energy.IEnergyContainer;
import mekanism.api.energy.IMekanismStrictEnergyHandler;
import mekanism.common.attachments.containers.ContainerType;
import mekanism.common.capabilities.holder.energy.IEnergyContainerHolder;
import mekanism.common.capabilities.holder.slot.IInventorySlotHolder;
import mekanism.common.capabilities.holder.slot.InventorySlotHelper;
import mekanism.common.inventory.container.MekanismContainer;
import mekanism.common.inventory.container.sync.SyncableBoolean;
import mekanism.common.inventory.container.sync.SyncableLong;
import mekanism.common.inventory.slot.BasicInventorySlot;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.tile.component.ITileComponent;
import mekanism.common.upgrade.IUpgradeData;
import mekanism.common.util.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class LightningGeneratorBlockEntity extends TileEntityMekanism {

    public static final long MAX_ENERGY = 16_000_000L; // 16 MFE 基础容量
    private long energy = 0;
    private boolean probeValid = false;

    // 5秒滚动平均产电量追踪
    private long totalEnergyProduced = 0;
    private long lastTickTotalProduced = 0;
    private final long[] productionHistory = new long[100];
    private int productionHistoryIndex = 0;
    private long productionHistorySum = 0;
    private long averageProduction = 0;

    // 充电槽
    private BasicInventorySlot energySlot;

    private int getEnergyUpgradeCount() {
        return upgradeComponent != null ? upgradeComponent.getUpgrades(Upgrade.ENERGY) : 0;
    }

    public long getCurrentMaxEnergy() {
        return MAX_ENERGY << getEnergyUpgradeCount();
    }

    private final IEnergyContainer energyContainer = new IEnergyContainer() {
        @Override public long getEnergy() { return energy; }
        @Override public void setEnergy(long e) {
            energy = Math.min(e, getCurrentMaxEnergy());
            setChanged();
        }
        @Override public long getMaxEnergy() { return getCurrentMaxEnergy(); }
        @Override public void onContentsChanged() {}

        @Override public CompoundTag serializeNBT(HolderLookup.Provider provider) {
            CompoundTag tag = new CompoundTag();
            tag.putLong("energy", energy);
            return tag;
        }

        @Override public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
            energy = nbt.getLong("energy");
        }

        @Override public long insert(long amount, Action action, AutomationType automationType) {
            return amount; // 禁止输入
        }
    };

    public LightningGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(ExtraRegistration.LIGHTNING_GENERATOR, pos, state);
    }

    @NotNull
    @Override
    protected IEnergyContainerHolder getInitialEnergyContainers(IContentsListener listener) {
        return side -> (side == null || side == Direction.DOWN) ? List.of(energyContainer) : Collections.emptyList();
    }

    @NotNull
    @Override
    protected IInventorySlotHolder getInitialInventory(IContentsListener listener) {
        InventorySlotHelper helper = InventorySlotHelper.forSide(facingSupplier);
        energySlot = BasicInventorySlot.at(
                (stack, type) -> true,
                (stack, type) -> true,
                listener, 143, 35);
        energySlot.setSlotOverlay(mekanism.common.inventory.container.slot.SlotOverlay.POWER);
        energySlot.setSlotType(mekanism.common.inventory.container.slot.ContainerSlotType.POWER);
        helper.addSlot(energySlot);
        return helper.build();
    }

    public boolean persistsToItem(ContainerType<?, ?, ?> type) {
        return true;
    }

    @NotNull
    @Override
    public IUpgradeData getUpgradeData(HolderLookup.Provider provider) {
        return new LightningUpgradeData(energy, redstone, getControlType(), getComponents(), provider);
    }

    @Override
    public void parseUpgradeData(HolderLookup.Provider provider, @NotNull IUpgradeData upgradeData) {
        if (upgradeData instanceof LightningUpgradeData data) {
            energy = data.energy;
            redstone = data.redstone;
            setControlType(data.controlType);
            for (ITileComponent component : getComponents()) {
                component.read(data.components, provider);
            }
        } else {
            super.parseUpgradeData(provider, upgradeData);
        }
    }

    private boolean checkProbe() {
        if (level == null) return false;
        BlockPos probePos = worldPosition.above();
        BlockState probeState = level.getBlockState(probePos);
        if (probeState.getBlock() != com.mekltgt.Mekltgt.SUPER_PROBE.get()) {
            return false;
        }
        BlockPos aboveProbe = probePos.above();
        return level.isEmptyBlock(aboveProbe);
    }

    @Override
    protected boolean onUpdateServer() {
        boolean sendUpdatePacket = super.onUpdateServer();

        if (ticker % 20 == 0) {
            boolean newProbeValid = checkProbe();
            if (newProbeValid != probeValid) {
                probeValid = newProbeValid;
                sendUpdatePacket = true;
            }
        }

        // 更新5秒滚动平均产电量
        long producedThisTick = totalEnergyProduced - lastTickTotalProduced;
        lastTickTotalProduced = totalEnergyProduced;
        productionHistorySum -= productionHistory[productionHistoryIndex];
        productionHistory[productionHistoryIndex] = producedThisTick;
        productionHistorySum += producedThisTick;
        productionHistoryIndex = (productionHistoryIndex + 1) % 100;
        averageProduction = productionHistorySum / 100;

        // 充电槽
        if (energySlot != null && !energySlot.isEmpty() && energy > 0) {
            chargeItem(energySlot.getStack());
        }

        // 每tick自动向底部弹出能量
        if (level != null && !isRemote() && energyContainer.getEnergy() > 0) {
            BlockEntity be = WorldUtils.getTileEntity(level, worldPosition.below());
            if (be instanceof IMekanismStrictEnergyHandler handler) {
                long toSend = energyContainer.getEnergy();
                long remainder = handler.insertEnergy(toSend, Direction.UP, Action.EXECUTE);
                long sent = toSend - remainder;
                if (sent > 0) {
                    energyContainer.extract(sent, Action.EXECUTE, AutomationType.INTERNAL);
                }
            }
        }

        return sendUpdatePacket;
    }

    private void chargeItem(ItemStack stack) {
        if (stack.isEmpty()) return;
        mekanism.api.energy.IStrictEnergyHandler handler = mekanism.common.integration.energy.EnergyCompatUtils
                .getStrictEnergyHandler(stack);
        if (handler != null && energyContainer.getEnergy() > 0) {
            long needed = handler.getMaxEnergy(0) - handler.getEnergy(0);
            if (needed > 0) {
                long toTransfer = Math.min(needed, energyContainer.getEnergy());
                long remainder = handler.insertEnergy(0, toTransfer, Action.EXECUTE);
                long transferred = toTransfer - remainder;
                if (transferred > 0) {
                    energyContainer.extract(transferred, Action.EXECUTE, AutomationType.INTERNAL);
                }
            }
        }
    }

    public void onLightningStrike() {
        if (level == null || level.isClientSide) return;
        if (!probeValid) return;

        long currentMax = getCurrentMaxEnergy();
        if (energy < currentMax) {
            long gained = currentMax - energy;
            long maxCharge = MekltgtConfig.LIGHTNING_MAX_CHARGE.get();
            if (maxCharge > 0 && gained > maxCharge) {
                gained = maxCharge;
            }
            energy += gained;
            totalEnergyProduced += gained;
            setChanged();
        } else {
            BlockPos probePos = worldPosition.above();
            BlockState probeState = level.getBlockState(probePos);
            if (probeState.getBlock() == com.mekltgt.Mekltgt.SUPER_PROBE.get()) {
                level.destroyBlock(probePos, true);
            }
        }
    }

    public boolean isProbeValid() {
        return probeValid;
    }

    public long getAverageProduction() {
        return averageProduction;
    }

    @Override
    public void addContainerTrackers(MekanismContainer container) {
        super.addContainerTrackers(container);
        container.track(SyncableBoolean.create(this::isProbeValid, value -> probeValid = value));
        container.track(SyncableLong.create(this::getAverageProduction, value -> averageProduction = value));
    }

    public static class LightningUpgradeData implements IUpgradeData {
        public final long energy;
        public final boolean redstone;
        public final RedstoneControl controlType;
        public final CompoundTag components;

        public LightningUpgradeData(long energy, boolean redstone, RedstoneControl controlType, List<ITileComponent> components, HolderLookup.Provider provider) {
            this.energy = energy;
            this.redstone = redstone;
            this.controlType = controlType;
            this.components = new CompoundTag();
            for (ITileComponent component : components) {
                component.write(this.components, provider);
            }
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (!(obj instanceof LightningUpgradeData other)) return false;
            return energy == other.energy && redstone == other.redstone && controlType == other.controlType && components.equals(other.components);
        }

        @Override
        public int hashCode() {
            return Objects.hash(energy, redstone, controlType, components);
        }
    }

    public IEnergyContainer getEnergyContainer() { return energyContainer; }
    public long getEnergyStoredLong() { return energy; }

    @Override public void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putLong("Energy", energy);
        tag.putLong("TotalEnergyProduced", totalEnergyProduced);
    }

    @Override public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        energy = tag.getLong("Energy");
        totalEnergyProduced = tag.getLong("TotalEnergyProduced");
        lastTickTotalProduced = totalEnergyProduced;
    }
}
