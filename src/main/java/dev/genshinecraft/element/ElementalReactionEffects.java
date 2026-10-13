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
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.network.chat.Component;

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
                             Element incoming, String attackGroup,
                             ElementalReactionEngine.Mode mode, int mastery) {
        ElementalAuraState state = ((ElementalAuraHolder) target).genshinecraft$auras();
        // Pyro breaks frozen targets on the first hit and leaves a Pyro aura.
        if (incoming == Element.PYRO && state.isFrozen()) {
            state.shatter();
            target.setTicksFrozen(0);
            state.consume(Element.CRYO, 16f);
            state.apply(Element.PYRO, attacker.getUUID(), mode);
            spawnReaction(level, target, "shatter", 14);
            return;
        }
        onFrozenAttack(level, target);
        if (!state.recordHit(attacker.getUUID(), incoming, attackGroup, target.tickCount)) {
            return;
        }
        state.recordStellarApplication(incoming);
        ElementalBloomCore.trigger(level, target, incoming, attacker, mastery);
        applyIncomingElement(level, attacker, target, incoming, false, mode, mastery);
    }

    /** Environmental applications resolve the same normal reactions without inventing a player attacker. */
    public static void onEnvironment(ServerLevel level, LivingEntity target, Element incoming) {
        if (!target.isAlive()) return;
        applyIncomingElement(level, null, target, incoming, false, ElementalReactionEngine.Mode.NORMAL, 0);
    }

    /**
     * Swirl disperses an existing element into each nearby target as a new
     * elemental application. This must resolve the target's gauge/reaction,
     * not overwrite its aura. Secondary application cannot generate Swirl
     * again because it carries Pyro/Hydro/Electro/Cryo, never Anemo.
     */
    private static void applyIncomingElement(ServerLevel level, Player attacker,
                                             LivingEntity target, Element incoming,
                                             boolean swirlSpread, ElementalReactionEngine.Mode mode,
                                             int mastery) {
        ElementalAuraState state = ((ElementalAuraHolder) target).genshinecraft$auras();
        UUID owner = attacker == null ? null : attacker.getUUID();
        if (swirlSpread && attacker != null) {
            ElementalBloomCore.trigger(level, target, incoming, attacker, mastery);
        }
        ElementalReactionEngine.Plan plan = attacker == null
                ? ElementalReactionEngine.preview(state, incoming)
                : ElementalReactionEngine.preview(state, incoming,
                        swirlSpread ? ElementalReactionEngine.Mode.NORMAL : mode);
        // Hydro Swirl itself only spreads Wet; secondary Vaporize can still
        // damage targets already carrying Pyro.
        if (swirlSpread && (incoming != Element.HYDRO
                || plan.reaction() == ElementalReactionEngine.Reaction.VAPORIZE)) {
            float spreadDamage = transformativeDamage(1.8f, mastery);
            float damage = switch (plan.reaction()) {
                case VAPORIZE, MELT -> amplifiedDamage(spreadDamage, plan.damageMultiplier(), mastery);
                case AGGRAVATE -> spreadDamage + additiveBonus(2f, mastery);
                case SPREAD -> spreadDamage + additiveBonus(2.4f, mastery);
                default -> spreadDamage * plan.damageMultiplier();
            };
            dealDamage(level, target, incoming, owner, damage);
        }
        if (plan.reaction() == ElementalReactionEngine.Reaction.NONE) {
            if (incoming != Element.ANEMO && incoming != Element.GEO) {
                state.apply(incoming, owner, mode);
            }
            return;
        }

        if (plan.gaugeCost() > 0f) {
            state.consume(plan.existing(), plan.gaugeCost());
        }

        switch (plan.reaction()) {
            case OVERLOADED -> {
                splash(level, target, attacker, Element.PYRO, 2.5f, mastery, 3.0, true);
            }
            case SUPERCONDUCT -> {
                splash(level, target, attacker, Element.CRYO, 1.5f, mastery, 3.0, false);
                debuffSuperconduct(level, target);
                for (LivingEntity other : level.getEntitiesOfClass(LivingEntity.class,
                        target.getBoundingBox().inflate(3.0), e -> e != target && e.isAlive())) {
                    debuffSuperconduct(level, other);
                }
            }
            case ELECTRO_CHARGED -> {
                state.apply(incoming, owner, mode);
                state.beginElectroCharged(owner, mastery);
            }
            case FROZEN -> {
                state.beginFrozen();
                target.setTicksFrozen(Math.max(target.getTicksFrozen(), 60));
            }
            case SWIRL -> {
                // Original target gets the Swirl hit. Nearby targets receive
                // the *element*, with their own reaction consumed/resolved.
                dealDamage(level, target, plan.existing(), owner,
                        transformativeDamage(1.8f, mastery));
                for (LivingEntity other : level.getEntitiesOfClass(LivingEntity.class,
                        target.getBoundingBox().inflate(3.0), e -> e != target && e.isAlive())) {
                    applyIncomingElement(level, attacker, other, plan.existing(), true,
                            ElementalReactionEngine.Mode.NORMAL, mastery);
                    spawnReaction(level, other, "swirl_" + elementKey(plan.existing()), 6);
                }
            }
            case CRYSTALLIZE -> ElementalCrystals.drop(level, target, plan.existing());
            case LUNAR_CHARGED -> {
                state.apply(incoming, owner, mode);
                state.beginLunarCharged(owner, mastery);

            }
            case LUNAR_BLOOM -> {
                // Lunar-Bloom generates a core and reserve, without an immediate reaction hit.
                if (attacker == null) break;
                ElementalBloomCore.spawn(level, target, owner, true, mastery);
                ElementalAuraState dew = ((ElementalAuraHolder) attacker).genshinecraft$auras();
                if (dew.collectVerdantDew()) {
                    if (attacker instanceof ServerPlayer player) {
                        notifyVerdantDew(player, dew.verdantDew());
                    }
                    spawnReaction(level, attacker, "lunar_dew", 8);
                }
            }
            case LUNAR_CRYSTALLIZE -> {
                ElementalCrystals.createMoonCages(level, target);
                state.startMoonCages(owner, mastery);
            }
            case STELLAR_CONDUCT -> {
                // The reaction itself deals no damage; the six-second field boosts Cryo
                // and Electro hits and lowers effective physical resistance.
                state.beginStellarField(target, true);
                for (LivingEntity nearby : level.getEntitiesOfClass(LivingEntity.class,
                        target.getBoundingBox().inflate(4.5), LivingEntity::isAlive)) {
                    if (nearby != target) {
                        ((ElementalAuraHolder) nearby).genshinecraft$auras()
                                .beginStellarField(nearby, false);
                    }
                }
            }
            case STELLAR_SWIRL -> {
                dealSpecialDamage(level, target, Element.ANEMO, owner, 0.75f,
                        mastery, DamageNumbers.Gradient.STELLAR_WIND);
                LivingEntity anchor = target;
                for (LivingEntity other : level.getEntitiesOfClass(LivingEntity.class,
                        target.getBoundingBox().inflate(5.0), LivingEntity::isAlive)) {
                    if (((ElementalAuraHolder) other).genshinecraft$auras().stellarVortexTicks() > 0) {
                        anchor = other;
                        break;
                    }
                }
                ElementalAuraState vortex = ((ElementalAuraHolder) anchor).genshinecraft$auras();
                if (vortex.triggerStellarVortex(owner, mastery)) {
                    vortex.detonateStellarVortex(level, anchor);
                }
            }
            case BURNING -> {
                state.beginBurning(owner, mastery);
                state.apply(Element.PYRO, owner, mode);
            }
            case BLOOM -> ElementalBloomCore.spawn(level, target, owner, false, mastery);
            case QUICKEN -> {
                state.beginQuicken();
            }
            case AGGRAVATE, SPREAD -> {
                // Player hits got their bonus pre-armor. The environment has no attack
                // amount, so it supplies only the additive reaction component.
                if (attacker == null) {
                    dealDamage(level, target, incoming, null,
                            additiveBonus(plan.reaction() == ElementalReactionEngine.Reaction.AGGRAVATE
                                    ? 2f : 2.4f, mastery));
                }
            }
            case VAPORIZE, MELT -> {
                // Player hits already applied amplification before armor; environmental
                // application needs a small base hit to produce actual reaction damage.
                if (attacker == null) {
                    dealDamage(level, target, incoming, null,
                            amplifiedDamage(1f, plan.damageMultiplier(), mastery));
                }
            }
            default -> {
            }
        }
        String effect = switch (plan.reaction()) {
            case SWIRL -> "swirl_" + elementKey(plan.existing());
            case CRYSTALLIZE -> "crystallize_" + elementKey(plan.existing());
            default -> plan.reaction().name().toLowerCase(java.util.Locale.ROOT);
        };
        spawnReaction(level, target, effect, 12);
    }

    /** Moon: lightning hits targets carrying both Hydro and Electro; every 40 ticks. */
    public static void lunarThundercloud(ServerLevel level, LivingEntity center,
                                         UUID owner, int mastery) {
        for (LivingEntity enemy : level.getEntitiesOfClass(LivingEntity.class,
                center.getBoundingBox().inflate(5.0), LivingEntity::isAlive)) {
            ElementalAuraState targetState = ((ElementalAuraHolder) enemy).genshinecraft$auras();
            if (targetState.has(Element.HYDRO) && targetState.has(Element.ELECTRO)) {
                dealSpecialDamage(level, enemy, Element.ELECTRO, owner, 3.0f, mastery,
                        DamageNumbers.Gradient.LUNAR_CHARGED);
                spawnReaction(level, enemy, "lunar_charged", 8);
            }
        }
    }

    /** Count actual successful weapon/projectile attacks, not two new elemental reactions. */
    public static void moonCageAttacked(ServerLevel level, LivingEntity target) {
        ElementalAuraState.MoonVolley volley =
                ((ElementalAuraHolder) target).genshinecraft$auras().moonCageAttackLanded();
        if (volley == null) return;
        ElementalCrystals.releaseMoonCages(level, target);
        lunarMoondriftHarmony(level, target, volley.owner(), volley.mastery());
    }

    /** Three moon cages fire together after the second successful attack. */
    private static void lunarMoondriftHarmony(ServerLevel level, LivingEntity center,
                                              UUID owner, int mastery) {
        for (LivingEntity enemy : level.getEntitiesOfClass(LivingEntity.class,
                center.getBoundingBox().inflate(4.5), LivingEntity::isAlive)) {
            dealSpecialDamage(level, enemy, Element.GEO, owner, 1.6f, mastery,
                    DamageNumbers.Gradient.LUNAR_CRYSTALLIZE);
            spawnReaction(level, enemy, "lunar_crystallize", 7);
        }
    }

    /** Stellars: vortex detonates at 60 ticks or on its sixth Swirl, with a larger Lv.2 radius. */
    public static void stellarVortexExplosion(ServerLevel level, LivingEntity center,
                                              UUID owner, int stage, int mastery) {
        double radius = stage >= 2 ? 4.5 : 3.0;
        for (LivingEntity enemy : level.getEntitiesOfClass(LivingEntity.class,
                center.getBoundingBox().inflate(radius), LivingEntity::isAlive)) {
            dealSpecialDamage(level, enemy, Element.CRYO, owner,
                    stage >= 2 ? 3f : 2f, mastery, DamageNumbers.Gradient.STELLAR_ICE);
            spawnReaction(level, enemy, "stellar_vortex", stage >= 2 ? 13 : 8);
            ((ElementalAuraHolder) enemy).genshinecraft$auras().apply(Element.CRYO, owner);
        }
        // Minecraft implementation of the Wiki's brief high-jump effect after detonation.
        for (Player player : level.getEntitiesOfClass(Player.class,
                center.getBoundingBox().inflate(radius), Player::isAlive)) {
            //? if >=26.3 {
            /*player.addEffect(new MobEffectInstance(MobEffects.JUMP_BOOST, 40, 1, false, true));
            *///?} else {
            player.addEffect(new MobEffectInstance(MobEffects.JUMP, 40, 1, false, true));
            //?}
        }
    }

    /** Vanilla action-bar message for changes to the player's Verdant Dew reserve. */
    static void notifyVerdantDew(ServerPlayer player, int count) {
        Component message = Component.translatable("message.genshinecraft.verdant_dew", count);
        //? if >=26.3 {
        /*player.sendOverlayMessage(message);
        *///?} else {
        player.displayClientMessage(message, true);
        //?}
    }

    /** EM bonuses apply to the original triggering attack or reaction, never a target's EM. */
    public static float amplifiedDamage(float base, float multiplier, int mastery) {
        double em = Math.max(0, mastery);
        return (float) (base * multiplier * (1.0 + 2.78 * em / (em + 1400.0)));
    }

    public static float additiveBonus(float base, int mastery) {
        double em = Math.max(0, mastery);
        return (float) (base * (1.0 + 5.0 * em / (em + 1200.0)));
    }

    public static float transformativeDamage(float base, int mastery) {
        double em = Math.max(0, mastery);
        return (float) (base * (1.0 + 16.0 * em / (em + 2000.0)));
    }

    /** Wiki reactions can crit. Mod adaptation: 5% baseline, 1.5x crit and EM bonus. */
    static void dealSpecialDamage(ServerLevel level, LivingEntity target, Element element,
                                          UUID owner, float base, int mastery, DamageNumbers.Gradient gradient) {
        // Reactions target opponents; the triggering player cannot damage themself.
        if (owner != null && owner.equals(target.getUUID())) return;
        float bonus = 1f + (float) (6.0 * Math.max(0, mastery) / (Math.max(0, mastery) + 2000.0));
        boolean critical = level.getRandom().nextFloat() < 0.05f;
        dealDamage(level, target, element, owner, base * bonus * (critical ? 1.5f : 1f), gradient);
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
        dealDamage(level, target, element, owner, amount, null);
    }

    public static void dealDamage(ServerLevel level, LivingEntity target, Element element,
                                  UUID owner, float amount, DamageNumbers.Gradient gradient) {
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
                    false, false, gradient);
        }
    }

    private static void debuffSuperconduct(ServerLevel level, LivingEntity target) {
        ((ElementalAuraHolder) target).genshinecraft$auras().beginSuperconduct();
    }

    private static void splash(ServerLevel level, LivingEntity central, Player attacker,
                               Element element, float damage, int mastery, double radius, boolean push) {
        UUID owner = attacker == null ? null : attacker.getUUID();
        float scaledDamage = transformativeDamage(damage, mastery);
        for (LivingEntity victim : level.getEntitiesOfClass(LivingEntity.class,
                central.getBoundingBox().inflate(radius), LivingEntity::isAlive)) {
            if (victim != attacker) {
                dealDamage(level, victim, element, owner, scaledDamage);
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
