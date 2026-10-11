package dev.genshinecraft.neoforge;

import dev.genshinecraft.Genshinecraft;
import dev.genshinecraft.client.DamageNumberSettings;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

/** Register NeoForge's built-in configuration screen for 1.21+. */
@Mod(Genshinecraft.MOD_ID)
public final class GenshinecraftNeoForge {
    public GenshinecraftNeoForge(ModContainer container) {
        Genshinecraft.init();
        container.registerConfig(ModConfig.Type.CLIENT, GenshinecraftNeoForgeClientConfig.SPEC);
        DamageNumberSettings.useNativeConfig(GenshinecraftNeoForgeClientConfig.DAMAGE_NUMBERS::get);
        //? if >=26.3 {
        /*boolean client = FMLEnvironment.getDist() == Dist.CLIENT;
        *///?} else {
        boolean client = FMLEnvironment.dist == Dist.CLIENT;
        //?}
        if (client) {
            container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        }
    }
}
