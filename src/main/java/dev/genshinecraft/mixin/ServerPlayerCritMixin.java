package dev.genshinecraft.mixin;

import dev.genshinecraft.combat.DamageNumbers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerCritMixin {
    @Inject(method = "crit", at = @At("HEAD"))
    private void genshinecraft$criticalNumber(Entity target, CallbackInfo ci) {
        DamageNumbers.onVanillaCritical((ServerPlayer) (Object) this, target);
    }
}
