package dev.genshinecraft.combat;

import dev.genshinecraft.Genshinecraft;
import dev.genshinecraft.item.CarrierData;
import dev.genshinecraft.item.CarrierItems;
import dev.genshinecraft.item.WeaponBinding;
import dev.genshinecraft.particle.ElementalParticles;
import dev.genshinecraft.element.ElementalAuraHolder;
import dev.genshinecraft.element.ElementalReactionEffects;
import dev.genshinecraft.element.ElementalReactionEngine;
import dev.genshinecraft.element.ElementalAuraState;
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
import net.minecraft.world.entity.LivingEntity;
//? if >=26.3 {
/*import net.minecraft.world.entity.boss.enderdragon.EnderDragonPart;
*///?} else {
import net.minecraft.world.entity.boss.EnderDragonPart;
//?}
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
    private static final Map<CarrierData.Element, ResourceKey<DamageType>> REACTION_TYPES = createReactionDamageTypes();

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

    private static Map<CarrierData.Element, ResourceKey<DamageType>> createReactionDamageTypes() {
        Map<CarrierData.Element, ResourceKey<DamageType>> result = new EnumMap<>(CarrierData.Element.class);
        for (CarrierData.Element element : CarrierData.Element.values()) {
            String id = "reaction_" + element.name().toLowerCase(Locale.ROOT);
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
        CarrierData data = CarrierData.read(meleeCarrier(weapon, player));
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

    /** Resolve the exact bound carrier; a switched weapon never changes an in-flight arrow. */
    public static int reactionMastery(Player player, DamageSource source) {
        CarrierData data = CarrierData.read(reactingCarrier(player, source));
        return data == null ? 0 : data.elementalMastery();
    }

    public static ElementalReactionEngine.Mode reactionMode(Player player, DamageSource source) {
        ItemStack carrier = reactingCarrier(player, source);
        if (carrier.isEmpty()) return ElementalReactionEngine.Mode.NORMAL;
        for (CarrierData.Element element : CarrierData.Element.values()) {
            if (carrier.is(CarrierItems.MOON_WHEELS.get(element).get())) {
                return ElementalReactionEngine.Mode.LUNAR;
            }
            if (carrier.is(CarrierItems.STAR_WEDGES.get(element).get())) {
                return ElementalReactionEngine.Mode.STELLAR;
            }
        }
        return ElementalReactionEngine.Mode.NORMAL;
    }

    private static ItemStack reactingCarrier(Player player, DamageSource source) {
        ItemStack carrier = ItemStack.EMPTY;
        if (source.getDirectEntity() instanceof Projectile projectile
                && projectile instanceof ElementalProjectile bound) {
            UUID original = bound.genshinecraft$carrierId();
            if (original != null) {
                var inventory = player.getInventory();
                for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
                    ItemStack stack = inventory.getItem(slot);
                    CarrierData data = CarrierData.read(stack);
                    if (data != null && original.equals(data.id()) && data.usableBy(player.getUUID())) {
                        carrier = stack;
                        break;
                    }
                }
            }
        } else {
            carrier = meleeCarrier(player.getMainHandItem(), player);
        }
        return carrier;
    }

    /** Accept both an owned organ wielded directly and a weapon bound to an owned organ. */
    private static ItemStack meleeCarrier(ItemStack held, Player player) {
        CarrierData data = CarrierData.read(held);
        return data != null && data.usableBy(player.getUUID())
                ? held : WeaponBinding.resolveStack(held, player);
    }

    /** Reaction damage has its own element color and attributes to its original player. */
    public static DamageSource reactionSource(ServerLevel level, CarrierData.Element element, Player attacker) {
        ResourceKey<DamageType> type = REACTION_TYPES.get(element);
        //? if >=26.3 {
        /*var holder = level.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE)
                .getOrThrow(type);
        *///?} else {
        var holder = level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
                .getHolderOrThrow(type);
        //?}
        return new DamageSource(holder, attacker);
    }

    /** Mob elemental attack: one damage type and true mob attribution (including world-level scaling). */
    public static DamageSource mobSource(ServerLevel level, CarrierData.Element element, Entity attacker) {
        var key = ATTACK_TYPES.get(element);
        //? if >=26.3 {
        /*var holder = level.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(key);
        *///?} else {
        var holder = level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(key);
        //?}
        return new DamageSource(holder, attacker);
    }

    /** Minecraft's Ender Dragon exposes non-living hit parts. Their auras belong to the dragon. */
    private static LivingEntity auraTarget(Entity hitEntity) {
        if (hitEntity instanceof LivingEntity living) return living;
        if (hitEntity instanceof EnderDragonPart part) return part.parentMob;
        return null;
    }

    /** Modify the original attack amount before Minecraft applies armor and resistance. */
    public static float reactionDamage(Player player, Entity target, DamageSource source,
                                       float amount, String attackGroup) {
        LivingEntity living = auraTarget(target);
        if (!(player.level() instanceof ServerLevel) || living == null || amount <= 0) {
            return amount;
        }
        CarrierData.Element incoming = sourceElement(source);
        var state = ((ElementalAuraHolder) living).genshinecraft$auras();
        if (incoming == null) {
            // Shatter and Superconduct modify physical weapon damage; no bonus to elements.
            float multiplier = state.isSuperconductActive() ? 1.30f : 1.0f;
            if (state.isStellarFieldActive()) multiplier *= 1.40f;
            if (state.isFrozen()) {
                multiplier *= 1.25f;
            }
            return amount * multiplier;
        }
        if (!state.canApply(player.getUUID(), incoming, attackGroup, living.tickCount)) {
            return amount * state.stellarElementBonus(incoming);
        }
        var plan = ElementalReactionEngine.preview(state, incoming, reactionMode(player, source));
        int mastery = reactionMastery(player, source);
        float damage = switch (plan.reaction()) {
            case VAPORIZE, MELT -> ElementalReactionEffects.amplifiedDamage(
                    amount, plan.damageMultiplier(), mastery);
            case AGGRAVATE -> amount + ElementalReactionEffects.additiveBonus(2f, mastery);
            case SPREAD -> amount + ElementalReactionEffects.additiveBonus(2.4f, mastery);
            default -> amount * plan.damageMultiplier();
        };
        return damage * state.stellarElementBonus(incoming);
    }

    public static CarrierData.Element sourceElement(DamageSource source) {
        for (CarrierData.Element element : CarrierData.Element.values()) {
            if (source.is(ATTACK_TYPES.get(element)) || source.is(REACTION_TYPES.get(element))) {
                return element;
            }
        }
        return null;
    }

    public static void onMeleeHit(Player player, Entity target, DamageSource source) {
        spawnHitParticles(player, target, source, "normal_melee");
        // Only directly wielding an owned Pyro organ ignites on a successful melee hit.
        CarrierData organ = CarrierData.read(player.getMainHandItem());
        if (organ != null && organ.usableBy(player.getUUID())
                && organ.element() == CarrierData.Element.PYRO
                && source.is(ATTACK_TYPES.get(CarrierData.Element.PYRO))
                && target instanceof LivingEntity living && living.isAlive()) {
            //? if =1.20.1 {
            /*living.setSecondsOnFire(4);
            *///?} else {
            living.igniteForSeconds(4);
            //?}
        }
    }

    public static void onProjectileHit(Projectile projectile, Entity target, DamageSource source) {
        if (projectile.getOwner() instanceof Player player) {
            spawnHitParticles(player, target, source, "normal_projectile");
        }
    }

    private static void spawnHitParticles(Player player, Entity target, DamageSource source,
                                          String attackGroup) {
        if (!(player.level() instanceof ServerLevel server)) {
            return;
        }
        LivingEntity living = auraTarget(target);
        if (sourceElement(source) == null && living != null) {
            ElementalReactionEffects.onPhysicalHit(server, living);
            ElementalReactionEffects.moonCageAttacked(server, living);
            return;
        }
        for (CarrierData.Element element : CarrierData.Element.values()) {
            if (!source.is(ATTACK_TYPES.get(element))) {
                continue;
            }
            if (living != null && living.isAlive()) {
                ElementalReactionEffects.onHit(server, player, living, element, attackGroup,
                        reactionMode(player, source), reactionMastery(player, source));
                ElementalReactionEffects.moonCageAttacked(server, living);
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
