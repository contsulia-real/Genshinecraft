package dev.genshinecraft.element;

import dev.genshinecraft.mixin.DragonFightBossEventAccessor;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * The BossEvent has a separate UUID from the mob. Resolve the ACTUAL event
 * owned by the living boss on the server and sync its UUID via entity data.
 * Most modded bosses hold a ServerBossEvent field; the End dragon instead
 * belongs to the server's dragon fight.
 */
public final class BossBarAssociation {
    private BossBarAssociation() {}

    private static final ClassValue<Field[]> EVENT_FIELDS = new ClassValue<>() {
        @Override
        protected Field[] computeValue(Class<?> type) {
            List<Field> found = new ArrayList<>();
            for (Class<?> cursor = type; cursor != null && cursor != Object.class;
                    cursor = cursor.getSuperclass()) {
                for (Field field : cursor.getDeclaredFields()) {
                    if (Modifier.isStatic(field.getModifiers())
                            || !ServerBossEvent.class.isAssignableFrom(field.getType())) continue;
                    if (field.trySetAccessible()) found.add(field);
                }
            }
            return found.toArray(Field[]::new);
        }
    };

    /** Returns an empty string only when this living entity owns no supported BossBar. */
    public static String id(ServerLevel level, LivingEntity entity) {
        for (Field field : EVENT_FIELDS.get(entity.getClass())) {
            try {
                if (field.get(entity) instanceof ServerBossEvent event && event.isVisible()) {
                    return event.getId().toString();
                }
            } catch (IllegalAccessException ignored) {
                // A field not accessible to this mod cannot supply an association.
            }
        }
        if (entity instanceof EnderDragon dragon && dragon.getDragonFight() != null) {
            // Follow this dragon's own fight reference. The fight's tracked
            // dragon UUID can legitimately lag during spawning or respawning.
            ServerBossEvent bar = ((DragonFightBossEventAccessor) dragon.getDragonFight())
                    .genshinecraft$dragonBar();
            if (bar != null && bar.isVisible()) return bar.getId().toString();
        }
        return "";
    }
}
