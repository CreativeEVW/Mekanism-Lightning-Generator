package com.mekltgt.compat;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.fml.ModList;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * mekltgt 与 AE2LT 跨模组联动 — 过载探头上方引雷后触发下方收集器。
 * 通过反射调用，无硬依赖。
 */
public class Ae2ltCompat {
    private static final boolean LOADED = ModList.get().isLoaded("ae2lt");
    private static final String COLLECTOR = "com.moakiee.ae2lt.blockentity.LightningCollectorBlockEntity";

    public static boolean tryCapture(Level level, BlockPos probePos) {
        if (!LOADED || level.isClientSide) return false;

        BlockPos below = probePos.below();
        BlockEntity be = level.getBlockEntity(below);
        if (be == null || !COLLECTOR.equals(be.getClass().getName())) return false;

        try {
            Method canCapture = be.getClass().getMethod("canCaptureLightning");
            if (!(boolean) canCapture.invoke(be)) return false;

            Method capture = be.getClass().getMethod("captureLightning", boolean.class);
            Field cooldownField = be.getClass().getDeclaredField("cooldownTicks");
            Field lastCaptureField = be.getClass().getDeclaredField("lastCaptureGameTime");
            cooldownField.setAccessible(true);
            lastCaptureField.setAccessible(true);

            // 先捕获极高压（EHV）
            boolean first = (boolean) capture.invoke(be, true);

            // 重置冷却，再捕获高压（HV）
            cooldownField.set(be, 0);
            lastCaptureField.set(be, Long.MIN_VALUE);
            boolean second = (boolean) capture.invoke(be, false);

            return first || second;
        } catch (Exception e) {
            return false;
        }
    }
}