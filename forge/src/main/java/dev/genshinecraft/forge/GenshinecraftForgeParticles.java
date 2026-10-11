package dev.genshinecraft.forge;

import dev.genshinecraft.Genshinecraft;
import dev.genshinecraft.client.ElementalSparkParticle;
import dev.genshinecraft.client.ElementalAuraParticle;
import dev.genshinecraft.particle.ElementalParticles;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Forge registers sprite-backed particles on its client-only mod event bus. */
@Mod.EventBusSubscriber(modid = Genshinecraft.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class GenshinecraftForgeParticles {
    private GenshinecraftForgeParticles() {
    }

    @SubscribeEvent
    public static void register(RegisterParticleProvidersEvent event) {
        for (var particleType : ElementalParticles.SPARKS.values()) {
            event.registerSpriteSet(particleType.get(), sprites ->
                    (type, level, x, y, z, vx, vy, vz) ->
                            new ElementalSparkParticle(level, x, y, z, vx, vy, vz, sprites));
        }
        for (var particleType : ElementalParticles.AURAS.values()) {
            event.registerSpriteSet(particleType.get(), sprites ->
                    (type, level, x, y, z, vx, vy, vz) ->
                            new ElementalAuraParticle(level, x, y, z, vx, vy, vz, sprites));
        }
    }
}
