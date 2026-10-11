package dev.genshinecraft.element;

import dev.genshinecraft.combat.DamageNumbers;
import dev.genshinecraft.combat.ElementalCombat;
import dev.genshinecraft.item.CarrierData.Element;
import dev.genshinecraft.particle.ElementalParticles;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
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
        onFrozenAttack(level, target);
        if (!state.recordHit(attacker.getUUID(), incoming, attackGroup, target.tickCount)) {
            return;
        }
        ElementalBloomCore.trigger(level, target, incoming);
        applyIncomingElement(level, attacker, target, incoming, false);
    }

    /**
     * Swirl disperses an existing element into each nearby target as a new
     * elemental application. This must resolve the target's gauge/reaction,
     * not overwrite its aura. Secondary application cannot generate Swirl
     * again because it carries Pyro/Hydro/Electro/Cryo, never Anemo.
     */
    private static void applyIncomingElement(ServerLevel level, Player attacker,
                                             LivingEntity target, Element incoming,
                                             boolean swirlSpread) {
        ElementalAuraState state = ((ElementalAuraHolder) target).genshinecraft$auras();
        if (swirlSpread) {
            ElementalBloomCore.trigger(level, target, incoming);
        }
        ElementalReactionEngine.Plan plan = ElementalReactionEngine.preview(state, incoming);
        // Hydro Swirl itself only spreads Wet; secondary Vaporize can still
        // damage targets already carrying Pyro.
        if (swirlSpread && (incoming != Element.HYDRO
                || plan.reaction() == ElementalReactionEngine.Reaction.VAPORIZE)) {
            float multiplier = plan.damageMultiplier();
            float bonus = switch (plan.reaction()) {
                case AGGRAVATE -> 2f;
                case SPREAD -> 2.4f;
                default -> 0f;
            };
            dealDamage(level, target, incoming, attacker.getUUID(), 1.8f * multiplier + bonus);
        }
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
            }
            case SWIRL -> {
                // Original target gets the Swirl hit. Nearby targets receive
                // the *element*, with their own reaction consumed/resolved.
                dealDamage(level, target, plan.existing(), attacker.getUUID(), 1.8f);
                for (LivingEntity other : level.getEntitiesOfClass(LivingEntity.class,
                        target.getBoundingBox().inflate(3.0), e -> e != target && e.isAlive())) {
                    applyIncomingElement(level, attacker, other, plan.existing(), true);
                    spawnReaction(level, other, "swirl_" + elementKey(plan.existing()), 6);
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
        String effect = switch (plan.reaction()) {
            case SWIRL -> "swirl_" + elementKey(plan.existing());
            case CRYSTALLIZE -> "crystallize_" + elementKey(plan.existing());
            default -> plan.reaction().name().toLowerCase(java.util.Locale.ROOT);
        };
        spawnReaction(level, target, effect, 12);
    }

    public static void onPhysicalHit(ServerLevel level, LivingEntity target) {
        onFrozenAttack(level, target);
    }

    private static void onFrozenAttack(ServerLevel level, LivingEntity target) {
        ElementalAuraState state = ((ElementalAuraHolder) target).genshinecraft$auras();
        if (state.onAttackLanded()) {
            target.setTicksFrozen(0);
            spawnReaction(level, target, "shatter", 12);
        }
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

    private static String elementKey(Element element) {
        return element.name().toLowerCase(java.util.Locale.ROOT);
    }

    public static void spawnReaction(ServerLevel level, LivingEntity target, String reaction, int count) {
        level.sendParticles(ElementalParticles.reaction(reaction),
                target.getX(), target.getY() + target.getBbHeight() * 0.62, target.getZ(),
                count, 0.3, 0.32, 0.3, 0.055);
    }
}
