package dev.genshinecraft.forge;

import net.minecraftforge.common.ForgeConfigSpec;

/** Forge client TOML is picked up automatically by Configured. */
public final class GenshinecraftForgeClientConfig {
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.BooleanValue DAMAGE_NUMBERS;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        DAMAGE_NUMBERS = builder
                .comment("Display floating damage numbers and critical markers.")
                .translation("config.genshinecraft.damage_numbers")
                .define("damageNumbers", true);
        SPEC = builder.build();
    }

    private GenshinecraftForgeClientConfig() {
    }
}
