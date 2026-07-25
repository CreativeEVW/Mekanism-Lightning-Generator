package com.mekltgt.blockentity;

import com.mekltgt.Mekltgt;
import com.mekltgt.registries.ExtraRegistration;
import mekanism.api.IContentsListener;
import mekanism.api.RelativeSide;
import mekanism.common.capabilities.energy.BasicEnergyContainer;
import mekanism.common.capabilities.energy.MachineEnergyContainer;
import mekanism.common.capabilities.holder.energy.EnergyContainerHelper;
import mekanism.common.capabilities.holder.energy.IEnergyContainerHolder;
import mekanism.common.capabilities.holder.slot.IInventorySlotHolder;
import mekanism.common.capabilities.holder.slot.InventorySlotHelper;
import mekanism.common.inventory.container.MekanismContainer;
import mekanism.common.inventory.container.sync.SyncableBoolean;
import mekanism.common.inventory.container.sync.SyncableInt;
import mekanism.common.inventory.slot.BasicInventorySlot;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.api.Upgrade;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

public class RocketLaunchPlatformBlockEntity extends TileEntityMekanism {

    public static final long MAX_ENERGY = 16_000_000L;
    private static final int COOLDOWN_TICKS = 1200; // 60秒冷却
    private static final int ACTIVE_TICKS = 1200;    // 60秒活跃
    private static final int LIGHTNING_RANGE = 16;

    private BasicEnergyContainer energyContainer;
    private BasicInventorySlot rocketSlot;

    private int cooldownTicks = 0;
    private int activeTicks = 0;
    private int launchDelay = 0;
    private int particleTicks = 0;     // 粒子持续时间（5秒）
    private int lightningInterval = 20;
    private boolean hasSkyAccess = false;

    public RocketLaunchPlatformBlockEntity(BlockPos pos, BlockState state) {
        super(ExtraRegistration.ROCKET_LAUNCH_PLATFORM, pos, state);
    }

    @NotNull
    @Override
    protected IEnergyContainerHolder getInitialEnergyContainers(IContentsListener listener) {
        EnergyContainerHelper builder = EnergyContainerHelper.forSide(facingSupplier);
        builder.addContainer(energyContainer = BasicEnergyContainer.input(
                MachineEnergyContainer.validateBlock(this).getStorage(), listener),
                RelativeSide.BOTTOM);
        return builder.build();
    }

    @NotNull
    @Override
    protected IInventorySlotHolder getInitialInventory(IContentsListener listener) {
        InventorySlotHelper helper = InventorySlotHelper.forSide(facingSupplier);
        rocketSlot = BasicInventorySlot.at(
                stack -> false,
                stack -> stack.getItem() == Mekltgt.THUNDERSTORM_ROCKET.get(),
                listener, 25, 31);
        rocketSlot.setSlotType(mekanism.common.inventory.container.slot.ContainerSlotType.NORMAL);
        helper.addSlot(rocketSlot, RelativeSide.LEFT, RelativeSide.RIGHT, RelativeSide.FRONT, RelativeSide.BACK);
        return helper.build();
    }

    @Override
    protected boolean onUpdateServer() {
        boolean sendUpdatePacket = super.onUpdateServer();

        // 检查顶部无遮挡
        if (ticker % 20 == 0) {
            boolean newSkyAccess = level != null && level.canSeeSky(worldPosition.above());
            if (newSkyAccess != hasSkyAccess) {
                hasSkyAccess = newSkyAccess;
                sendUpdatePacket = true;
            }
        }

        // 发射延迟
        if (launchDelay > 0) {
            launchDelay--;
        }

        // 粒子效果（仅发射后5秒）
        if (particleTicks > 0) {
            particleTicks--;
            if (level instanceof ServerLevel serverLevel) {
                spawnLaunchParticles(serverLevel);
            }
        }

        // 冷却倒计时
        if (cooldownTicks > 0) {
            cooldownTicks--;
            if (cooldownTicks == 0) {
                sendUpdatePacket = true;
            }
        }

        // 雷暴活跃期间生成闪电
        if (activeTicks > 0) {
            activeTicks--;
            if (level instanceof ServerLevel serverLevel && hasSkyAccess) {
                if (activeTicks % lightningInterval == 0) {
                    spawnLightning(serverLevel);
                }
            }
            if (activeTicks == 0) {
                cooldownTicks = COOLDOWN_TICKS;
                sendUpdatePacket = true;
            }
        }

        // 自动发射：有火箭弹、无遮挡、非延迟、非冷却、非活跃、电量≥50%
        if (hasSkyAccess && launchDelay == 0 && cooldownTicks == 0 && activeTicks == 0
                && rocketSlot != null && !rocketSlot.isEmpty()
                && energyContainer != null && energyContainer.getEnergy() >= energyContainer.getMaxEnergy() / 2) {
            consumeRocket();
            sendUpdatePacket = true;
        }

        return sendUpdatePacket;
    }

    private void consumeRocket() {
        if (level == null || level.isClientSide) return;
        ItemStack stack = rocketSlot.getStack();
        if (stack.isEmpty() || energyContainer == null) return;

        // 消耗一半缓存电量
        long halfEnergy = energyContainer.getMaxEnergy() / 2;
        if (energyContainer.getEnergy() < halfEnergy) return;

        stack.shrink(1);
        energyContainer.extract(halfEnergy, mekanism.api.Action.EXECUTE, mekanism.api.AutomationType.INTERNAL);
        particleTicks = 100;
        setChanged();

        // 发射音效（有降噪升级则跳过）
        if (upgradeComponent == null || upgradeComponent.getUpgrades(Upgrade.MUFFLING) == 0) {
            level.playSound(null, worldPosition, SoundEvents.FIREWORK_ROCKET_LAUNCH, SoundSource.BLOCKS, 1.0F, 1.0F);
        }

        if (level.isThundering()) {
            // 已有雷暴：生成闪电60秒，之后冷却
            activeTicks = ACTIVE_TICKS;
            lightningInterval = Math.max(1, 20 / (1 + level.random.nextInt(3)));
        } else {
            // 无雷暴：切换天气为雷暴，短暂延迟防连发
            if (level instanceof ServerLevel serverLevel) {
                serverLevel.setWeatherParameters(0, 6000, true, true);
            }
            launchDelay = 100; // 5秒最小发射间隔
        }
    }

    private void spawnLaunchParticles(ServerLevel level) {
        double cx = worldPosition.getX() + 0.5;
        double cz = worldPosition.getZ() + 0.5;
        for (int y = worldPosition.getY() + 1; y <= worldPosition.getY() + 20; y++) {
            double offsetX = level.random.nextDouble() * 0.5 - 0.25;
            double offsetZ = level.random.nextDouble() * 0.5 - 0.25;
            // 烟雾
            level.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE,
                    cx + offsetX, y + level.random.nextDouble() * 0.5, cz + offsetZ,
                    1, 0, 0.05, 0, 0.02);
            // 火焰（XZ范围减半）
            level.sendParticles(ParticleTypes.FLAME,
                    cx + offsetX * 0.5, y + level.random.nextDouble() * 0.5, cz + offsetZ * 0.5,
                    1, 0, 0.02, 0, 0.01);
        }
    }

    private void spawnLightning(ServerLevel level) {
        BlockPos target = findLightningTarget();
        LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(level);
        if (bolt != null) {
            bolt.setPos(target.getX() + 0.5, target.getY(), target.getZ() + 0.5);
            level.addFreshEntity(bolt);
        }
    }

    private BlockPos findLightningTarget() {
        // 优先打击范围内的超导探头
        for (int dx = -LIGHTNING_RANGE; dx <= LIGHTNING_RANGE; dx++) {
            for (int dy = -LIGHTNING_RANGE; dy <= LIGHTNING_RANGE; dy++) {
                for (int dz = -LIGHTNING_RANGE; dz <= LIGHTNING_RANGE; dz++) {
                    BlockPos pos = worldPosition.offset(dx, dy, dz);
                    if (level != null && level.getBlockState(pos).getBlock() == Mekltgt.SUPER_PROBE.get()) {
                        return pos;
                    }
                }
            }
        }
        // 无探头则随机位置
        int x = worldPosition.getX() + level.random.nextInt(LIGHTNING_RANGE * 2 + 1) - LIGHTNING_RANGE;
        int z = worldPosition.getZ() + level.random.nextInt(LIGHTNING_RANGE * 2 + 1) - LIGHTNING_RANGE;
        BlockPos topPos = level.getHeightmapPos(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING,
                new BlockPos(x, worldPosition.getY(), z));
        return topPos;
    }

    public boolean isCoolingDown() {
        return cooldownTicks > 0;
    }

    public boolean isActive() {
        return activeTicks > 0;
    }

    public boolean hasSkyAccess() {
        return hasSkyAccess;
    }

    public int getCooldownTicks() {
        return cooldownTicks;
    }

    public int getActiveTicks() {
        return activeTicks;
    }

    @Override
    public void addContainerTrackers(MekanismContainer container) {
        super.addContainerTrackers(container);
        container.track(SyncableBoolean.create(this::hasSkyAccess, value -> hasSkyAccess = value));
        container.track(SyncableInt.create(this::getCooldownTicks, value -> cooldownTicks = value));
        container.track(SyncableInt.create(this::getActiveTicks, value -> activeTicks = value));
    }

    public BasicEnergyContainer getEnergyContainer() {
        return energyContainer;
    }

    public long getEnergyStoredLong() {
        return energyContainer == null ? 0 : energyContainer.getEnergy();
    }
}