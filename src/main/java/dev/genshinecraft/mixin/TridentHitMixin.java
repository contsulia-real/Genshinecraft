package dev.genshinecraft.mixin;

import dev.genshinecraft.combat.ElementalCombat;
import dev.genshinecraft.combat.DamageNumbers;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
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
        /*Projectile shot = (Projectile) (Object) this;
        float healthBefore = target instanceof LivingEntity living ? living.getHealth() : 0f;
        float absorptionBefore = target instanceof LivingEntity living ? living.getAbsorptionAmount() : 0f;
        float adjusted = shot.getOwner() instanceof Player player
                ? ElementalCombat.reactionDamage(player, target, source, amount, "normal_projectile") : amount;
        boolean hurt = target.hurtOrSimulate(source, adjusted);
        *///?} else {
        Projectile shot = (Projectile) (Object) this;
        float healthBefore = target instanceof LivingEntity living ? living.getHealth() : 0f;
        float absorptionBefore = target instanceof LivingEntity living ? living.getAbsorptionAmount() : 0f;
        float adjusted = shot.getOwner() instanceof Player player
                ? ElementalCombat.reactionDamage(player, target, source, amount, "normal_projectile") : amount;
        boolean hurt = target.hurt(source, adjusted);
        //?}
        if (hurt && shot.getOwner() instanceof Player attacker) {
            DamageNumbers.afterHit(attacker, target, source, healthBefore, absorptionBefore,
                    false, false);
        }
        if (hurt) {
            ElementalCombat.onProjectileHit((Projectile) (Object) this, target, source);
        }
        return hurt;
    }
}
