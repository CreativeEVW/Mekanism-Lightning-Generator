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

    @SubscribeEvent
    public static void onLightningStrike(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide() || !(event.getEntity() instanceof LightningBolt bolt)) {
            return;
        }
        Level level = event.getLevel();
        BlockPos boltPos = bolt.blockPosition();

        // 闪电发电机
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
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
