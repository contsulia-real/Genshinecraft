package dev.genshinecraft.element;

import dev.genshinecraft.item.CarrierData.Element;
import dev.genshinecraft.particle.ElementalParticles;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

/** Commit elemental gauge changes and gameplay effects only after a successful hit. */
public final class ElementalReactionEffects {
    private ElementalReactionEffects() {
    }

    public static void onHit(ServerLevel level, Player attacker, LivingEntity target,
                             Element incoming, String attackGroup) {
        ElementalAuraState state = ((ElementalAuraHolder) target).genshinecraft$auras();
        if (!state.recordHit(attacker.getUUID(), incoming, attackGroup, target.tickCount)) {
            return;
        }

        ElementalReactionEngine.Plan plan = ElementalReactionEngine.preview(state, incoming);
        if (plan.reaction() == ElementalReactionEngine.Reaction.NONE) {
            // Wind and Geo trigger reactions but do not leave ordinary auras.
            if (incoming != Element.ANEMO && incoming != Element.GEO) {
                state.apply(incoming, attacker.getUUID());
            }
            return;
        }

        if (plan.gaugeCost() > 0f) {
            state.consume(plan.existing(), plan.gaugeCost());
        }

        switch (plan.reaction()) {
            case ELECTRO_CHARGED -> {
                state.apply(incoming, attacker.getUUID());
                state.beginElectroCharged();
            }
            case FROZEN -> {
                // Reuse the vanilla freeze visual with strong temporary slowness.
                target.setTicksFrozen(Math.max(target.getTicksFrozen(), 60));
                //? if >=26.3 {
                /*target.addEffect(new MobEffectInstance(MobEffects.SLOWNESS,
                        60, 6, false, true));
                *///?} else {
                target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,
                        60, 6, false, true));
                //?}
            }
            case OVERLOADED -> {
                double dx = target.getX() - attacker.getX();
                double dz = target.getZ() - attacker.getZ();
                double distance = Math.max(0.01, Math.sqrt(dx * dx + dz * dz));
                target.push(dx / distance * 0.7, 0.28, dz / distance * 0.7);
            }
            case SUPERCONDUCT -> {
                // Temporary combat debuff; armor-specific physical shred is a later step.
                target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS,
                        100, 0, false, true));
            }
            case SWIRL -> {
                // Basic elemental spread: emit the aura element on nearby living targets.
                for (LivingEntity nearby : level.getEntitiesOfClass(LivingEntity.class,
                        target.getBoundingBox().inflate(3.0), e -> e != target && e.isAlive())) {
                    ((ElementalAuraHolder) nearby).genshinecraft$auras()
                            .apply(plan.existing(), attacker.getUUID());
                    level.sendParticles(ElementalParticles.SPARKS.get(plan.existing()).get(),
                            nearby.getX(), nearby.getY() + nearby.getBbHeight() * 0.5,
                            nearby.getZ(), 4, 0.2, 0.2, 0.2, 0.02);
                }
            }
            default -> {
                // Vaporize/Melt use the adjusted original hit; crystallize is visual for now.
            }
        }

        // Visually distinguish reactions from ordinary hit bursts without replacing
        // the user's hand-edited particle art or injecting an extra damage hit.
        level.sendParticles(ElementalParticles.SPARKS.get(plan.incoming()).get(),
                target.getX(), target.getY() + target.getBbHeight() * 0.62, target.getZ(),
                12, 0.3, 0.36, 0.3, 0.09);
        level.sendParticles(ElementalParticles.EMBLEMS.get(plan.existing()).get(),
                target.getX(), target.getY() + target.getBbHeight() * 0.8, target.getZ(),
                3, 0.35, 0.2, 0.35, 0.015);
    }
}
