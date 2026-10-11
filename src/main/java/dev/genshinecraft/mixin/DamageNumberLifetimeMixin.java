package dev.genshinecraft.mixin;

import dev.genshinecraft.combat.DamageNumbers;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Display;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Short-lived damage numbers with a slow upward drift. */
@Mixin(Display.class)
public abstract class DamageNumberLifetimeMixin {
    @Inject(method = "tick", at = @At("TAIL"))
    private void genshinecraft$riseAndExpire(CallbackInfo ci) {
        Display display = (Display) (Object) this;
        if (!(display.level() instanceof ServerLevel)) {
            return;
        }
        if (!DamageNumbers.isOurDisplay(display)) {
            return;
        }
        if (display.tickCount >= 22) {
            display.discard();
        } else {
            display.setPos(display.getX(), display.getY() + 0.018, display.getZ());
        }
    }
}
