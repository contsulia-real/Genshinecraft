package dev.genshinecraft.element;

import com.mojang.math.Transformation;
import dev.genshinecraft.Genshinecraft;
import dev.genshinecraft.mixin.DamageNumberDisplayAccessor;
import dev.genshinecraft.mixin.DamageNumberTextAccessor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Display;
//? if >=26.3 {
/*import net.minecraft.network.chat.FontDescription;
import net.minecraft.resources.Identifier;
*///?} else {
import net.minecraft.resources.ResourceLocation;
//?}
import org.joml.Quaternionf;
import org.joml.Vector3f;

/** Bitmap-font sprites allow genuine registered entities to use textured billboards. */
final class ElementalDisplaySprites {
    private ElementalDisplaySprites() {
    }

    static void apply(Display.TextDisplay entity, char glyph) {
        //? if >=26.3 {
        /*Component sprite = Component.literal(String.valueOf(glyph)).withStyle(style ->
                style.withFont(new FontDescription.Resource(
                        Identifier.fromNamespaceAndPath(Genshinecraft.MOD_ID, "elemental_entities"))));
        *///?} else if =1.21.1 {
        Component sprite = Component.literal(String.valueOf(glyph)).withStyle(style ->
                style.withFont(ResourceLocation.fromNamespaceAndPath(
                        Genshinecraft.MOD_ID, "elemental_entities")));
        //?} else {
        /*Component sprite = Component.literal(String.valueOf(glyph)).withStyle(style ->
                style.withFont(new ResourceLocation(Genshinecraft.MOD_ID, "elemental_entities")));
        *///?}
        ((DamageNumberTextAccessor) entity).genshinecraft$text(sprite);
        ((DamageNumberTextAccessor) entity).genshinecraft$background(0);
        ((DamageNumberDisplayAccessor) entity).genshinecraft$billboard(Display.BillboardConstraints.CENTER);
        ((DamageNumberDisplayAccessor) entity).genshinecraft$transformation(
                new Transformation(new Vector3f(), new Quaternionf(),
                        new Vector3f(1.4f, 1.4f, 1.4f), new Quaternionf()));
    }
}
