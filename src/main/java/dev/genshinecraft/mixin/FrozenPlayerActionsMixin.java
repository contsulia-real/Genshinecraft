package dev.genshinecraft.mixin;

import dev.genshinecraft.element.ElementalAuraHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Player overrides both melee attack and travel: cancel them at the server. */
@Mixin(Player.class)
public abstract class FrozenPlayerActionsMixin {
    private boolean genshinecraft$frozen() {
        return ((ElementalAuraHolder) (Object) this).genshinecraft$auras().isFrozen();
    }

    @Inject(method = "attack", at = @At("HEAD"), cancellable = true)
    private void genshinecraft$stopAttack(Entity target, CallbackInfo ci) {
        if (genshinecraft$frozen()) {
            ci.cancel();
        }
    }

    @Inject(method = "travel", at = @At("HEAD"), cancellable = true)
    private void genshinecraft$stopTravel(Vec3 input, CallbackInfo ci) {
        if (genshinecraft$frozen()) {
            ci.cancel();
        }
    }

    //? if <26.3 {
    @Inject(method = "jumpFromGround", at = @At("HEAD"), cancellable = true)
    private void genshinecraft$stopJump(CallbackInfo ci) {
        if (genshinecraft$frozen()) {
            ci.cancel();
        }
    }
    //?}
}
