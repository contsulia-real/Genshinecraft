package dev.genshinecraft.fabric;

import dev.genshinecraft.Genshinecraft;
import dev.genshinecraft.item.CarrierData;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
//? if >=26.3 {
/*import net.minecraft.resources.Identifier;
*///?} else {
import net.minecraft.resources.ResourceLocation;
//?}

/** Inject the data-driven ore placed features into overworld biomes on Fabric. */
public final class ElementOreWorldgen {
    private ElementOreWorldgen() {
    }

    public static void init() {
        for (CarrierData.Element element : CarrierData.Element.values()) {
            String name = "ore_" + element.name().toLowerCase(java.util.Locale.ROOT);
            //? if >=26.3 {
            /*var id = Identifier.fromNamespaceAndPath(Genshinecraft.MOD_ID, name);
            *///?} else if =1.21.1 {
            var id = ResourceLocation.fromNamespaceAndPath(Genshinecraft.MOD_ID, name);
            //?} else {
            /*var id = new ResourceLocation(Genshinecraft.MOD_ID, name);
            *///?}
            BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(),
                    GenerationStep.Decoration.UNDERGROUND_ORES,
                    ResourceKey.create(Registries.PLACED_FEATURE, id));
        }
    }
}
