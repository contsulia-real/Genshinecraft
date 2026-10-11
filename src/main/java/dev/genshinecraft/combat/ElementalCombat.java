package dev.genshinecraft.combat;

import dev.genshinecraft.Genshinecraft;
import dev.genshinecraft.item.CarrierData;
import dev.genshinecraft.item.WeaponBinding;
import dev.genshinecraft.particle.ElementalParticles;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
//? if >=26.3 {
/*import net.minecraft.resources.Identifier;
*///?} else if =1.21.1 {
import net.minecraft.resources.ResourceLocation;
//?} else {
/*import net.minecraft.resources.ResourceLocation;
*///?}
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;

/** Replaces the vanilla melee damage source, never the attack or damage calculation. */
public final class ElementalCombat {
    private static final Map<CarrierData.Element, ResourceKey<DamageType>> ATTACK_TYPES = createDamageTypes();

    private ElementalCombat() {
    }

    private static Map<CarrierData.Element, ResourceKey<DamageType>> createDamageTypes() {
        Map<CarrierData.Element, ResourceKey<DamageType>> result = new EnumMap<>(CarrierData.Element.class);
        for (CarrierData.Element element : CarrierData.Element.values()) {
            String id = "element_" + element.name().toLowerCase(Locale.ROOT) + "_attack";
            //? if >=26.3 {
            /*result.put(element, ResourceKey.create(Registries.DAMAGE_TYPE,
                    Identifier.fromNamespaceAndPath(Genshinecraft.MOD_ID, id)));
            *///?} else if =1.21.1 {
            result.put(element, ResourceKey.create(Registries.DAMAGE_TYPE,
                    ResourceLocation.fromNamespaceAndPath(Genshinecraft.MOD_ID, id)));
            //?} else {
            /*result.put(element, ResourceKey.create(Registries.DAMAGE_TYPE,
                    new ResourceLocation(Genshinecraft.MOD_ID, id)));
            *///?}
        }
        return result;
    }

    public static DamageSource meleeSource(Player player, ItemStack weapon, DamageSource vanilla) {
        CarrierData data = WeaponBinding.resolve(weapon, player);
        if (data == null) {
            return vanilla;
        }
        ResourceKey<DamageType> type = ATTACK_TYPES.get(data.element());
        //? if >=26.3 {
        /*var holder = player.level().registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE)
                .getOrThrow(type);
        *///?} else {
        var holder = player.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
                .getHolderOrThrow(type);
        //?}
        return new DamageSource(holder, player);
    }

    public static void onMeleeHit(Player player, Entity target, DamageSource source) {
        if (!(player.level() instanceof ServerLevel server)) {
            return;
        }
        CarrierData data = WeaponBinding.resolve(player.getMainHandItem(), player);
        if (data == null || !source.is(ATTACK_TYPES.get(data.element()))) {
            return;
        }
        server.sendParticles(ElementalParticles.SPARKS.get(data.element()).get(),
                target.getX(), target.getY() + target.getBbHeight() * 0.5, target.getZ(),
                8, 0.22, 0.30, 0.22, 0.045);
    }
}
