package dev.genshinecraft.forge;

import dev.genshinecraft.Genshinecraft;
import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Genshinecraft.MOD_ID)
public final class GenshinecraftForge {
    public GenshinecraftForge() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        EventBuses.registerModEventBus(Genshinecraft.MOD_ID, modBus);
        Genshinecraft.init();
    }
}
