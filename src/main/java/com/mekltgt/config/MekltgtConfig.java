package com.mekltgt.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class MekltgtConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.LongValue SINGLE_STRIKE_MAX_ENERGY;

    public static final ModConfigSpec SPEC;

    static {
        BUILDER.comment("Mekanism Lightning Generator Configuration");

        SINGLE_STRIKE_MAX_ENERGY = BUILDER
                .comment("单次最大产电量 (FE，游戏内单位)。默认 20,000,000 (20MFE)。")
                .defineInRange("singleStrikeMaxEnergy", 20_000_000L, 1L, Long.MAX_VALUE);

        SPEC = BUILDER.build();
    }
}
