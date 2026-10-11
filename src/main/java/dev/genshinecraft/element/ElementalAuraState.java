package dev.genshinecraft.element;

import dev.genshinecraft.item.CarrierData;
import dev.genshinecraft.particle.ElementalParticles;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

/** Server-owned aura gauges. Different elements can coexist until reactions are added. */
public final class ElementalAuraState {
    public static final int DEFAULT_TICKS = 190; // 1U: 9.5 seconds
    private static final float DECAY_PER_TICK = 1.0f / DEFAULT_TICKS;
    private static final int PARTICLE_INTERVAL = 8;

    public record Aura(float gauge, int remainingTicks, UUID applier) {
    }

    private final EnumMap<CarrierData.Element, Aura> auras =
            new EnumMap<>(CarrierData.Element.class);

    public Map<CarrierData.Element, Aura> snapshot() {
        return Map.copyOf(auras);
    }

    public boolean has(CarrierData.Element element) {
        return auras.containsKey(element);
    }

    public void apply(CarrierData.Element element, UUID applier) {
        if (element != null) {
            auras.put(element, new Aura(1.0f, DEFAULT_TICKS, applier));
        }
    }

    public void restore(CarrierData.Element element, float gauge, int ticks, UUID applier) {
        if (element != null && Float.isFinite(gauge) && gauge > 0f && gauge <= 16f
                && ticks > 0 && ticks <= 2400) {
            auras.put(element, new Aura(gauge, ticks, applier));
        }
    }

    public void clear() {
        auras.clear();
    }

    public void tick(LivingEntity entity) {
        if (!(entity.level() instanceof ServerLevel server)) {
            return;
        }
        if (!entity.isAlive()) {
            auras.clear();
            return;
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
