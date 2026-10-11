package dev.genshinecraft.mixin;

import dev.genshinecraft.element.ElementalAuraHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Disable movement, jumping and charged item use while this mod's Freeze aura is active. */
@Mixin(LivingEntity.class)
public abstract class FrozenLivingMovementMixin {
    private boolean genshinecraft$frozen() {
        LivingEntity self = (LivingEntity) (Object) this;
        return ((ElementalAuraHolder) self).genshinecraft$auras().isFrozen();
    }

    @Inject(method = "travel", at = @At("HEAD"), cancellable = true)
    private void genshinecraft$stopTravel(Vec3 motion, CallbackInfo ci) {
        if (genshinecraft$frozen()) {
            ci.cancel();
        }
    }

    @Inject(method = "jumpFromGround", at = @At("HEAD"), cancellable = true)
    private void genshinecraft$stopJump(CallbackInfo ci) {
        if (genshinecraft$frozen()) {
            ci.cancel();
        }
    }

    @Inject(method = "startUsingItem", at = @At("HEAD"), cancellable = true)
    private void genshinecraft$stopUsingItem(InteractionHand hand, CallbackInfo ci) {
        if (genshinecraft$frozen()) {
            ci.cancel();
        }
    }
}
