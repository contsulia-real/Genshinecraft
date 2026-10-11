package dev.genshinecraft.mixin;

import dev.genshinecraft.combat.ElementalCombat;
import net.minecraft.world.damagesource.DamageSource;
//? if <26.3 {
import net.minecraft.world.damagesource.DamageSources;
//?}
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
//? if >=26.3 {
/*import org.spongepowered.asm.mixin.Shadow;
*///?}
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Player.class)
public abstract class PlayerAttackMixin {
    //? if >=26.3 {
    /*@Shadow
    private DamageSource createAttackSource(ItemStack stack) { throw new AssertionError(); }
    *///?}
    //? if <26.3 {
    @Redirect(method = "attack",
            at = @At(value = "INVOKE", target =
                    "Lnet/minecraft/world/damagesource/DamageSources;playerAttack(Lnet/minecraft/world/entity/player/Player;)Lnet/minecraft/world/damagesource/DamageSource;"))
    private DamageSource genshinecraft$elementalSource(DamageSources sources, Player player) {
        return ElementalCombat.meleeSource(player, player.getMainHandItem(), sources.playerAttack(player));
    }
    //?} else {
    /*@Redirect(method = "attack",
            at = @At(value = "INVOKE", target =
                    "Lnet/minecraft/world/entity/player/Player;createAttackSource(Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/damagesource/DamageSource;"))
    private DamageSource genshinecraft$elementalSource(Player player, ItemStack stack) {
        return ElementalCombat.meleeSource(player, stack, createAttackSource(stack));
    }
    *///?}

    //? if <26.3 {
    @Redirect(method = "attack",
            at = @At(value = "INVOKE", target =
                    "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
    private boolean genshinecraft$hitParticles(Entity target, DamageSource source, float amount) {
        boolean damaged = target.hurt(source,
                ElementalCombat.reactionDamage((Player) (Object) this, target, source, amount, "normal_melee"));
        if (damaged) {
            ElementalCombat.onMeleeHit((Player) (Object) this, target, source);
        }
        return damaged;
    }
    //?} else {
    /*@Redirect(method = "attack",
            at = @At(value = "INVOKE", target =
                    "Lnet/minecraft/world/entity/Entity;hurtOrSimulate(Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
    private boolean genshinecraft$hitParticles(Entity target, DamageSource source, float amount) {
        boolean damaged = target.hurtOrSimulate(source,
                ElementalCombat.reactionDamage((Player) (Object) this, target, source, amount, "normal_melee"));
        if (damaged) {
            ElementalCombat.onMeleeHit((Player) (Object) this, target, source);
        }
        return damaged;
    }
    *///?}
}
