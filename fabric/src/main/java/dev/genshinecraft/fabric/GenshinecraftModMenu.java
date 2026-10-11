package dev.genshinecraft.fabric;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.genshinecraft.client.GenshinecraftConfigScreen;

/** Optional Mod Menu integration, loaded only when Mod Menu is installed. */
public final class GenshinecraftModMenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return GenshinecraftConfigScreen::new;
    }
}
