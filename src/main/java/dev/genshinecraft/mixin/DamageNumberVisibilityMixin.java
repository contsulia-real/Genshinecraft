package dev.genshinecraft.mixin;

import dev.genshinecraft.client.DamageNumberSettings;
import dev.genshinecraft.combat.DamageNumbers;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderDispatcher.class)
public abstract class DamageNumberVisibilityMixin {
    //? if >=26.3 {
    /*@Inject(method = "shouldRender", at = @At("HEAD"), cancellable = true)
    private void genshinecraft$hideNumber(Entity entity, Frustum frustum, double x, double y,
                                         double z, float partialTick, CallbackInfoReturnable<Boolean> cir) {
        if (!DamageNumberSettings.enabled() && DamageNumbers.isOurDisplay(entity)) {
            cir.setReturnValue(false);
        }
    }
    *///?} else {
    @Inject(method = "shouldRender", at = @At("HEAD"), cancellable = true)
    private void genshinecraft$hideNumber(Entity entity, Frustum frustum, double x, double y,
                                         double z, CallbackInfoReturnable<Boolean> cir) {
        if (!DamageNumberSettings.enabled() && DamageNumbers.isOurDisplay(entity)) {
            cir.setReturnValue(false);
        }
    }
    //?}
}
