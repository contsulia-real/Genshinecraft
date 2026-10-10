package dev.genshinecraft.neoforge;

import dev.genshinecraft.Genshinecraft;
import net.neoforged.fml.common.Mod;

@Mod(Genshinecraft.MOD_ID)
public final class GenshinecraftNeoForge {
    public GenshinecraftNeoForge() {
        Genshinecraft.init();
    }
}
