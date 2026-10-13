package dev.genshinecraft.mixin;

import dev.genshinecraft.client.AuraIconOverlay;
import net.minecraft.client.gui.components.BossHealthOverlay;
//? if >=26.3 {
/*import net.minecraft.client.gui.GuiGraphicsExtractor;
*///?} else {
import net.minecraft.client.gui.GuiGraphics;
//?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Paint auras AFTER each frame's vanilla BossBar layer, never underneath the bars. */
@Mixin(BossHealthOverlay.class)
public abstract class BossAuraOverlayMixin {
    //? if >=26.3 {
    /*@Inject(method = "extractRenderState", at = @At("TAIL"))
    private void genshinecraft$showBossAuras(GuiGraphicsExtractor graphics, CallbackInfo ci) {
        AuraIconOverlay.renderBosses(graphics);
    }
    *///?} else {
    @Inject(method = "render", at = @At("TAIL"))
    private void genshinecraft$showBossAuras(GuiGraphics graphics, CallbackInfo ci) {
        AuraIconOverlay.renderBosses(graphics);
    }
    //?}
}
