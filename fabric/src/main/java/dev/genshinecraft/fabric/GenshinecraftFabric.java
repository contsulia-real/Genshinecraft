package dev.genshinecraft.fabric;

import dev.genshinecraft.Genshinecraft;
import net.fabricmc.api.ModInitializer;

public final class GenshinecraftFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Genshinecraft.init();
    }
}
