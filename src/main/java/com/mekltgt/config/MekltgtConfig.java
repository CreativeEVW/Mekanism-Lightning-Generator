package com.mekltgt.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class MekltgtConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.LongValue SINGLE_STRIKE_MAX_ENERGY;
    public static final ModConfigSpec.BooleanValue SINGLE_STRIKE_CACHE_MODE;
    public static final ModConfigSpec.IntValue LARGE_GENERATOR_POWER_MULTIPLIER;
    public static final ModConfigSpec.IntValue LARGE_GENERATOR_CO2_MULTIPLIER;
    public static final ModConfigSpec.IntValue LARGE_GENERATOR_ENERGY_MULTIPLIER;
    public static final ModConfigSpec.IntValue LARGE_GENERATOR_FLUID_MULTIPLIER;

    public static final ModConfigSpec SPEC;

    static {
        BUILDER.comment("Mekanism Lightning Generator Configuration");

        SINGLE_STRIKE_MAX_ENERGY = BUILDER
                .comment("单次最大产电量 (FE，游戏内单位)。默认 20,000,000 (20MFE)。")
                .defineInRange("singleStrikeMaxEnergy", 20_000_000L, 1L, Long.MAX_VALUE);

        SINGLE_STRIKE_CACHE_MODE = BUILDER
                .comment("单次充能缓存模式。开启后，闪电发电机和大型闪电发电机被闪电激发时充能全部缓存。默认 false。")
                .define("singleStrikeCacheMode", false);

        LARGE_GENERATOR_POWER_MULTIPLIER = BUILDER
                .comment("大型闪电发电机发电倍率 (x0.001，1000 = 1.0x)。默认 1000。")
                .defineInRange("largeGeneratorPowerMultiplier", 1000, 1, Integer.MAX_VALUE);

        LARGE_GENERATOR_CO2_MULTIPLIER = BUILDER
                .comment("大型闪电发电机液态二氧化碳消耗倍率 (x0.1，10 = 1.0x)。默认 10。")
                .defineInRange("largeGeneratorCo2Multiplier", 10, 1, Integer.MAX_VALUE);

        LARGE_GENERATOR_ENERGY_MULTIPLIER = BUILDER
                .comment("大型闪电发电机电容倍率 (x1)。默认 1。")
                .defineInRange("largeGeneratorEnergyMultiplier", 1, 1, Integer.MAX_VALUE);

        LARGE_GENERATOR_FLUID_MULTIPLIER = BUILDER
                .comment("大型闪电发电机液容倍率 (x1)。默认 1。")
                .defineInRange("largeGeneratorFluidMultiplier", 1, 1, Integer.MAX_VALUE);

        SPEC = BUILDER.build();
    }
}
