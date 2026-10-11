package dev.genshinecraft.neoforge;

import dev.genshinecraft.Genshinecraft;
import dev.genshinecraft.client.ElementalSparkParticle;
import dev.genshinecraft.client.ElementalAuraParticle;
import dev.genshinecraft.particle.ElementalParticles;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

/** NeoForge registers sprite-backed particles on its client-only mod event bus. */
//? if >=26.3 {
/*@EventBusSubscriber(modid = Genshinecraft.MOD_ID, value = Dist.CLIENT)
*///?} else {
@EventBusSubscriber(modid = Genshinecraft.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
//?}
public final class GenshinecraftNeoForgeParticles {
    private GenshinecraftNeoForgeParticles() {
    }

    @SubscribeEvent
    public static void register(RegisterParticleProvidersEvent event) {
        for (var particleType : ElementalParticles.SPARKS.values()) {
            //? if >=26.3 {
            /*event.registerSpriteSet(particleType.get(), sprites ->
                    (type, level, x, y, z, vx, vy, vz, random) ->
                            new ElementalSparkParticle(level, x, y, z, vx, vy, vz, sprites));
            *///?} else {
            event.registerSpriteSet(particleType.get(), sprites ->
                    (type, level, x, y, z, vx, vy, vz) ->
                            new ElementalSparkParticle(level, x, y, z, vx, vy, vz, sprites));
            //?}
        }
        for (var particleType : ElementalParticles.AURAS.values()) {
            //? if >=26.3 {
            /*event.registerSpriteSet(particleType.get(), sprites ->
                    (type, level, x, y, z, vx, vy, vz, random) ->
                            new ElementalAuraParticle(level, x, y, z, vx, vy, vz, sprites));
            *///?} else {
            event.registerSpriteSet(particleType.get(), sprites ->
                    (type, level, x, y, z, vx, vy, vz) ->
                            new ElementalAuraParticle(level, x, y, z, vx, vy, vz, sprites));
            //?}
        }
        for (var particleType : ElementalParticles.EMBLEMS.values()) {
            //? if >=26.3 {
            /*event.registerSpriteSet(particleType.get(), sprites ->
                    (type, level, x, y, z, vx, vy, vz, random) ->
                            new ElementalAuraParticle(level, x, y, z, vx, vy, vz, sprites, true));
            *///?} else {
            event.registerSpriteSet(particleType.get(), sprites ->
                    (type, level, x, y, z, vx, vy, vz) ->
                            new ElementalAuraParticle(level, x, y, z, vx, vy, vz, sprites, true));
            //?}
        }
    }
}
