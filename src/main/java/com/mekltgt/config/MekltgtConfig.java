package com.mekltgt.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class MekltgtConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.LongValue LIGHTNING_MAX_CHARGE;

    public static final ModConfigSpec SPEC;

    static {
        BUILDER.comment("Mekanism Lightning Generator Configuration");

        LIGHTNING_MAX_CHARGE = BUILDER
                .comment("Maximum energy gained per lightning strike (FE). Set to 0 to disable the cap (generator fills to full capacity on each strike).")
                .defineInRange("lightningMaxCharge", 0L, 0L, Long.MAX_VALUE);

        SPEC = BUILDER.build();
    }
}
