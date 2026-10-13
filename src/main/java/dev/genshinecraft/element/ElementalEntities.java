package dev.genshinecraft.element;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import dev.genshinecraft.Genshinecraft;
import dev.genshinecraft.item.CarrierData;
import dev.architectury.registry.level.entity.EntityAttributeRegistry;
import dev.architectury.registry.level.entity.SpawnPlacementsRegistry;
import dev.architectury.registry.level.biome.BiomeModifications;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.entity.SpawnPlacements;
//? if >=1.21.1 {
import net.minecraft.world.entity.SpawnPlacementTypes;
//?}
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Monster;
import java.util.EnumMap;
import java.util.Map;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
//? if >=26.3 {
/*import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
*///?}

/** Each elemental reaction object is an independent registered entity type. */
public final class ElementalEntities {
    private static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(Genshinecraft.MOD_ID, Registries.ENTITY_TYPE);

    public static final RegistrySupplier<EntityType<ElementalBloomCore>> DENDRO_CORE =
            ENTITIES.register("dendro_core", () -> EntityType.Builder
                    .of(ElementalBloomCore::new, MobCategory.MISC)
                    .sized(0.7f, 0.7f).clientTrackingRange(8).updateInterval(1)
                    //? if >=26.3 {
                    /*.build(ResourceKey.create(Registries.ENTITY_TYPE,
                            Identifier.fromNamespaceAndPath(Genshinecraft.MOD_ID, "dendro_core"))));
                    *///?} else {
                    .build(Genshinecraft.MOD_ID + ":dendro_core"));
                    //?}

    public static final RegistrySupplier<EntityType<ElementalCrystals>> CRYSTAL_SHARD =
            ENTITIES.register("crystal_shard", () -> EntityType.Builder
                    .of(ElementalCrystals::new, MobCategory.MISC)
                    .sized(0.55f, 0.65f).clientTrackingRange(8).updateInterval(1)
                    //? if >=26.3 {
                    /*.build(ResourceKey.create(Registries.ENTITY_TYPE,
                            Identifier.fromNamespaceAndPath(Genshinecraft.MOD_ID, "crystal_shard"))));
                    *///?} else {
                    .build(Genshinecraft.MOD_ID + ":crystal_shard"));
                    //?}


    public static final Map<CarrierData.Element, RegistrySupplier<EntityType<ElementSlime>>> SLIMES =
            registerSlimes();

    private static Map<CarrierData.Element, RegistrySupplier<EntityType<ElementSlime>>> registerSlimes() {
        Map<CarrierData.Element, RegistrySupplier<EntityType<ElementSlime>>> result =
                new EnumMap<>(CarrierData.Element.class);
        for (CarrierData.Element element : CarrierData.Element.values()) {
            String path = "slime_" + element.name().toLowerCase(java.util.Locale.ROOT);
            result.put(element, ENTITIES.register(path, () -> EntityType.Builder
                    .<ElementSlime>of((type, level) -> new ElementSlime(type, level, element), MobCategory.MONSTER)
                    //? if =1.20.1 {
                    /*.sized(2.04f, 2.04f)
                    *///?} else {
                    .sized(0.52f, 0.52f)
                    //?}
                    .clientTrackingRange(8).updateInterval(2)
                    //? if >=26.3 {
                    /*.build(ResourceKey.create(Registries.ENTITY_TYPE,
                            Identifier.fromNamespaceAndPath(Genshinecraft.MOD_ID, path)))));
                    *///?} else {
                    .build(Genshinecraft.MOD_ID + ":" + path)));
                    //?}
        }
        return result;
    }

    private ElementalEntities() {
    }

    public static void init() {
        ENTITIES.register();
        for (var entry : SLIMES.entrySet()) {
            CarrierData.Element element = entry.getKey();
            var type = entry.getValue();
            EntityAttributeRegistry.register(type::get, Monster::createMonsterAttributes);
            SpawnPlacementsRegistry.register(type::get,
                    //? if =1.20.1 {
                    /*SpawnPlacements.Type.ON_GROUND,
                    *///?} else {
                    SpawnPlacementTypes.ON_GROUND,
                    //?}
                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    (entityType, level, reason, pos, random) ->
                            Mob.checkMobSpawnRules(entityType, level, reason, pos, random)
                                    && ElementSlime.canSpawn(element, level, pos));
        }
        BiomeModifications.addProperties(ctx -> ctx.hasTag(BiomeTags.IS_OVERWORLD), (ctx, biome) -> {
            for (var type : SLIMES.values()) {
                biome.getSpawnProperties().addSpawn(MobCategory.MONSTER,
                        //? if >=26.3 {
                        /*new MobSpawnSettings.SpawnerData(type.get(),
                                net.minecraft.util.valueproviders.UniformInt.of(1, 3)), 1);
                        *///?} else {
                        new MobSpawnSettings.SpawnerData(type.get(), 1, 1, 3));
                        //?}
            }
        });
    }
}
