package dev.genshinecraft.forge;

import dev.genshinecraft.Genshinecraft;
import dev.genshinecraft.client.DamageNumberSettings;
import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/** Configured reads Forge's native CLIENT config; no custom screen API needed. */
@Mod(Genshinecraft.MOD_ID)
public final class GenshinecraftForge {
    public GenshinecraftForge() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        EventBuses.registerModEventBus(Genshinecraft.MOD_ID, modBus);
        Genshinecraft.init();
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT,
                GenshinecraftForgeClientConfig.SPEC);
        DamageNumberSettings.useNativeConfig(GenshinecraftForgeClientConfig.DAMAGE_NUMBERS::get);
    }
}
