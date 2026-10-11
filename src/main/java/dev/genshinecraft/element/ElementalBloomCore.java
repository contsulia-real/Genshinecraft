package dev.genshinecraft.element;

import com.mojang.math.Transformation;
import dev.genshinecraft.combat.ElementalCombat;
import dev.genshinecraft.item.CarrierData.Element;
import dev.genshinecraft.mixin.DamageNumberDisplayAccessor;
import dev.genshinecraft.mixin.DamageNumberTextAccessor;
import dev.genshinecraft.particle.ElementalParticles;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.List;
import java.util.UUID;

/**
 * An independent, persisted display entity standing in for a Dendro Core.
 * 6-second Bloom expiry; nearby Pyro/Electro hits can react with it.
 * Not yet a directly targetable projectile entity.
 */
public final class ElementalBloomCore {
    private static final String PREFIX = "genshinecraft:bloom_core:";
    private static final int MAX_NEARBY = 5;
    private static final int CORE_LIFETIME = 120;

    private ElementalBloomCore() {
    }

    public static boolean isCore(Display display) {
        return display instanceof Display.TextDisplay && display.getCustomName() != null
                && display.getCustomName().getString().startsWith(PREFIX);
    }

    public static void spawn(ServerLevel level, LivingEntity target, Player owner) {
        AABB range = target.getBoundingBox().inflate(7.0);
        List<Display.TextDisplay> nearby = cores(level, range);
        if (nearby.size() >= MAX_NEARBY) {
            detonate(level, nearby.get(0), null, 2.0f, 2.5);
        }
        //? if >=26.3 {
        /*Display.TextDisplay core = new Display.TextDisplay(net.minecraft.world.entity.EntityTypes.TEXT_DISPLAY, level);
        *///?} else {
        Display.TextDisplay core = new Display.TextDisplay(EntityType.TEXT_DISPLAY, level);
        //?}
        double x = target.getX() + (level.getRandom().nextDouble() - 0.5) * 0.7;
        double y = target.getY() + 0.22;
        double z = target.getZ() + (level.getRandom().nextDouble() - 0.5) * 0.7;
        core.setPos(x, y, z);
        core.setNoGravity(true);
        core.setSilent(true);
        core.setCustomName(Component.literal(PREFIX + owner.getUUID()));
        core.setCustomNameVisible(false);
        ((DamageNumberTextAccessor) core).genshinecraft$text(Component.literal("✦")
                .withStyle(style -> style.withColor(0xB4E967)));
        ((DamageNumberTextAccessor) core).genshinecraft$background(0);
        ((DamageNumberDisplayAccessor) core).genshinecraft$billboard(Display.BillboardConstraints.CENTER);
        ((DamageNumberDisplayAccessor) core).genshinecraft$transformation(
                new Transformation(new Vector3f(), new Quaternionf(),
                        new Vector3f(1.10f, 1.10f, 1.10f), new Quaternionf()));
        level.addFreshEntity(core);
        particles(level, x, y, z, 5);
    }

    public static void tick(Display display) {
        if (!(display.level() instanceof ServerLevel level) || !isCore(display)) {
            return;
        }
        if (display.tickCount % 12 == 0) {
            particles(level, display.getX(), display.getY(), display.getZ(), 2);
        }
        if (display.tickCount >= CORE_LIFETIME) {
            detonate(level, (Display.TextDisplay) display, null, 2f, 2.5);
        }
    }

    /** Fire explodes the core; Electro hits the closest other living entity. */
    public static boolean trigger(ServerLevel level, LivingEntity hit, Element incoming) {
        if (incoming != Element.PYRO && incoming != Element.ELECTRO) {
            return false;
        }
        List<Display.TextDisplay> nearby = cores(level, hit.getBoundingBox().inflate(2.8));
        if (nearby.isEmpty()) {
            return false;
        }
        Display.TextDisplay core = nearby.get(0);
        if (incoming == Element.PYRO) {
            detonate(level, core, null, 4f, 3.0);
        } else {
            UUID owner = owner(core);
            LivingEntity selected = hit;
            double nearest = Double.MAX_VALUE;
            for (LivingEntity other : level.getEntitiesOfClass(LivingEntity.class,
                    core.getBoundingBox().inflate(5.0), e -> e.isAlive())) {
                if (other.getUUID().equals(owner)) {
                    continue;
                }
                double distance = other.distanceToSqr(core);
                if (distance < nearest) {
                    nearest = distance;
                    selected = other;
                }
            }
            ElementalReactionEffects.dealDamage(level, selected, Element.DENDRO, owner, 4f);
            level.sendParticles(ElementalParticles.SPARKS.get(Element.DENDRO).get(),
                    selected.getX(), selected.getY() + selected.getBbHeight() * 0.6,
                    selected.getZ(), 12, 0.25, 0.3, 0.25, 0.08);
            core.discard();
        }
        return true;
    }

    private static List<Display.TextDisplay> cores(ServerLevel level, AABB range) {
        return level.getEntitiesOfClass(Display.TextDisplay.class, range, ElementalBloomCore::isCore);
    }

    private static UUID owner(Display core) {
        if (core.getCustomName() != null) {
            try {
                return UUID.fromString(core.getCustomName().getString().substring(PREFIX.length()));
            } catch (IllegalArgumentException ignored) {
                // A malformed persisted marker does not crash the entity tick.
            }
        }
        return null;
    }

    private static void detonate(ServerLevel level, Display.TextDisplay core, LivingEntity ignored,
                                 float damage, double radius) {
        UUID source = owner(core);
        for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class,
                core.getBoundingBox().inflate(radius), e -> e.isAlive())) {
            // Owner is immune to the mod's Bloom splash; separate target if PvP desired.
            if (target.getUUID().equals(source)) {
                continue;
            }
            ElementalReactionEffects.dealDamage(level, target, Element.DENDRO, source, damage);
        }
        particles(level, core.getX(), core.getY(), core.getZ(), 14);
        core.discard();
    }

    private static void particles(ServerLevel level, double x, double y, double z, int count) {
        level.sendParticles(ElementalParticles.SPARKS.get(Element.DENDRO).get(),
                x, y, z, count, 0.25, 0.2, 0.25, 0.04);
    }
}
