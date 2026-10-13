package dev.genshinecraft.world;

import dev.genshinecraft.item.CarrierData;
import dev.genshinecraft.item.CarrierItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;

/** Uses the biome at the statue, not the biome where the player happens to stand. */
public final class StatueRewardStyles {
    private StatueRewardStyles() {
    }

    public static Item choose(ServerLevel level, BlockPos pos, CarrierData.Element element) {
        Holder<Biome> biome = level.getBiome(pos);
        if (isSnowOrIce(biome)) {
            return switch (level.getRandom().nextInt(3)) {
                case 0 -> CarrierItems.visionForRegion(element, "snezhnaya");
                case 1 -> CarrierItems.MOON_WHEELS.get(element).get();
                default -> CarrierItems.STAR_WEDGES.get(element).get();
            };
        }
        return CarrierItems.visionForRegion(element, visionRegion(biome));
    }

    private static boolean isSnowOrIce(Holder<Biome> biome) {
        return biome.is(Biomes.SNOWY_PLAINS) || biome.is(Biomes.ICE_SPIKES)
                || biome.is(Biomes.SNOWY_TAIGA) || biome.is(Biomes.GROVE)
                || biome.is(Biomes.SNOWY_SLOPES) || biome.is(Biomes.FROZEN_PEAKS)
                || biome.is(Biomes.JAGGED_PEAKS) || biome.is(Biomes.FROZEN_RIVER)
                || biome.is(Biomes.FROZEN_OCEAN) || biome.is(Biomes.DEEP_FROZEN_OCEAN)
                || biome.is(Biomes.SNOWY_BEACH);
    }

    private static String visionRegion(Holder<Biome> biome) {
        if (biome.is(Biomes.CHERRY_GROVE)) {
            return "inazuma";
        }
        if (biome.is(Biomes.BAMBOO_JUNGLE)) {
            return "liyue";
        }
        if (biome.is(Biomes.DESERT) || biome.is(BiomeTags.IS_JUNGLE)) {
            return "sumeru";
        }
        if (biome.is(BiomeTags.IS_SAVANNA) || biome.is(BiomeTags.IS_BADLANDS)) {
            return "natlan";
        }
        if (biome.is(BiomeTags.IS_OCEAN) || biome.is(BiomeTags.IS_RIVER)
                || biome.is(BiomeTags.IS_BEACH) || biome.is(Biomes.SWAMP)
                || biome.is(Biomes.MANGROVE_SWAMP)) {
            return "fontaine";
        }
        if (biome.is(Biomes.STONY_PEAKS) || biome.is(Biomes.WINDSWEPT_HILLS)
                || biome.is(Biomes.WINDSWEPT_GRAVELLY_HILLS)) {
            return "liyue";
        }
        return "mondstadt";
    }
}
