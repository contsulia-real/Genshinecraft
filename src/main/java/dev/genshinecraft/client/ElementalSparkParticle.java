package dev.genshinecraft.client;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SpriteSet;
//? if >=26.3 {
/*import net.minecraft.client.particle.SingleQuadParticle;
*///?} else {
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.particle.ParticleRenderType;
//?}

/** Short-lived sprite effect. Its silhouette and colour are authored in 8x8 PNG frames. */
public final class ElementalSparkParticle
        //? if >=26.3 {
        /*extends SingleQuadParticle {
        *///?} else {
        extends TextureSheetParticle {
        //?}
    private final SpriteSet sprites;

    public ElementalSparkParticle(ClientLevel level, double x, double y, double z,
                           double vx, double vy, double vz, SpriteSet sprites) {
        //? if >=26.3 {
        /*super(level, x, y, z, vx, vy, vz, sprites.first());
        *///?} else {
        super(level, x, y, z, vx, vy, vz);
        //?}
        this.sprites = sprites;
        this.lifetime = 12 + this.random.nextInt(7);
        this.quadSize = 0.22f + this.random.nextFloat() * 0.12f;
        this.friction = 0.87f;
        this.gravity = 0.025f;
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
        if (!this.removed) {
            this.setSpriteFromAge(sprites);
            this.setAlpha(Math.max(0f, 1f - (float) age / (float) lifetime));
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
