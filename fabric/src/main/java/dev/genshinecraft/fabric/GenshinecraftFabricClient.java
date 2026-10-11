package dev.genshinecraft.fabric;

import dev.genshinecraft.client.ElementalSparkParticle;
import dev.genshinecraft.client.ElementalAuraParticle;
import dev.genshinecraft.particle.ElementalParticles;
import net.fabricmc.api.ClientModInitializer;
//? if >=26.3 {
/*import net.fabricmc.fabric.api.client.particle.v1.ParticleProviderRegistry;
*///?} else {
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
//?}

/** Register the actual client particle providers with Fabric's particle engine. */
public final class GenshinecraftFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        for (var particleType : ElementalParticles.SPARKS.values()) {
            //? if >=26.3 {
            /*ParticleProviderRegistry.getInstance().register(particleType.get(), sprites ->
                    (type, level, x, y, z, vx, vy, vz, random) ->
                            new ElementalSparkParticle(level, x, y, z, vx, vy, vz, sprites));
            *///?} else {
            ParticleFactoryRegistry.getInstance().register(particleType.get(), sprites ->
                    (type, level, x, y, z, vx, vy, vz) ->
                            new ElementalSparkParticle(level, x, y, z, vx, vy, vz, sprites));
            //?}
        }
        for (var particleType : ElementalParticles.AURAS.values()) {
            //? if >=26.3 {
            /*ParticleProviderRegistry.getInstance().register(particleType.get(), sprites ->
                    (type, level, x, y, z, vx, vy, vz, random) ->
                            new ElementalAuraParticle(level, x, y, z, vx, vy, vz, sprites));
            *///?} else {
            ParticleFactoryRegistry.getInstance().register(particleType.get(), sprites ->
                    (type, level, x, y, z, vx, vy, vz) ->
                            new ElementalAuraParticle(level, x, y, z, vx, vy, vz, sprites));
            //?}
        }
        for (var particleType : ElementalParticles.EMBLEMS.values()) {
            //? if >=26.3 {
            /*ParticleProviderRegistry.getInstance().register(particleType.get(), sprites ->
                    (type, level, x, y, z, vx, vy, vz, random) ->
                            new ElementalAuraParticle(level, x, y, z, vx, vy, vz, sprites, true));
            *///?} else {
            ParticleFactoryRegistry.getInstance().register(particleType.get(), sprites ->
                    (type, level, x, y, z, vx, vy, vz) ->
                            new ElementalAuraParticle(level, x, y, z, vx, vy, vz, sprites, true));
            //?}
        }
    }
}
