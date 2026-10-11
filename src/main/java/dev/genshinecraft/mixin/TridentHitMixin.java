package dev.genshinecraft.mixin;

import dev.genshinecraft.combat.ElementalCombat;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
//? if >=26.3 {
/*import net.minecraft.world.entity.projectile.arrow.ThrownTrident;
*///?} else {
import net.minecraft.world.entity.projectile.ThrownTrident;
//?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ThrownTrident.class)
public abstract class TridentHitMixin {
    @Redirect(method = "onHitEntity",
            at = @At(value = "INVOKE", target =
                    "Lnet/minecraft/world/damagesource/DamageSources;trident(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/Entity;)Lnet/minecraft/world/damagesource/DamageSource;"))
    private DamageSource genshinecraft$source(DamageSources sources, Entity trident, Entity owner) {
        return ElementalCombat.projectileSource((Projectile) (Object) this, sources.trident(trident, owner));
    }

    //? if >=26.3 {
    /*@Redirect(method = "onHitEntity",
            at = @At(value = "INVOKE", target =
                    "Lnet/minecraft/world/entity/Entity;hurtOrSimulate(Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
    *///?} else {
    @Redirect(method = "onHitEntity",
            at = @At(value = "INVOKE", target =
                    "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
    //?}
    private boolean genshinecraft$hit(Entity target, DamageSource source, float amount) {
        //? if >=26.3 {
        /*boolean hurt = target.hurtOrSimulate(source, amount);
        *///?} else {
        boolean hurt = target.hurt(source, amount);
        //?}
        if (hurt) {
            ElementalCombat.onProjectileHit((Projectile) (Object) this, target, source);
        }
        return hurt;
    }
}
