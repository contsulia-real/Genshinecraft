package dev.genshinecraft.element;

import dev.genshinecraft.combat.DamageNumbers;
import dev.genshinecraft.combat.ElementalCombat;
import dev.genshinecraft.item.CarrierData.Element;
import dev.genshinecraft.particle.ElementalParticles;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.damagesource.DamageSource;

import java.util.UUID;

/**
 * Successful-hit-only elemental reactions.
 * Transformative damage and durations below are initial mod balance values,
 * not exact Genshin level/elemental-mastery formulas.
 */
public final class ElementalReactionEffects {
    private ElementalReactionEffects() {
    }

    public static void onHit(ServerLevel level, Player attacker, LivingEntity target,
                             Element incoming, String attackGroup) {
        ElementalAuraState state = ((ElementalAuraHolder) target).genshinecraft$auras();
        if (!state.recordHit(attacker.getUUID(), incoming, attackGroup, target.tickCount)) {
            return;
        }

        // Bloom cores persist independently of their original host target.
        if (ElementalBloomCore.trigger(level, target, incoming)) {
            burst(level, target, incoming, 8);
        }

        ElementalReactionEngine.Plan plan = ElementalReactionEngine.preview(state, incoming);
        if (plan.reaction() == ElementalReactionEngine.Reaction.NONE) {
            if (incoming != Element.ANEMO && incoming != Element.GEO) {
                state.apply(incoming, attacker.getUUID());
            }
            return;
        }

        if (plan.gaugeCost() > 0f) {
            state.consume(plan.existing(), plan.gaugeCost());
        }

        switch (plan.reaction()) {
            case OVERLOADED -> {
                splash(level, target, attacker, Element.PYRO, 2.5f, 3.0, true);
            }
            case SUPERCONDUCT -> {
                splash(level, target, attacker, Element.CRYO, 1.5f, 3.0, false);
                debuffSuperconduct(level, target);
                for (LivingEntity other : level.getEntitiesOfClass(LivingEntity.class,
                        target.getBoundingBox().inflate(3.0), e -> e != target && e.isAlive())) {
                    debuffSuperconduct(level, other);
                }
            }
            case ELECTRO_CHARGED -> {
                state.apply(incoming, attacker.getUUID());
                state.beginElectroCharged(attacker.getUUID());
            }
            case FROZEN -> {
                state.beginFrozen();
                target.setTicksFrozen(Math.max(target.getTicksFrozen(), 60));
                //? if >=26.3 {
                /*target.addEffect(new MobEffectInstance(MobEffects.SLOWNESS,
                        60, 6, false, true));
                *///?} else {
                target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,
                        60, 6, false, true));
                //?}
            }
            case SWIRL -> {
                for (LivingEntity other : level.getEntitiesOfClass(LivingEntity.class,
                        target.getBoundingBox().inflate(3.0), e -> e != target && e.isAlive())) {
                    ((ElementalAuraHolder) other).genshinecraft$auras()
                            .apply(plan.existing(), attacker.getUUID());
                    dealDamage(level, other, plan.existing(), attacker.getUUID(), 1.8f);
                    burst(level, other, plan.existing(), 5);
                }
            }
            case CRYSTALLIZE -> ElementalCrystals.drop(level, target, plan.existing());
            case BURNING -> {
                state.beginBurning(attacker.getUUID());
                state.apply(Element.PYRO, attacker.getUUID());
            }
            case BLOOM -> ElementalBloomCore.spawn(level, target, attacker);
            case QUICKEN -> {
                state.beginQuicken();
            }
            case AGGRAVATE, SPREAD -> {
                // Additive bonus was applied in the original damage call, pre-armor.
            }
            default -> {
                // Vaporize and Melt already modified the original hit's damage.
            }
        }
        burst(level, target, incoming, 12);
        if (plan.existing() != null) {
            level.sendParticles(ElementalParticles.EMBLEMS.get(plan.existing()).get(),
                    target.getX(), target.getY() + target.getBbHeight() * 0.8,
                    target.getZ(), 3, 0.35, 0.2, 0.35, 0.015);
        }
    }

    public static void onPhysicalHit(ServerLevel level, LivingEntity target) {
        ElementalAuraState state = ((ElementalAuraHolder) target).genshinecraft$auras();
        if (!state.isFrozen()) {
            return;
        }
        state.shatter();
        target.setTicksFrozen(0);
        //? if >=26.3 {
        /*target.removeEffect(MobEffects.SLOWNESS);
        *///?} else {
        target.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
        //?}
        burst(level, target, Element.CRYO, 10);
    }

    public static void dealDamage(ServerLevel level, LivingEntity target, Element element,
                                  UUID owner, float amount) {
        if (!target.isAlive() || amount <= 0) {
            return;
        }
        Player resolved = owner == null ? null : level.getPlayerByUUID(owner);
        ServerPlayer attacker = resolved instanceof ServerPlayer online ? online : null;
        DamageSource source = ElementalCombat.reactionSource(level, element, attacker);
        float beforeHealth = target.getHealth();
        float beforeAbsorption = target.getAbsorptionAmount();
        //? if >=26.3 {
        /*boolean hurt = target.hurtOrSimulate(source, amount);
        *///?} else {
        boolean hurt = target.hurt(source, amount);
        //?}
        if (hurt && attacker != null) {
            DamageNumbers.afterHit(attacker, target, source, beforeHealth, beforeAbsorption,
                    false, false);
        }
    }

    private static void debuffSuperconduct(ServerLevel level, LivingEntity target) {
        ((ElementalAuraHolder) target).genshinecraft$auras().beginSuperconduct();
    }

    private static void splash(ServerLevel level, LivingEntity central, Player attacker,
                               Element element, float damage, double radius, boolean push) {
        for (LivingEntity victim : level.getEntitiesOfClass(LivingEntity.class,
                central.getBoundingBox().inflate(radius), LivingEntity::isAlive)) {
            if (victim != attacker) {
                dealDamage(level, victim, element, attacker.getUUID(), damage);
            }
            if (push && victim != attacker) {
                double dx = victim.getX() - central.getX();
                double dz = victim.getZ() - central.getZ();
                double length = Math.max(0.1, Math.sqrt(dx * dx + dz * dz));
                victim.push(dx / length * 0.65, 0.22, dz / length * 0.65);
            }
        }
    }

    private static void burst(ServerLevel level, LivingEntity target, Element element, int count) {
        level.sendParticles(ElementalParticles.SPARKS.get(element).get(),
                target.getX(), target.getY() + target.getBbHeight() * 0.62, target.getZ(),
                count, 0.3, 0.35, 0.3, 0.07);
    }
}
