package dev.genshinecraft.particle;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import dev.genshinecraft.Genshinecraft;
import dev.genshinecraft.item.CarrierData;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;

import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/** Native animated elemental FX, orbs and emblems. */
public final class ElementalParticles {
    private static final DeferredRegister<ParticleType<?>> PARTICLES =
            DeferredRegister.create(Genshinecraft.MOD_ID, Registries.PARTICLE_TYPE);

    public static final Map<CarrierData.Element, RegistrySupplier<SimpleParticleType>> SPARKS = registerSparks();

    public static final Map<CarrierData.Element, RegistrySupplier<SimpleParticleType>> AURAS = registerAuras();

    public static final Map<CarrierData.Element, RegistrySupplier<SimpleParticleType>> EMBLEMS = registerEmblems();

    /** Each reaction has its own 16px sprite sequence and native client provider. */
    public static final Map<String, RegistrySupplier<SimpleParticleType>> REACTIONS = registerReactions();

    private static Map<String, RegistrySupplier<SimpleParticleType>> registerReactions() {
        Map<String, RegistrySupplier<SimpleParticleType>> effects = new LinkedHashMap<>();
        for (String id : new String[]{
                "overloaded", "superconduct", "electro_charged", "frozen", "shatter",
                "burning", "bloom", "burgeon", "hyperbloom",
                "quicken", "aggravate", "spread", "vaporize", "melt",
                "swirl_pyro", "swirl_hydro", "swirl_electro", "swirl_cryo",
                "crystallize_pyro", "crystallize_hydro", "crystallize_electro", "crystallize_cryo",
                "lunar_charged", "lunar_bloom", "lunar_crystallize", "lunar_dew", "lunar_moondrift",
                "stellar_conduct", "stellar_swirl", "stellar_vortex", "stellar_prism"}) {
            effects.put(id, PARTICLES.register("reaction_" + id, () -> new SimpleParticleType(true) {
            }));
        }
        return Map.copyOf(effects);
    }

    public static SimpleParticleType reaction(String id) {
        RegistrySupplier<SimpleParticleType> type = REACTIONS.get(id);
        if (type == null) {
            throw new IllegalArgumentException("Unknown elemental reaction particle: " + id);
        }
        return type.get();
    }

    private ElementalParticles() {
    }

    private static Map<CarrierData.Element, RegistrySupplier<SimpleParticleType>> registerSparks() {
        Map<CarrierData.Element, RegistrySupplier<SimpleParticleType>> result =
                new EnumMap<>(CarrierData.Element.class);
        for (CarrierData.Element element : CarrierData.Element.values()) {
            String id = "element_" + element.name().toLowerCase(Locale.ROOT);
            result.put(element, PARTICLES.register(id, () -> new SimpleParticleType(true) {
            }));
        }
        return result;
    }

    private static Map<CarrierData.Element, RegistrySupplier<SimpleParticleType>> registerAuras() {
        Map<CarrierData.Element, RegistrySupplier<SimpleParticleType>> result =
                new EnumMap<>(CarrierData.Element.class);
        for (CarrierData.Element element : CarrierData.Element.values()) {
            String id = "attach_" + element.name().toLowerCase(Locale.ROOT);
            result.put(element, PARTICLES.register(id, () -> new SimpleParticleType(true) {
            }));
        }
        return result;
    }

    private static Map<CarrierData.Element, RegistrySupplier<SimpleParticleType>> registerEmblems() {
        Map<CarrierData.Element, RegistrySupplier<SimpleParticleType>> result =
                new EnumMap<>(CarrierData.Element.class);
        for (CarrierData.Element element : CarrierData.Element.values()) {
            String id = "emblem_" + element.name().toLowerCase(Locale.ROOT);
            result.put(element, PARTICLES.register(id, () -> new SimpleParticleType(true) {
            }));
        }
        return result;
    }

    public static void init() {
        PARTICLES.register();
    }
}
