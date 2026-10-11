package dev.genshinecraft.element;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import dev.genshinecraft.Genshinecraft;
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

    private ElementalEntities() {
    }

    public static void init() {
        ENTITIES.register();
    }
}
