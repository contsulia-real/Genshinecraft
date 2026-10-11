package dev.genshinecraft.client;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SpriteSet;
//? if >=26.3 {
/*import net.minecraft.client.particle.SingleQuadParticle;
*///?} else {
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.particle.ParticleRenderType;
//?}

/** Dedicated four-frame reaction effects, independent of attack and aura sprites. */
public final class ElementalReactionParticle
        //? if >=26.3 {
        /*extends SingleQuadParticle {
        *///?} else {
        extends TextureSheetParticle {
        //?}
    private final SpriteSet sprites;

    public ElementalReactionParticle(ClientLevel level, double x, double y, double z,
                                     double vx, double vy, double vz, SpriteSet sprites,
                                     boolean lingering) {
        //? if >=26.3 {
        /*super(level, x, y, z, vx, vy, vz, sprites.first());
        *///?} else {
        super(level, x, y, z, vx, vy, vz);
        //?}
        this.sprites = sprites;
        this.lifetime = lingering ? 20 + random.nextInt(9) : 13 + random.nextInt(8);
        this.quadSize = lingering ? 0.095f + random.nextFloat() * 0.045f
                : 0.15f + random.nextFloat() * 0.07f;
        this.xd = vx * 0.6;
        this.yd = lingering ? Math.abs(vy) * 0.15 + 0.007 : vy * 0.7;
        this.zd = vz * 0.6;
        this.gravity = 0f;
        this.friction = lingering ? 0.93f : 0.88f;
        this.hasPhysics = false;
        //? if >=26.3 {
        /*setSpriteFromAge(sprites);
        *///?} else {
        pickSprite(sprites);
        //?}
    }

    @Override
    public void tick() {
        super.tick();
        if (!removed) {
            setSpriteFromAge(sprites);
            float fade = Math.min(1f, (lifetime - age) / (lifetime * 0.30f));
            setAlpha(Math.max(0f, fade));
        }
    }

    //? if >=26.3 {
    /*@Override
    public SingleQuadParticle.Layer getLayer() {
        return SingleQuadParticle.Layer.TRANSLUCENT;
    }
    *///?} else {
    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }
    //?}
}
