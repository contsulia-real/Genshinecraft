package dev.genshinecraft.element;

import dev.genshinecraft.item.CarrierData;
import dev.genshinecraft.particle.ElementalParticles;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.LightningBolt;
//? if >=26.3 {
/*import net.minecraft.world.entity.EntityTypes;
*///?} else {
import net.minecraft.world.entity.EntityType;
//?}
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

    public record Aura(float gauge, int remainingTicks, UUID applier,
                       ElementalReactionEngine.Mode source) {
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
    private int frozenHitsRemaining;
    private static final int FREEZE_HITS_TO_BREAK = 3;
    private int superconductTicks;
    private UUID burningOwner;
    private int burningMastery;
    private UUID electroOwner;
    private int electroMastery;
    private int lunarChargedTicks;
    private UUID lunarOwner;
    private int lunarMastery;
    private int verdantDew;
    private int dewCooldown;
    private int moondriftTicks;
    private int moondriftCount;
    private UUID moondriftOwner;
    private int moondriftMastery;
    private int stellarFieldTicks;
    private boolean stellarFieldAnchor;
    private double stellarFieldX;
    private double stellarFieldY;
    private double stellarFieldZ;
    private int stellarStacks;
    private int stellarStackWindow;
    private int stellarVortexTicks;
    private int stellarVortexTriggers;
    private UUID vortexOwner;
    private int vortexMastery;

    public Map<CarrierData.Element, Aura> snapshot() {
        return Map.copyOf(auras);
    }

    public ElementalReactionEngine.Mode sourceMode(CarrierData.Element element) {
        Aura aura = auras.get(element);
        return aura == null ? ElementalReactionEngine.Mode.NORMAL : aura.source();
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

    public boolean isStellarFieldActive() { return stellarFieldTicks > 0; }

    public float stellarElementBonus(CarrierData.Element element) {
        if (!isStellarFieldActive() || (element != CarrierData.Element.CRYO
                && element != CarrierData.Element.ELECTRO)) return 1f;
        return 1.2f + (stellarStacks > 0 ? 0.08f + Math.min(12, stellarStacks) * 0.01f : 0f);
    }

    public int verdantDew() { return verdantDew; }
    public int stellarVortexTicks() { return stellarVortexTicks; }

    public void beginLunarCharged(UUID owner, int mastery) {
        lunarChargedTicks = 100;
        lunarOwner = owner;
        lunarMastery = Math.max(0, mastery);
    }

    /** One Verdant Dew per 2.5s, up to three; Lunar-Bloom itself does not hit. */
    public boolean collectVerdantDew() {
        if (dewCooldown > 0 || verdantDew >= 3) return false;
        verdantDew++;
        dewCooldown = 50;
        return true;
    }

    /** One Lunar-Bloom core can spend only one charge. */
    public boolean spendVerdantDew() {
        if (verdantDew <= 0) return false;
        verdantDew--;
        return true;
    }

    /** One Lunar-Crystallize creates three cages. Exactly two landed attacks activate them. */
    public void startMoonCages(UUID owner, int mastery) {
        if (moondriftTicks <= 0) moondriftCount = 0;
        moondriftTicks = 160;
        moondriftOwner = owner;
        moondriftMastery = mastery;
    }

    public record MoonVolley(UUID owner, int mastery) {}

    public MoonVolley moonCageAttackLanded() {
        if (moondriftTicks <= 0 || ++moondriftCount < 2) return null;
        MoonVolley volley = new MoonVolley(moondriftOwner, moondriftMastery);
        moondriftTicks = 0;
        moondriftCount = 0;
        moondriftOwner = null;
        moondriftMastery = 0;
        return volley;
    }

    /** Six-second stellar domain; reapplication refreshes the domain. */
    public void beginStellarField(LivingEntity origin, boolean anchor) {
        if (anchor) {
            stellarFieldTicks = 120;
            stellarFieldAnchor = true;
            stellarFieldX = origin.getX();
            stellarFieldY = origin.getY();
            stellarFieldZ = origin.getZ();
        } else {
            // Field membership expires shortly after leaving the stationary 6-second zone.
            stellarFieldTicks = Math.max(stellarFieldTicks, 14);
        }
    }

    public void recordStellarApplication(CarrierData.Element element) {
        if (stellarFieldTicks <= 0
                || (element != CarrierData.Element.CRYO && element != CarrierData.Element.ELECTRO)) return;
        stellarStacks = Math.min(12, stellarStacks + 1);
        stellarStackWindow = 80;
    }

    /** Stellar Vortex: 3 triggers upgrade; 6 triggers explode early, otherwise 3s. */
    public boolean triggerStellarVortex(UUID owner, int mastery) {
        if (stellarVortexTicks == 0) {
            stellarVortexTicks = 60;
            stellarVortexTriggers = 0;
        }
        vortexOwner = owner;
        vortexMastery = Math.max(0, mastery);
        stellarVortexTriggers++;
        return stellarVortexTriggers >= 6;
    }

    public void detonateStellarVortex(ServerLevel level, LivingEntity center) {
        if (stellarVortexTicks <= 0) return;
        int stage = stellarVortexTriggers >= 3 ? 2 : 1;
        UUID owner = vortexOwner;
        stellarVortexTicks = 0;
        stellarVortexTriggers = 0;
        vortexOwner = null;
        ElementalReactionEffects.stellarVortexExplosion(level, center, owner, stage, vortexMastery);
    }

    public int statusTicks(String name) {
        return switch (name) {
            case "electro" -> electroChargedTicks;
            case "burning" -> burningTicks;
            case "quicken" -> quickenTicks;
            case "frozen" -> frozenTicks;
            case "superconduct" -> superconductTicks;
            case "dew" -> verdantDew;
            case "dew_cooldown" -> dewCooldown;
            default -> 0;
        };
    }

    public void restoreDew(int amount, int cooldown) {
        verdantDew = Math.max(0, Math.min(3, amount));
        dewCooldown = Math.max(0, Math.min(50, cooldown));
    }

    public void restoreStatuses(int electro, int burning, int quicken, int frozen, int superconduct) {
        electroChargedTicks = clampTicks(electro);
        burningTicks = clampTicks(burning);
        quickenTicks = clampTicks(quicken);
        frozenTicks = clampTicks(frozen);
        frozenHitsRemaining = frozenTicks > 0 ? FREEZE_HITS_TO_BREAK : 0;
        superconductTicks = clampTicks(superconduct);
    }

    private static int clampTicks(int ticks) {
        return Math.max(0, Math.min(1200, ticks));
    }

    public void apply(CarrierData.Element element, UUID applier) {
        apply(element, applier, ElementalReactionEngine.Mode.NORMAL);
    }

    public void apply(CarrierData.Element element, UUID applier, ElementalReactionEngine.Mode source) {
        if (element != null) {
            Aura old = auras.get(element);
            if (old == null || old.gauge() <= BASE_GAUGE) {
                auras.put(element, new Aura(BASE_GAUGE, DEFAULT_TICKS, applier,
                        source == null ? ElementalReactionEngine.Mode.NORMAL : source));
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
            auras.put(element, new Aura(gauge, ticks, old.applier(), old.source()));
        }
    }

    public void beginElectroCharged(UUID owner, int mastery) {
        electroChargedTicks = 80;
        electroOwner = owner;
        electroMastery = Math.max(0, mastery);
    }

    public void beginBurning(UUID owner, int mastery) {
        burningTicks = 100;
        burningOwner = owner;
        burningMastery = Math.max(0, mastery);
    }

    public record OngoingReaction(UUID owner, int mastery) {}

    public OngoingReaction electroChargedSource() {
        return new OngoingReaction(electroOwner, electroMastery);
    }

    public OngoingReaction burningSource() {
        return new OngoingReaction(burningOwner, burningMastery);
    }

    /** Restore attribution and EM for any still-active ongoing reactions. */
    public void restoreOngoingSources(String electroId, int electroEm, String burningId, int burningEm) {
        if (electroChargedTicks > 0) {
            electroOwner = parseOwner(electroId);
            electroMastery = Math.max(0, electroEm);
        }
        if (burningTicks > 0) {
            burningOwner = parseOwner(burningId);
            burningMastery = Math.max(0, burningEm);
        }
    }

    private static UUID parseOwner(String raw) {
        try {
            return UUID.fromString(raw);
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }

    public void beginQuicken() {
        quickenTicks = 160;
    }

    public void beginFrozen() {
        frozenTicks = 60;
        frozenHitsRemaining = FREEZE_HITS_TO_BREAK;
    }

    public int remainingFrozenHits() {
        return frozenHitsRemaining;
    }

    public void restoreFrozenHits(int hits) {
        frozenHitsRemaining = frozenTicks > 0 ? Math.max(1, Math.min(FREEZE_HITS_TO_BREAK, hits)) : 0;
    }

    /** Only actual landed weapon/projectile attacks count, never burning or DoT. */
    public boolean onAttackLanded() {
        if (frozenTicks <= 0) {
            return false;
        }
        if (--frozenHitsRemaining <= 0) {
            shatter();
            return true;
        }
        return false;
    }

    public void shatter() {
        frozenTicks = 0;
        frozenHitsRemaining = 0;
    }

    public void beginSuperconduct() {
        superconductTicks = 100;
    }

    public void restore(CarrierData.Element element, float gauge, int ticks, UUID applier,
                        ElementalReactionEngine.Mode source) {
        if (element != null && Float.isFinite(gauge) && gauge > 0f && gauge <= 16f
                && ticks > 0 && ticks <= 2400) {
            auras.put(element, new Aura(gauge, ticks, applier, source == null ? ElementalReactionEngine.Mode.NORMAL : source));
        }
    }

    public void clear() {
        auras.clear();
        icd.clear();
        electroChargedTicks = 0;
        burningTicks = 0;
        quickenTicks = 0;
        frozenTicks = 0;
        frozenHitsRemaining = 0;
        superconductTicks = 0;
        burningOwner = null;
        burningMastery = 0;
        electroOwner = null;
        electroMastery = 0;
        lunarChargedTicks = 0;
        lunarOwner = null;
        lunarMastery = 0;
        verdantDew = 0;
        dewCooldown = 0;
        moondriftTicks = 0;
        moondriftCount = 0;
        moondriftOwner = null;
        moondriftMastery = 0;
        stellarFieldTicks = 0;
        stellarFieldAnchor = false;
        stellarStacks = 0;
        stellarStackWindow = 0;
        stellarVortexTicks = 0;
        stellarVortexTriggers = 0;
        vortexOwner = null;
        vortexMastery = 0;
    }

    public void tick(LivingEntity entity) {
        if (!(entity.level() instanceof ServerLevel server)) {
            return;
        }
        if (!entity.isAlive()) {
            clear();
            return;
        }
        // Natural exposure refreshes a normal aura while contact lasts. It costs no damage
        // and subsequent elemental hits use the existing gauge/reaction engine.
        if (entity.tickCount % 40 == 0) {
            if (entity.isInLava() || entity.isOnFire()) {
                ElementalReactionEffects.onEnvironment(server, entity, CarrierData.Element.PYRO);
            } else if (entity.isInWaterOrRain()) {
                ElementalReactionEffects.onEnvironment(server, entity, CarrierData.Element.HYDRO);
            } else if (entity.getTicksFrozen() > 0
                    || (server.canSeeSky(entity.blockPosition())
                    && server.getBiome(entity.blockPosition()).value().getBaseTemperature() <= 0.15f)) {
                ElementalReactionEffects.onEnvironment(server, entity, CarrierData.Element.CRYO);
            }
        }
        icd.entrySet().removeIf(entry -> entity.tickCount - entry.getValue().lastApplicationTick() > 70);

        if (electroChargedTicks > 0) {
            electroChargedTicks--;
            if (!has(CarrierData.Element.HYDRO) || !has(CarrierData.Element.ELECTRO)) {
                electroChargedTicks = 0;
            } else {
                if (entity.tickCount % 10 == 0) {
                    server.sendParticles(ElementalParticles.reaction("electro_charged"),
                            entity.getX(), entity.getY() + entity.getBbHeight() * 0.65,
                            entity.getZ(), 2, 0.25, 0.28, 0.25, 0.012);
                }
                if (entity.tickCount % 20 == 0) {
                    consume(CarrierData.Element.HYDRO, 0.4f);
                    consume(CarrierData.Element.ELECTRO, 0.4f);
                    ElementalReactionEffects.dealDamage(server, entity, CarrierData.Element.ELECTRO,
                            electroOwner, ElementalReactionEffects.transformativeDamage(1.6f, electroMastery));
                }
            }
        }

        if (dewCooldown > 0) dewCooldown--;
        if (lunarChargedTicks > 0) {
            lunarChargedTicks--;
            if (!has(CarrierData.Element.HYDRO) || !has(CarrierData.Element.ELECTRO)) {
                lunarChargedTicks = 0;
            } else {
                double cloudY = entity.getY() + entity.getBbHeight() + 1.15;
                if (entity.tickCount % 4 == 0) {
                    // Vanilla Minecraft cloud and electric particles, no custom thundercloud entity.
                    server.sendParticles(ParticleTypes.CLOUD, entity.getX(), cloudY,
                            entity.getZ(), 13, 0.85, 0.18, 0.85, 0.012);
                    server.sendParticles(ParticleTypes.ELECTRIC_SPARK, entity.getX(), cloudY,
                            entity.getZ(), 4, 0.75, 0.16, 0.75, 0.015);
                }
                if (entity.tickCount % 40 == 0) {
                    //? if >=26.3 {
                    /*LightningBolt lightning = new LightningBolt(EntityTypes.LIGHTNING_BOLT, server);
                    *///?} else {
                    LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(server);
                    //?}
                    if (lightning != null) {
                        lightning.setPos(entity.getX(), entity.getY(), entity.getZ());
                        lightning.setVisualOnly(true);
                        server.addFreshEntity(lightning);
                    }
                    ElementalReactionEffects.lunarThundercloud(server, entity, lunarOwner, lunarMastery);
                }
            }
        }
        if (moondriftTicks > 0) {
            moondriftTicks--;
            if (entity.tickCount % 12 == 0) {
                server.sendParticles(ElementalParticles.reaction("lunar_moondrift"),
                        entity.getX(), entity.getY() + entity.getBbHeight() * 0.8, entity.getZ(),
                        3, 0.8, 0.3, 0.8, 0.015);
            }
            if (moondriftTicks == 0) {
                moondriftCount = 0;
                moondriftOwner = null;
                moondriftMastery = 0;
            }
        }
        if (stellarFieldTicks > 0) {
            if (stellarFieldAnchor && entity.tickCount % 8 == 0) {
                var zone = new net.minecraft.world.phys.AABB(
                        stellarFieldX - 4.5, stellarFieldY - 3.0, stellarFieldZ - 4.5,
                        stellarFieldX + 4.5, stellarFieldY + 5.0, stellarFieldZ + 4.5);
                for (LivingEntity nearby : server.getEntitiesOfClass(LivingEntity.class,
                        zone, LivingEntity::isAlive)) {
                    if (nearby != entity) {
                        ((ElementalAuraHolder) nearby).genshinecraft$auras()
                                .beginStellarField(nearby, false);
                    }
                }
            }
            stellarFieldTicks--;
            if (stellarFieldTicks == 0) stellarFieldAnchor = false;
            if (stellarStackWindow > 0) stellarStackWindow--;
            if (stellarStackWindow == 0) stellarStacks = 0;
            if (entity.tickCount % 12 == 0) {
                server.sendParticles(ElementalParticles.reaction("stellar_prism"),
                        entity.getX(), entity.getY() + 0.1, entity.getZ(),
                        5, 1.8, 0.08, 1.8, 0.01);
            }
        }
        if (stellarVortexTicks > 0) {
            stellarVortexTicks--;
            if (entity.tickCount % 4 == 0) {
                server.sendParticles(ElementalParticles.reaction("stellar_vortex"),
                        entity.getX(), entity.getY() + entity.getBbHeight() * 0.85, entity.getZ(),
                        stellarVortexTriggers >= 3 ? 8 : 4, 0.65, 0.4, 0.65, 0.03);
            }
            if (stellarVortexTicks == 0) {
                // Use one active tick for the explosion method, then clear the state.
                stellarVortexTicks = 1;
                detonateStellarVortex(server, entity);
            }
        }

        if (burningTicks > 0) {
            burningTicks--;
            if (entity.tickCount % 20 == 0) {
                ElementalReactionEffects.dealDamage(server, entity, CarrierData.Element.PYRO,
                        burningOwner, ElementalReactionEffects.transformativeDamage(1.2f, burningMastery));
                server.sendParticles(ElementalParticles.reaction("burning"),
                        entity.getX(), entity.getY() + entity.getBbHeight() * 0.6, entity.getZ(),
                        4, 0.2, 0.3, 0.2, 0.02);
            }
        }
        if (quickenTicks > 0) {
            quickenTicks--;
            if (entity.tickCount % 20 == 0) {
                server.sendParticles(ElementalParticles.reaction("quicken"),
                        entity.getX(), entity.getY() + entity.getBbHeight() * 0.7, entity.getZ(),
                        1, 0.25, 0.15, 0.25, 0.008);
            }
        }
        if (frozenTicks > 0) {
            frozenTicks--;
            // Server-authoritative immobility; LivingEntity/Mob/Player mixins stop
            // travel, jumps and attacks while this timer is active.
            entity.setDeltaMovement(0, 0, 0);
            entity.setTicksFrozen(Math.max(entity.getTicksFrozen(), entity.getTicksRequiredToFreeze()));
            if (entity.tickCount % 12 == 0) {
                server.sendParticles(ElementalParticles.reaction("frozen"),
                        entity.getX(), entity.getY() + entity.getBbHeight() * 0.65,
                        entity.getZ(), 2, 0.25, 0.25, 0.25, 0.009);
            }
            if (frozenTicks == 0) {
                frozenHitsRemaining = 0;
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
            active.setValue(new Aura(gauge, remaining, old.applier(), old.source()));
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
        AuraIconHolder icons = (AuraIconHolder) entity;
        int mask = AuraStatusIcons.mask(auras.keySet());
        icons.genshinecraft$setAuraIcons(mask);
        if (mask == 0) {
            icons.genshinecraft$setBossBarId("");
        } else if (entity.tickCount % 10 == 0) {
            icons.genshinecraft$setBossBarId(BossBarAssociation.id(server, entity));
        }
    }
}
