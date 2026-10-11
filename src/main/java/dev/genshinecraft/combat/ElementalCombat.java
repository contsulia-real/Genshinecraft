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
import net.minecraft.world.entity.projectile.Projectile;
import java.util.UUID;
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

    /** The element was validated at launch; verify its original carrier still exists on impact. */
    public static DamageSource projectileSource(Projectile projectile, DamageSource vanilla) {
        if (!(projectile instanceof ElementalProjectile bound)
                || !(projectile.getOwner() instanceof Player player)) {
            return vanilla;
        }
        UUID carrierId = bound.genshinecraft$carrierId();
        CarrierData.Element element = bound.genshinecraft$element();
        if (carrierId == null || element == null) {
            return vanilla;
        }
        var inventory = player.getInventory();
        boolean valid = false;
        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            CarrierData data = CarrierData.read(inventory.getItem(slot));
            if (data != null && data.id().equals(carrierId)
                    && data.element() == element && data.usableBy(player.getUUID())) {
                valid = true;
                break;
            }
        }
        if (!valid) {
            return vanilla;
        }
        ResourceKey<DamageType> type = ATTACK_TYPES.get(element);
        //? if >=26.3 {
        /*var holder = player.level().registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE)
                .getOrThrow(type);
        *///?} else {
        var holder = player.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
                .getHolderOrThrow(type);
        //?}
        return new DamageSource(holder, projectile, player);
    }

    public static void onMeleeHit(Player player, Entity target, DamageSource source) {
        spawnHitParticles(player, target, source);
    }

    public static void onProjectileHit(Projectile projectile, Entity target, DamageSource source) {
        if (projectile.getOwner() instanceof Player player) {
            spawnHitParticles(player, target, source);
        }
    }

    private static void spawnHitParticles(Player player, Entity target, DamageSource source) {
        if (!(player.level() instanceof ServerLevel server)) {
            return;
        }
        for (CarrierData.Element element : CarrierData.Element.values()) {
            if (!source.is(ATTACK_TYPES.get(element))) {
                continue;
            }
            // Keep the near-side placement, but restore the original subtle burst.
            double dx = player.getX() - target.getX();
            double dz = player.getZ() - target.getZ();
            double distance = Math.sqrt(dx * dx + dz * dz);
            if (distance < 0.001) {
                dx = 0.0;
                dz = 1.0;
                distance = 1.0;
            }
            double surface = target.getBbWidth() * 0.5 + 0.18;
            double x = target.getX() + dx / distance * surface;
            double y = target.getY() + target.getBbHeight() * 0.55;
            double z = target.getZ() + dz / distance * surface;
            server.sendParticles(ElementalParticles.SPARKS.get(element).get(),
                    x, y, z, 8, 0.16, 0.26, 0.16, 0.045);
            return;
        }
    }
}
