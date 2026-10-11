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

/** Server-side elemental gauges, ICD and independent timed reaction states. */
public final class ElementalAuraState {
    public static final int DEFAULT_TICKS = 190;
    public static final float BASE_GAUGE = 0.8f;
    private static final float DECAY_PER_TICK = BASE_GAUGE / DEFAULT_TICKS;
    private static final int PARTICLE_INTERVAL = 8;

    public record Aura(float gauge, int remainingTicks, UUID applier) {
    }

    private record IcdKey(UUID attacker, CarrierData.Element element, String attackGroup) {
    }

    private record IcdEntry(int lastApplicationTick, int hitsSinceApplication) {
    }

    private final EnumMap<CarrierData.Element, Aura> auras =
            new EnumMap<>(CarrierData.Element.class);
    private final Map<IcdKey, IcdEntry> icd = new HashMap<>();
    private int electroChargedTicks;
    private int burningTicks;
    private int quickenTicks;
    private int frozenTicks;
    private int superconductTicks;
    private UUID burningOwner;
    private UUID electroOwner;

    public Map<CarrierData.Element, Aura> snapshot() {
        return Map.copyOf(auras);
    }

    public boolean has(CarrierData.Element element) {
        return auras.containsKey(element);
    }

    public boolean isQuickened() {
        return quickenTicks > 0;
    }

    public boolean isFrozen() {
        return frozenTicks > 0;
    }

    public boolean isSuperconductActive() {
        return superconductTicks > 0;
    }

    public int statusTicks(String name) {
        return switch (name) {
            case "electro" -> electroChargedTicks;
            case "burning" -> burningTicks;
            case "quicken" -> quickenTicks;
            case "frozen" -> frozenTicks;
            case "superconduct" -> superconductTicks;
            default -> 0;
        };
    }

    public void restoreStatuses(int electro, int burning, int quicken, int frozen, int superconduct) {
        electroChargedTicks = clampTicks(electro);
        burningTicks = clampTicks(burning);
        quickenTicks = clampTicks(quicken);
        frozenTicks = clampTicks(frozen);
        superconductTicks = clampTicks(superconduct);
    }

    private static int clampTicks(int ticks) {
        return Math.max(0, Math.min(1200, ticks));
    }

    public void apply(CarrierData.Element element, UUID applier) {
        if (element != null) {
            Aura old = auras.get(element);
            if (old == null || old.gauge() <= BASE_GAUGE) {
                auras.put(element, new Aura(BASE_GAUGE, DEFAULT_TICKS, applier));
            }
        }
    }

    public UUID applier(CarrierData.Element element) {
        Aura aura = auras.get(element);
        return aura == null ? null : aura.applier();
    }

    public boolean canApply(UUID attacker, CarrierData.Element element, String group, int tick) {
        IcdEntry old = icd.get(new IcdKey(attacker, element, group));
        return old == null || tick - old.lastApplicationTick() >= 50
                || old.hitsSinceApplication() >= 2;
    }

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

    public void beginElectroCharged(UUID owner) {
        electroChargedTicks = 80;
        electroOwner = owner;
    }

    public void beginBurning(UUID owner) {
        burningTicks = 100;
        burningOwner = owner;
    }

    public void beginQuicken() {
        quickenTicks = 160;
    }

    public void beginFrozen() {
        frozenTicks = 60;
    }

    public void shatter() {
        frozenTicks = 0;
    }

    public void beginSuperconduct() {
        superconductTicks = 100;
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
        burningTicks = 0;
        quickenTicks = 0;
        frozenTicks = 0;
        superconductTicks = 0;
        burningOwner = null;
        electroOwner = null;
    }

    public void tick(LivingEntity entity) {
        if (!(entity.level() instanceof ServerLevel server)) {
            return;
        }
        if (!entity.isAlive()) {
            clear();
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
                ElementalReactionEffects.dealDamage(server, entity, CarrierData.Element.ELECTRO,
                        electroOwner, 1.6f);
            }
        }

        if (burningTicks > 0) {
            burningTicks--;
            if (entity.tickCount % 20 == 0) {
                ElementalReactionEffects.dealDamage(server, entity, CarrierData.Element.PYRO,
                        burningOwner, 1.5f);
                server.sendParticles(ElementalParticles.SPARKS.get(CarrierData.Element.PYRO).get(),
                        entity.getX(), entity.getY() + entity.getBbHeight() * 0.6, entity.getZ(),
                        4, 0.2, 0.3, 0.2, 0.02);
            }
        }
        if (quickenTicks > 0) {
            quickenTicks--;
            if (entity.tickCount % 20 == 0) {
                server.sendParticles(ElementalParticles.EMBLEMS.get(CarrierData.Element.DENDRO).get(),
                        entity.getX(), entity.getY() + entity.getBbHeight() * 0.7, entity.getZ(),
                        1, 0.25, 0.15, 0.25, 0.008);
            }
        }
        if (frozenTicks > 0) {
            frozenTicks--;
            entity.setTicksFrozen(Math.max(entity.getTicksFrozen(), 45));
            if (frozenTicks == 0) {
                entity.setTicksFrozen(0);
            }
        }
        if (superconductTicks > 0) {
            superconductTicks--;
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
