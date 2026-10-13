package dev.genshinecraft.mixin;

import dev.genshinecraft.element.ElementalAuraHolder;
import dev.genshinecraft.item.CarrierData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Actual vanilla lightning contact applies an Electro aura to living entities. */
@Mixin(Entity.class)
public abstract class EnvironmentLightningMixin {
    @Inject(method = "thunderHit", at = @At("TAIL"))
    private void genshinecraft$lightningAura(ServerLevel level, LightningBolt bolt, CallbackInfo ci) {
        if ((Object) this instanceof LivingEntity living) {
            ((ElementalAuraHolder) living).genshinecraft$auras()
                    .apply(CarrierData.Element.ELECTRO, null);
        }
    }
}
