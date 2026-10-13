package dev.genshinecraft.mixin;

import dev.genshinecraft.world.WorldLevelMobs;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
//? if >=26.3 {
/*import net.minecraft.server.level.ServerLevel;
*///?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/** Scale damage from vanilla hostile mobs once, at the victim's server-side hurt entry. */
@Mixin(LivingEntity.class)
public abstract class WorldLevelDamageMixin {
    //? if >=26.3 {
    /*@ModifyVariable(method = "hurtServer(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;F)Z",
            at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private float genshinecraft$mobDamage(float amount, ServerLevel level,
                                          DamageSource source, float original) {
        return WorldLevelMobs.scaleDamage(source, amount);
    }
    *///?} else {
    @ModifyVariable(method = "hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z",
            at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private float genshinecraft$mobDamage(float amount, DamageSource source, float original) {
        return WorldLevelMobs.scaleDamage(source, amount);
    }
    //?}
}
