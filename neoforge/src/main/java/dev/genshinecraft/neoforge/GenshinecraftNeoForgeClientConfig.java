package dev.genshinecraft.neoforge;

import net.neoforged.neoforge.common.ModConfigSpec;

/** Native NeoForge client configuration, shown by ConfigurationScreen. */
public final class GenshinecraftNeoForgeClientConfig {
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.BooleanValue DAMAGE_NUMBERS;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        DAMAGE_NUMBERS = builder
                .comment("Display floating damage numbers and critical markers.")
                .translation("config.genshinecraft.damage_numbers")
                .define("damageNumbers", true);
        SPEC = builder.build();
    }

    private GenshinecraftNeoForgeClientConfig() {
    }
}
