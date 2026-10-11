package dev.genshinecraft.mixin;

import dev.genshinecraft.combat.ElementalCombat;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
//? if >=26.3 {
/*import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
*///?} else {
import net.minecraft.world.entity.projectile.AbstractArrow;
//?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/** Replace only the damage source and observe the original hit result. */
@Mixin(AbstractArrow.class)
public abstract class ArrowHitMixin {
    //? if >=26.3 {
    /*@Redirect(method = "onHitEntity",
            at = @At(value = "INVOKE", target =
                    "Lnet/minecraft/world/damagesource/DamageSources;arrow(Lnet/minecraft/world/entity/projectile/arrow/AbstractArrow;Lnet/minecraft/world/entity/Entity;)Lnet/minecraft/world/damagesource/DamageSource;"))
    *///?} else {
    @Redirect(method = "onHitEntity",
            at = @At(value = "INVOKE", target =
                    "Lnet/minecraft/world/damagesource/DamageSources;arrow(Lnet/minecraft/world/entity/projectile/AbstractArrow;Lnet/minecraft/world/entity/Entity;)Lnet/minecraft/world/damagesource/DamageSource;"))
    //?}
    private DamageSource genshinecraft$source(DamageSources sources, AbstractArrow arrow, Entity owner) {
        return ElementalCombat.projectileSource((Projectile) (Object) this, sources.arrow(arrow, owner));
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
        float adjusted = shot.getOwner() instanceof Player player
                ? ElementalCombat.reactionDamage(player, target, source, amount, "normal_projectile") : amount;
        boolean hurt = target.hurtOrSimulate(source, adjusted);
        *///?} else {
        Projectile shot = (Projectile) (Object) this;
        float adjusted = shot.getOwner() instanceof Player player
                ? ElementalCombat.reactionDamage(player, target, source, amount, "normal_projectile") : amount;
        boolean hurt = target.hurt(source, adjusted);
        //?}
        if (hurt) {
            ElementalCombat.onProjectileHit((Projectile) (Object) this, target, source);
        }
        return hurt;
    }
}
