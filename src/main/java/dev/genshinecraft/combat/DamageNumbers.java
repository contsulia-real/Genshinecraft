package dev.genshinecraft.combat;

import com.mojang.math.Transformation;
import dev.genshinecraft.item.CarrierData;
import dev.genshinecraft.mixin.DamageNumberDisplayAccessor;
import dev.genshinecraft.mixin.DamageNumberTextAccessor;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Entity;
//? if >=26.3 {
/*import net.minecraft.world.entity.EntityTypes;
*///?} else {
import net.minecraft.world.entity.EntityType;
//?}
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.damagesource.DamageSource;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Locale;
import java.util.Map;
import java.util.WeakHashMap;

/** Vanilla-synchronized floating text: never alters a hit or its damage. */
public final class DamageNumbers {
    public static final String MARKER = "genshinecraft:damage_number";
    private static final int PHYSICAL_COLOR = 0xFFFFFF;
    private static final Map<ServerPlayer, RecentHit> RECENT_MELEE = new WeakHashMap<>();

    private record RecentHit(Entity target, long tick, double x, double y, double z, int color) {
    }

    private DamageNumbers() {
    }

    /** Reads the *actual* lost health and absorption, after vanilla has resolved the hit. */
    public static void afterHit(Player attacker, Entity target, DamageSource source,
                                float beforeHealth, float beforeAbsorption,
                                boolean critical, boolean melee) {
        if (!(attacker instanceof ServerPlayer player) || !(target instanceof LivingEntity living)
                || !(attacker.level() instanceof ServerLevel server)) {
            return;
        }
        float dealt = Math.max(0f, beforeHealth - living.getHealth())
                + Math.max(0f, beforeAbsorption - living.getAbsorptionAmount());
        if (!Float.isFinite(dealt) || dealt < 0.05f) {
            return;
        }
        int color = color(source);
        double x = target.getX() + (Math.random() - 0.5) * 0.25;
        double y = target.getY() + target.getBbHeight() + 0.23;
        double z = target.getZ() + (Math.random() - 0.5) * 0.25;
        String number = Math.abs(dealt - Math.round(dealt)) < 0.05f
                ? Integer.toString(Math.round(dealt))
                : String.format(Locale.ROOT, "%.1f", dealt);
        spawn(server, x, y, z, number, color, false);
        if (critical) {
            spawn(server, x, y, z, "✦", color, true);
        } else if (melee) {
            RECENT_MELEE.put(player, new RecentHit(target, server.getGameTime(), x, y, z, color));
        }
    }

    /** Vanilla ServerPlayer.crit is called only when a real melee critical occurs. */
    public static void onVanillaCritical(ServerPlayer attacker, Entity target) {
        RecentHit hit = RECENT_MELEE.remove(attacker);
        if (hit != null && hit.target() == target
                && attacker.level() instanceof ServerLevel server
                && server.getGameTime() - hit.tick() <= 1) {
            spawn(server, hit.x(), hit.y(), hit.z(), "✦", hit.color(), true);
        }
    }

    public static boolean isOurDisplay(Entity entity) {
        return entity instanceof Display.TextDisplay
                && entity.getCustomName() != null
                && MARKER.equals(entity.getCustomName().getString());
    }

    private static int color(DamageSource source) {
        CarrierData.Element element = ElementalCombat.sourceElement(source);
        if (element == null) {
            return PHYSICAL_COLOR;
        }
        return switch (element) {
            case ANEMO -> 0x73DDB9;
            case GEO -> 0xE8BB65;
            case ELECTRO -> 0xB78DEB;
            case DENDRO -> 0x93CA65;
            case HYDRO -> 0x64B5EF;
            case PYRO -> 0xF08062;
            case CRYO -> 0x9BD9F0;
        };
    }

    private static void spawn(ServerLevel server, double x, double y, double z,
                              String value, int color, boolean icon) {
        //? if >=26.3 {
        /*Display.TextDisplay display = new Display.TextDisplay(EntityTypes.TEXT_DISPLAY, server);
        *///?} else {
        Display.TextDisplay display = new Display.TextDisplay(EntityType.TEXT_DISPLAY, server);
        //?}
        display.setPos(x, y, z);
        display.setNoGravity(true);
        display.setSilent(true);
        display.setCustomName(Component.literal(MARKER));
        display.setCustomNameVisible(false);

        ((DamageNumberTextAccessor) display).genshinecraft$text(
                Component.literal(value).withStyle(style -> style.withColor(color)));
        ((DamageNumberTextAccessor) display).genshinecraft$background(0);
        ((DamageNumberDisplayAccessor) display).genshinecraft$billboard(Display.BillboardConstraints.CENTER);
        float scale = icon ? 0.58f : 1.35f;
        ((DamageNumberDisplayAccessor) display).genshinecraft$transformation(
                new Transformation(
                        new Vector3f(icon ? 0.27f : 0f, icon ? 0.17f : 0f, 0f),
                        new Quaternionf(), new Vector3f(scale, scale, scale), new Quaternionf()));
        server.addFreshEntity(display);
    }
}
