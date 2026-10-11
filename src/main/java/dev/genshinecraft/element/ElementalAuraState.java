package dev.genshinecraft.element;

import dev.genshinecraft.item.CarrierData;
import dev.genshinecraft.particle.ElementalParticles;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

/** Server-owned aura gauges. Different elements can coexist until reactions are added. */
public final class ElementalAuraState {
    public static final int DEFAULT_TICKS = 190; // 1U: 9.5 seconds
    public static final float BASE_GAUGE = 0.8f; // 1U hit after the standard aura tax
    private static final float DECAY_PER_TICK = BASE_GAUGE / DEFAULT_TICKS;
    private static final int PARTICLE_INTERVAL = 8;

    public record Aura(float gauge, int remainingTicks, UUID applier) {
    }

    private final EnumMap<CarrierData.Element, Aura> auras =
            new EnumMap<>(CarrierData.Element.class);

    private record IcdKey(UUID attacker, CarrierData.Element element, String attackGroup) {
    }

    private record IcdEntry(int lastApplicationTick, int hitsSinceApplication) {
    }

    // ICD tracking is transient. It does not need to be saved with the aura gauges.
    private final Map<IcdKey, IcdEntry> icd = new HashMap<>();
    private int electroChargedTicks;

    public Map<CarrierData.Element, Aura> snapshot() {
        return Map.copyOf(auras);
    }

    public boolean has(CarrierData.Element element) {
        return auras.containsKey(element);
    }

    public void apply(CarrierData.Element element, UUID applier) {
        if (element != null) {
            Aura old = auras.get(element);
            if (old == null || old.gauge() <= BASE_GAUGE) {
                auras.put(element, new Aura(BASE_GAUGE, DEFAULT_TICKS, applier));
            }
        }
    }


    public boolean canApply(UUID attacker, CarrierData.Element element, String group, int tick) {
        IcdEntry old = icd.get(new IcdKey(attacker, element, group));
        return old == null || tick - old.lastApplicationTick() >= 50
                || old.hitsSinceApplication() >= 2;
    }

    /** Called for each successful elemental hit, including those suppressed by ICD. */
    public boolean recordHit(UUID attacker, CarrierData.Element element, String group, int tick) {
        IcdKey key = new IcdKey(attacker, element, group);
        IcdEntry old = icd.get(key);
        if (old == null || tick - old.lastApplicationTick() >= 50
                || old.hitsSinceApplication() >= 2) {
            icd.put(key, new IcdEntry(tick, 0));
            return true;
        }
        icd.put(key, new IcdEntry(old.lastApplicationTick(), old.hitsSinceApplication() + 1));
        return false;
    }

    public void consume(CarrierData.Element element, float amount) {
        Aura old = auras.get(element);
        if (old == null || amount <= 0f) {
            return;
        }
        float gauge = old.gauge() - amount;
        if (gauge <= 0f) {
            auras.remove(element);
        } else {
            int ticks = Math.max(1, Math.round(old.remainingTicks() * gauge / old.gauge()));
            auras.put(element, new Aura(gauge, ticks, old.applier()));
        }
    }

    public void beginElectroCharged() {
        electroChargedTicks = 80;
    }

    public void restore(CarrierData.Element element, float gauge, int ticks, UUID applier) {
        if (element != null && Float.isFinite(gauge) && gauge > 0f && gauge <= 16f
                && ticks > 0 && ticks <= 2400) {
            auras.put(element, new Aura(gauge, ticks, applier));
        }
    }

    public void clear() {
        auras.clear();
        icd.clear();
        electroChargedTicks = 0;
    }

    public void tick(LivingEntity entity) {
        if (!(entity.level() instanceof ServerLevel server)) {
            return;
        }
        if (!entity.isAlive()) {
            auras.clear();
            return;
        }
        icd.entrySet().removeIf(entry -> entity.tickCount - entry.getValue().lastApplicationTick() > 70);
        if (electroChargedTicks > 0) {
            electroChargedTicks--;
            if (!has(CarrierData.Element.HYDRO) || !has(CarrierData.Element.ELECTRO)) {
                electroChargedTicks = 0;
            } else if (entity.tickCount % 20 == 0) {
                consume(CarrierData.Element.HYDRO, 0.4f);
                consume(CarrierData.Element.ELECTRO, 0.4f);
                server.sendParticles(ElementalParticles.SPARKS.get(CarrierData.Element.ELECTRO).get(),
                        entity.getX(), entity.getY() + entity.getBbHeight() * 0.6, entity.getZ(),
                        5, 0.3, 0.3, 0.3, 0.03);
                // Tick damage follows vanilla invulnerability and resistance.
                //? if >=26.3 {
                /*entity.hurtOrSimulate(entity.damageSources().magic(), 1.0f);
                *///?} else {
                entity.hurt(entity.damageSources().magic(), 1.0f);
                //?}
            }
        }
        Iterator<Map.Entry<CarrierData.Element, Aura>> it = auras.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<CarrierData.Element, Aura> active = it.next();
            Aura old = active.getValue();
            int remaining = old.remainingTicks() - 1;
            float gauge = old.gauge() - DECAY_PER_TICK;
            if (remaining <= 0 || gauge <= 0f) {
                it.remove();
                continue;
            }
            active.setValue(new Aura(gauge, remaining, old.applier()));
            if (entity.tickCount % PARTICLE_INTERVAL == 0) {
                // Animated orbs orbit the torso; elemental emblems remain legible.
                server.sendParticles(ElementalParticles.AURAS.get(active.getKey()).get(),
                        entity.getX(), entity.getY() + entity.getBbHeight() * 0.65, entity.getZ(),
                        2, entity.getBbWidth() * 0.55 + 0.10,
                        entity.getBbHeight() * 0.20, entity.getBbWidth() * 0.55 + 0.10, 0.008);
            }
            if (entity.tickCount % (PARTICLE_INTERVAL * 2) == 0) {
                server.sendParticles(ElementalParticles.EMBLEMS.get(active.getKey()).get(),
                        entity.getX(), entity.getY() + entity.getBbHeight() * 0.78, entity.getZ(),
                        1, entity.getBbWidth() * 0.4 + 0.06,
                        entity.getBbHeight() * 0.10, entity.getBbWidth() * 0.4 + 0.06, 0.003);
            }
        }
    }
}
