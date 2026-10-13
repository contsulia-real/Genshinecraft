package dev.genshinecraft.mixin;

import net.minecraft.client.gui.components.BossHealthOverlay;
import net.minecraft.client.gui.components.LerpingBossEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import java.util.Map;
import java.util.UUID;

/** Read exactly the same insertion/order map the vanilla BossBar overlay renders. */
@Mixin(BossHealthOverlay.class)
public interface BossOverlayEventsAccessor {
    @Accessor("events")
    Map<UUID, LerpingBossEvent> genshinecraft$events();
}
