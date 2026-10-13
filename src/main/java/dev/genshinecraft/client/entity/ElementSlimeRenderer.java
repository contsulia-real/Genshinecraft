package dev.genshinecraft.client.entity;

import dev.genshinecraft.item.CarrierData;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.SlimeRenderer;
//? if >=26.3 {
/*import net.minecraft.client.renderer.entity.state.SlimeRenderState;
import net.minecraft.resources.Identifier;
*///?} else {
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Slime;
//?}

/** The vanilla Slime model/animation with one distinct elemental slime texture per type. */
public final class ElementSlimeRenderer extends SlimeRenderer {
    //? if >=26.3 {
    /*private final Identifier texture;
    *///?} else {
    private final ResourceLocation texture;
    //?}
    public ElementSlimeRenderer(EntityRendererProvider.Context context, CarrierData.Element element) {
        super(context);
        String name = "textures/entity/slime_" + element.name().toLowerCase(java.util.Locale.ROOT) + ".png";
        //? if >=26.3 {
        /*texture = Identifier.fromNamespaceAndPath("genshinecraft", name);
        *///?} else if =1.21.1 {
        texture = ResourceLocation.fromNamespaceAndPath("genshinecraft", name);
        //?} else {
        /*texture = new ResourceLocation("genshinecraft", name);
        *///?}
    }
    //? if >=26.3 {
    /*@Override
    public Identifier getTextureLocation(SlimeRenderState slime) {
        return texture;
    }
    *///?} else {
    @Override
    public ResourceLocation getTextureLocation(Slime slime) {
        return texture;
    }
    //?}
}
