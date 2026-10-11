package dev.genshinecraft.particle;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import dev.genshinecraft.Genshinecraft;
import dev.genshinecraft.item.CarrierData;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;

import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;

/** Eight-pixel elemental spark shapes, shared by later hit and aura effects. */
public final class ElementalParticles {
    private static final DeferredRegister<ParticleType<?>> PARTICLES =
            DeferredRegister.create(Genshinecraft.MOD_ID, Registries.PARTICLE_TYPE);

    public static final Map<CarrierData.Element, RegistrySupplier<SimpleParticleType>> SPARKS = registerSparks();

    private ElementalParticles() {
    }

    private static Map<CarrierData.Element, RegistrySupplier<SimpleParticleType>> registerSparks() {
        Map<CarrierData.Element, RegistrySupplier<SimpleParticleType>> result =
                new EnumMap<>(CarrierData.Element.class);
        for (CarrierData.Element element : CarrierData.Element.values()) {
            String id = "element_" + element.name().toLowerCase(Locale.ROOT);
            result.put(element, PARTICLES.register(id, () -> new SimpleParticleType(false) {
            }));
        }
        return result;
    }

    public static void init() {
        PARTICLES.register();
    }
}
