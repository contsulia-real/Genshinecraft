package dev.genshinecraft.client;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SpriteSet;
//? if >=26.3 {
/*import net.minecraft.client.particle.SingleQuadParticle;
*///?} else {
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.particle.ParticleRenderType;
//?}

/** Subtle rising aura motes (distinct from the instantaneous hit sparks). */
public final class ElementalAuraParticle
        //? if >=26.3 {
        /*extends SingleQuadParticle {
        *///?} else {
        extends TextureSheetParticle {
        //?}
    private final SpriteSet sprites;

    public ElementalAuraParticle(ClientLevel level, double x, double y, double z,
                                 double vx, double vy, double vz, SpriteSet sprites) {
        //? if >=26.3 {
        /*super(level, x, y, z, vx, vy, vz, sprites.first());
        *///?} else {
        super(level, x, y, z, vx, vy, vz);
        //?}
        this.sprites = sprites;
        this.lifetime = 18 + random.nextInt(11);
        this.quadSize = 0.075f + random.nextFloat() * 0.045f;
        this.xd = vx * 0.4;
        this.yd = 0.012 + Math.abs(vy) * 0.3;
        this.zd = vz * 0.4;
        this.gravity = 0f;
        this.friction = 0.92f;
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
            setAlpha(Math.max(0f, 0.72f * (1f - (float) age / (float) lifetime)));
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
