package dev.genshinecraft.mixin;

import dev.genshinecraft.element.ElementSlime;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
//? if >=26.3 {
/*import net.minecraft.resources.Identifier;
*///?} else {
import net.minecraft.resources.ResourceLocation;
//?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Vanilla Monster Hunter lists vanilla entity IDs explicitly, not the MONSTER
 * spawn category. Treat an elemental slime kill as a slime kill for that one
 * existing vanilla criterion; preserve all original advancements and tabs.
 */
@Mixin(ServerPlayer.class)
public abstract class ElementSlimeMonsterHunterMixin {
    //? if >=26.3 {
    /*@Inject(method = "awardKillScore", at = @At("TAIL"))
    private void genshinecraft$elementSlimeKill(Entity victim, DamageSource source, CallbackInfo ci) {
        genshinecraft$grantSlimeCriterion(victim);
    }
    *///?} else {
    @Inject(method = "awardKillScore", at = @At("TAIL"))
    private void genshinecraft$elementSlimeKill(Entity victim, int score, DamageSource source, CallbackInfo ci) {
        genshinecraft$grantSlimeCriterion(victim);
    }
    //?}

    @Unique
    private void genshinecraft$grantSlimeCriterion(Entity victim) {
        if (!(victim instanceof ElementSlime)) return;
        ServerPlayer player = (ServerPlayer) (Object) this;
        var server = ((ServerLevel) player.level()).getServer();
        //? if >=26.3 {
        /*var advancement = server.getAdvancements().get(
                Identifier.withDefaultNamespace("adventure/kill_a_mob"));
        *///?} else if =1.21.1 {
        var advancement = server.getAdvancements().get(
                ResourceLocation.withDefaultNamespace("adventure/kill_a_mob"));
        //?} else {
        /*var advancement = server.getAdvancements().getAdvancement(
                new ResourceLocation("minecraft", "adventure/kill_a_mob"));
        *///?}
        if (advancement != null) {
            player.getAdvancements().award(advancement, "minecraft:slime");
        }
    }
}
