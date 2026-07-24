package com.mekltgt.event;

import com.mekltgt.Mekltgt;
import com.mekltgt.blockentity.LightningGeneratorBlockEntity;
import com.mekltgt.registries.ExtraRegistration;
import mekanism.api.Action;
import mekanism.api.energy.IStrictEnergyHandler;
import mekanism.api.gear.IModule;
import mekanism.api.gear.IModuleContainer;
import mekanism.api.gear.IModuleHelper;
import mekanism.common.capabilities.Capabilities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

import java.util.List;

@EventBusSubscriber(modid = Mekltgt.MODID)
public class CommonEventHandlers {

    /** 超导探针作为避雷针时吸引闪电的搜索范围（格） */
    private static final int PROBE_ATTRACT_RANGE = 32;

    @SubscribeEvent
    public static void onLightningStrike(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide() || !(event.getEntity() instanceof LightningBolt bolt)) {
            return;
        }
        Level level = event.getLevel();
        BlockPos boltPos = bolt.blockPosition();

        // 超导探针避雷针特性：仅在雷雨天将闪电吸引至最近的探针
        if (level.isThundering()) {
            BlockPos probePos = findNearestProbe(level, boltPos);
            if (probePos != null) {
                bolt.setPos(probePos.getX() + 0.5, probePos.getY(), probePos.getZ() + 0.5);
                boltPos = probePos;
            }
        }

        // 闪电发电机（向上扩展2格）
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 3; y++) {
                for (int z = -1; z <= 1; z++) {
                    if (level.getBlockEntity(boltPos.offset(x, y, z))
                            instanceof LightningGeneratorBlockEntity gen && gen.isProbeValid()) {
                        gen.onLightningStrike();
                        bolt.discard();
                        return;
                    }
                }
            }
        }

        // 闪电吸收单元：3 格内 MekaSuit 头盔
        AABB area = new AABB(boltPos).inflate(3);
        for (ServerPlayer player : level.getEntitiesOfClass(ServerPlayer.class, area)) {
            ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
            IModuleContainer container = IModuleHelper.INSTANCE.getModuleContainer(helmet);
            if (container != null) {
                IModule<?> module = container.get(ExtraRegistration.LIGHTNING_ABSORPTION_MODULE);
                if (module != null && module.getInstalledCount() > 0 && module.isEnabled()) {
                    bolt.discard();
                    // 充能头盔
                    fillEnergy(helmet);
                    // 充能所有护甲
                    for (EquipmentSlot slot : EquipmentSlot.values()) {
                        if (slot.getType() != EquipmentSlot.Type.HUMANOID_ARMOR || slot == EquipmentSlot.HEAD)
                            continue;
                        fillEnergy(player.getItemBySlot(slot));
                    }
                    return;
                }
            }
        }
    }

    /** 搜索范围内最近的超导探针 */
    private static BlockPos findNearestProbe(Level level, BlockPos center) {
        BlockPos nearest = null;
        double nearestDistSq = Double.MAX_VALUE;

        for (int dx = -PROBE_ATTRACT_RANGE; dx <= PROBE_ATTRACT_RANGE; dx++) {
            for (int dy = -PROBE_ATTRACT_RANGE; dy <= PROBE_ATTRACT_RANGE; dy++) {
                for (int dz = -PROBE_ATTRACT_RANGE; dz <= PROBE_ATTRACT_RANGE; dz++) {
                    BlockPos pos = center.offset(dx, dy, dz);
                    if (level.getBlockState(pos).getBlock() == Mekltgt.SUPER_PROBE.get()) {
                        double distSq = center.distSqr(pos);
                        if (distSq < nearestDistSq) {
                            nearestDistSq = distSq;
                            nearest = pos;
                        }
                    }
                }
            }
        }
        return nearest;
    }

    private static void fillEnergy(ItemStack stack) {
        IStrictEnergyHandler handler = Capabilities.STRICT_ENERGY.getCapability(stack);
        if (handler != null) {
            int count = handler.getEnergyContainerCount();
            for (int i = 0; i < count; i++) {
                long space = handler.getMaxEnergy(i) - handler.getEnergy(i);
                if (space > 0) {
                    handler.insertEnergy(i, space * 10, Action.EXECUTE);
                }
            }
        }
    }
}
