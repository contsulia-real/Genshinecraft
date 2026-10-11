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
    private final boolean emblem;

    public ElementalAuraParticle(ClientLevel level, double x, double y, double z,
                                 double vx, double vy, double vz, SpriteSet sprites) {
        this(level, x, y, z, vx, vy, vz, sprites, false);
    }

    public ElementalAuraParticle(ClientLevel level, double x, double y, double z,
                                 double vx, double vy, double vz, SpriteSet sprites, boolean emblem) {
        //? if >=26.3 {
        /*super(level, x, y, z, vx, vy, vz, sprites.first());
        *///?} else {
        super(level, x, y, z, vx, vy, vz);
        //?}
        this.sprites = sprites;
        this.emblem = emblem;
        this.lifetime = emblem ? 26 + random.nextInt(9) : 18 + random.nextInt(11);
        this.quadSize = emblem ? 0.16f + random.nextFloat() * 0.05f
                : 0.095f + random.nextFloat() * 0.045f;
        this.xd = vx * 0.4;
        this.yd = (emblem ? 0.004 : 0.012) + Math.abs(vy) * 0.3;
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
            // Preserve the supplied texture's strong color. Fade only during the last third.
            float fade = Math.min(1f, (lifetime - age) / (lifetime * 0.33f));
            setAlpha(Math.max(0f, 0.98f * fade));
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
