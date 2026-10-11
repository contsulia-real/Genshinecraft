package dev.genshinecraft.mixin;

import dev.genshinecraft.element.ElementalAuraHolder;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Stops mob targeting, pathfinding and AI attacks without changing its saved NoAI flag. */
@Mixin(Mob.class)
public abstract class FrozenMobAiMixin {
    @Inject(method = "serverAiStep", at = @At("HEAD"), cancellable = true)
    private void genshinecraft$stopMobAi(CallbackInfo ci) {
        if (((ElementalAuraHolder) (Object) this).genshinecraft$auras().isFrozen()) {
            ci.cancel();
        }
    }
}
